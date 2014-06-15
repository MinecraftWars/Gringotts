package org.gestern.gringotts.data;

import com.avaje.ebean.EbeanServer;
import org.gestern.bukkitmigration.UUIDFetcher;
import org.gestern.gringotts.Gringotts;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Data migration tools:
 *
 *   * migrate derby to eBean
 *   * migrate player names to uuids
 */
public class Migration {

    private final EbeanServer db = Gringotts.G.getDatabase();
    private final Logger logger = Gringotts.G.getLogger();

    /** Return whether the legacy derby db has been migrated to bukkit built-in sqlite / ebean. */
    public boolean isDerbyMigrated() {
        return false; // TODO derby migration
    }

    /** Return whether player names have been migrated to uuids. */
    public boolean isUUIDMigrated() {
        // use a special file as marker that uuids have been migrated
        return new File(Gringotts.G.getDataFolder(),".uuids-migrated").exists();
    }

    /** Perform migration of player names in db to uuids. */
    public void doUUIDMigration() {

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

        try {
            Map<String,UUID> nameUUIDs = new UUIDFetcher(names).call();
            // either update all, or nothing
            db.beginTransaction();
            for (EBeanAccount account : accounts) {
                String name = account.getOwner();
                if (nameUUIDs.containsKey(name)) {
                    account.setOwner(nameUUIDs.get(name).toString());
                    db.update(account);
                }
            }
            db.commitTransaction();
            db.endTransaction();
        } catch (Exception e) {
            logger.severe("Unable to update names to UUIDs for player names: " + Arrays.toString(names.toArray()));
            throw new RuntimeException(e);
        }

        try {
            new File(Gringotts.G.getDataFolder(), ".uuids-migrated").createNewFile();
            logger.info("Players to UUIDs database migration complete.");
        } catch (IOException e) {
            logger.severe("Failed to set uuid migration complete flag, but it probably completed anyway. Error was:" +  e.getMessage());
        }
    }
}
