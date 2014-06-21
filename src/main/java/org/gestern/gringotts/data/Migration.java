package org.gestern.gringotts.data;

import com.avaje.ebean.EbeanServer;
import org.gestern.bukkitmigration.UUIDFetcher;
import org.gestern.gringotts.Gringotts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data migration tools:
 *
 *   * migrate derby to eBean
 *   * migrate player names to uuids
 */
public class Migration {

    private final EbeanServer db = Gringotts.G.getDatabase();
    private final Logger log = Gringotts.G.getLogger();

    private final File gringottsFolder = Gringotts.G.getDataFolder();
    private final File derbyMigratedFlag = new File(gringottsFolder, ".derby-migrated");
    private final File uuidsMigratedFlag = new File(Gringotts.G.getDataFolder(), ".uuids-migrated");

    /** Return whether the legacy derby db has been migrated to bukkit built-in sqlite / ebean. */
    public boolean isDerbyMigrated() {
        return derbyMigratedFlag.exists();
    }

    /** Return whether player names have been migrated to uuids. */
    public boolean isUUIDMigrated() {
        // use a special file as marker that uuids have been migrated
        return uuidsMigratedFlag.exists();
    }

    /** Perform migration of player names in db to uuids. */
    public void doUUIDMigration() {

        // create backup copy of Gringotts.db
        File dbFile = new File(gringottsFolder, "Gringotts.db");
        File dbBackup = new File(gringottsFolder, "Gringotts.db.bak");
        if (dbFile.exists() && !dbBackup.exists()) {
            // only create backup once, we don't want a bad file overwriting it
            try {
                Files.copy(dbFile.toPath(), dbBackup.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
            } catch (IOException err) {
                log.log(Level.WARNING, "unable to create backup of Gringotts.db. Aborting UUID migration.", err);
            }
            log.info("Created backup of Gringotts database as Gringotts.db.bak");
        }

        // only players need to be updated
        List<EBeanAccount> accounts = db.find(EBeanAccount.class).where().eq("type","player").findList();
        List<String> names = new LinkedList<>();
        for (EBeanAccount account : accounts) {
            String owner = account.getOwner();
            try { UUID ignored = UUID.fromString(owner); }
            catch(IllegalArgumentException ignored) {
                // when owner string is not a valid uuid, do the migration thing
                String name = account.getOwner();
                names.add(name);
            }
        }

        Map<String, UUID> nameUUIDs;
        try {
            nameUUIDs = new UUIDFetcher(names, true).call();
        } catch(Exception err) {
            log.log(Level.WARNING, "Could not migrate player accounts to UUIDS.", err);
            return;
        }


        try {
            // either update all, or nothing
            db.beginTransaction();
            for (EBeanAccount account : accounts) {
                String name = account.getOwner();
                if (nameUUIDs.containsKey(name)) {
                    account.setOwner(nameUUIDs.get(name).toString());
                    db.update(account);
                } else {
                    log.info("No UUID found for player " + name);
                }
            }
            db.commitTransaction();
            db.endTransaction();
        } catch (Exception err) {
            log.log(Level.WARNING,
                    "Unable to update names to UUIDs. Please shutdown server and replace Gringotts.db with Gringotts.db.bak",
                    err);
            return;
        }

        try {
            uuidsMigratedFlag.createNewFile();
            log.info("Players to UUIDs database migration complete.");
        } catch (IOException err) {
            log.log(Level.SEVERE,
                    "Failed to set uuid migration complete flag (but it probably completed anyway)",
                    err);
        }
    }

    /** Migrate an existing Derby DB to Bukkit-internal EBean. */
    public void doDerbyMigration(DerbyDAO derbyDAO, EBeanDAO eBeanDAO) {
        log.info("Reading account data from Derby database ...");
        List<DerbyDAO.DerbyAccount> accounts = derbyDAO.getAccountsRaw();
        List<DerbyDAO.DerbyAccountChest> chests = derbyDAO.getChestsRaw();

        db.beginTransaction();
        for (DerbyDAO.DerbyAccount da : accounts) {
            EBeanAccount acc = new EBeanAccount();
            acc.setOwner(da.owner);
            acc.setType(da.type);
            acc.setCents(da.cents);

            db.insert(acc);
        }

        for (DerbyDAO.DerbyAccountChest dac : chests) {
            EBeanAccountChest chest = new EBeanAccountChest();
            chest.setId(dac.id);
            chest.setWorld(dac.world);
            chest.setX(dac.x);
            chest.setY(dac.y);
            chest.setZ(dac.z);
            chest.setAccount(dac.account);

            db.insert(chest);
        }
        db.commitTransaction();


        try {
            derbyMigratedFlag.createNewFile();
            log.info("Wrote account data to EBean-supported database. Migration complete.");
        } catch (IOException err) {
            log.log(Level.SEVERE,
                    "Failed to set derby migration complete flag (but it probably completed anyway)",
                    err);
        }
    }
}
