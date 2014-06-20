package org.gestern.gringotts.data;

import com.avaje.ebean.EbeanServer;
import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.Profile;
import com.mojang.api.profiles.ProfileRepository;
import org.gestern.gringotts.Gringotts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
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
    private final File derbyMigratedFlag = new File(gringottsFolder,".derby-migrated");
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
        if (!dbBackup.exists()) {
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

        Map<String, String> nameUUIDs = new HashMap<>();;
        try {
            ProfileRepository profileRepo = new HttpProfileRepository("minecraft");
            Profile[] profiles = profileRepo.findProfilesByNames(names.toArray(new String[names.size()]));
            for (Profile p : profiles)
                nameUUIDs.put(p.getName(), p.getId());

        } catch(Exception err) {
            log.log(Level.WARNING, "Could not migrate player accounts to UUIDS: Unable to communicate with Mojang server.", err);
            return;
        }


        try {
            // either update all, or nothing
            db.beginTransaction();
            for (EBeanAccount account : accounts) {
                String name = account.getOwner();
                if (nameUUIDs.containsKey(name)) {
                    account.setOwner(nameUUIDs.get(name));
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
            Files.setAttribute(uuidsMigratedFlag.toPath(), "dos:hidden", true);
            log.info("Players to UUIDs database migration complete.");
        } catch (IOException err) {
            log.log(Level.SEVERE,
                    "Failed to set uuid migration complete flag (but it probably completed anyway)",
                    err);
        }
    }

    /** Migrate an existing Derby DB to Bukkit-internal EBean. */
    public void doDerbyMigration() {
        // TODO derby migration
    }
}
