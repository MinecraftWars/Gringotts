package org.gestern.gringotts.data;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.SqlUpdate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.gestern.gringotts.AccountChest;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.GringottsAccount;
import org.gestern.gringotts.Util;
import org.gestern.gringotts.accountholder.AccountHolder;

import java.util.*;
import java.util.logging.Logger;

import static org.gestern.gringotts.Configuration.CONF;

public class EBeanDAO implements DAO {

    private final EbeanServer db = Gringotts.G.getDatabase();
    private final Logger log = Gringotts.G.getLogger();

    private static EBeanDAO dao;

    @Override
    public synchronized boolean storeAccountChest(AccountChest chest) {
        SqlUpdate storeChest = db.createSqlUpdate(
                "insert into gringotts_accountchest (world,x,y,z,account) values (:world, :x, :y, :z, (select id from gringotts_account where owner=:owner and type=:type))");
        Sign mark = chest.sign;
        storeChest.setParameter("world", mark.getWorld().getName());
        storeChest.setParameter("x", mark.getX());
        storeChest.setParameter("y", mark.getY());
        storeChest.setParameter("z", mark.getZ());
        storeChest.setParameter("owner", chest.account.owner.getId());
        storeChest.setParameter("type", chest.account.owner.getType());

        return storeChest.execute() > 0;
    }

    @Override
    public synchronized boolean destroyAccountChest(AccountChest chest) {
        Sign mark = chest.sign;
        return deleteAccountChest(mark.getWorld().getName(), mark.getX(), mark.getY(), mark.getZ());
    }

    @Override
    public synchronized boolean storeAccount(GringottsAccount account) {
        if (hasAccount(account.owner))
            return false;
        
        EBeanAccount acc = new EBeanAccount();
        acc.setOwner(account.owner.getId());
        acc.setType(account.owner.getType());

        // TODO this is business logic and should probably be outside of the DAO implementation.
        // also find a more elegant way of handling different account types
        double startValue = 0;
        String type = account.owner.getType();
        switch (type) {
            case "player":
                startValue = CONF.startBalancePlayer;
                break;
            case "faction":
                startValue = CONF.startBalanceFaction;
                break;
            case "town":
                startValue = CONF.startBalanceTown;
                break;
            case "nation":
                startValue = CONF.startBalanceNation;
                break;
        }

        acc.setCents(CONF.currency.centValue(startValue));
        db.save(acc);
        return true;
    }

    @Override
    public synchronized boolean hasAccount(AccountHolder accountHolder) {
        int accCount = db.find(EBeanAccount.class)
                .where().ieq("type", accountHolder.getType()).ieq("owner", accountHolder.getId()).findRowCount();
        return accCount == 1;
    }

    @Override
    public synchronized List<AccountChest> getChests() {
        List<SqlRow> result = db.createSqlQuery("SELECT ac.world, ac.x, ac.y, ac.z, a.type, a.owner " +
                "FROM gringotts_accountchest ac JOIN gringotts_account a ON ac.account = a.id ").findList();

        List<AccountChest> chests = new LinkedList<>();

        for (SqlRow c : result) {
            String worldName = c.getString("world");
            int x = c.getInteger("x");
            int y = c.getInteger("y");
            int z = c.getInteger("z");

            String type = c.getString("type");
            String ownerId = c.getString("owner");

            World world = Bukkit.getWorld(worldName);
            if (world == null) continue; // skip vaults in non-existing worlds

            Location loc = new Location(world, x, y, z);
            Block signBlock = loc.getBlock();
            if (Util.isSignBlock(signBlock)) {
                AccountHolder owner = Gringotts.G.accountHolderFactory.get(type, ownerId);
                if (owner == null) {
                    log.info("AccountHolder "+type+":"+ownerId+" is not valid. Deleting associated account chest at " + signBlock.getLocation());
                    deleteAccountChest(signBlock.getWorld().getName(), signBlock.getX(), signBlock.getY(), signBlock.getZ());
                } else {
                    GringottsAccount ownerAccount = new GringottsAccount(owner);
                    Sign sign = (Sign) signBlock.getState();
                    chests.add(new AccountChest(sign, ownerAccount));
                }
            } else {
                // remove accountchest from storage if it is not a valid chest
                deleteAccountChest(worldName, x, y, z);
            }
        }

        return chests;
    }

    private boolean deleteAccountChest(String world, int x, int y, int z) {
        SqlUpdate deleteChest = db.createSqlUpdate(
                "delete from gringotts_accountchest where world = :world and x = :x and y = :y and z = :z");
        deleteChest.setParameter("world", world);
        deleteChest.setParameter("x", x);
        deleteChest.setParameter("y", y);
        deleteChest.setParameter("z", z);

        return deleteChest.execute() > 0;
    }

    public static EBeanDAO getDao() {
        if (dao!=null) return dao;
        dao = new EBeanDAO();
        return dao;
    }

    @Override
    public synchronized List<AccountChest> getChests(GringottsAccount account) {
        // TODO ensure world interaction is done in sync task
        SqlQuery getChests = db.createSqlQuery("SELECT ac.world, ac.x, ac.y, ac.z " +
                "FROM gringotts_accountchest ac JOIN gringotts_account a ON ac.account = a.id " +
                "WHERE a.owner = :owner and a.type = :type");

        getChests.setParameter("owner", account.owner.getId());
        getChests.setParameter("type", account.owner.getType());

        List<AccountChest> chests = new LinkedList<>();
        for (SqlRow result : getChests.findSet()) {
            String worldName = result.getString("world");
            int x = result.getInteger("x");
            int y = result.getInteger("y");
            int z = result.getInteger("z");

            World world = Bukkit.getWorld(worldName);
            if (world==null) continue; // skip chest if it is in non-existent world
            Location loc = new Location(world, x, y, z);

            Block signBlock = loc.getBlock();
            if (Util.isSignBlock(signBlock)) {
                Sign sign = (Sign) loc.getBlock().getState();
                chests.add(new AccountChest(sign, account));
            } else {
                // remove accountchest from storage if it is not a valid chest
                deleteAccountChest(worldName, x, y, z);
            }
        }

        return chests;
    }

    @Override
    public synchronized boolean storeCents(GringottsAccount account, long amount) {
        SqlUpdate up = db.createSqlUpdate("UPDATE gringotts_account SET cents = :cents WHERE owner = :owner and type = :type");
        up.setParameter("cents", amount);
        up.setParameter("owner", account.owner.getId());
        up.setParameter("type", account.owner.getType());
        
        return up.execute() == 1;
    }

    @Override
    public synchronized long getCents(GringottsAccount account) {
        // can this NPE? (probably doesn't)
        return db.find(EBeanAccount.class)
                .where().ieq("type", account.owner.getType()).ieq("owner", account.owner.getId())
                .findUnique().cents;
    }

    @Override
    public synchronized void deleteAccount(GringottsAccount acc) {
        // TODO implement deleteAccount, mayhaps?
        throw new RuntimeException("delete account not supported yet in EBeanDAO");
    }

    /** The classes comprising the DB model, required for the EBean DDL ("data description language"). */
    public static List<Class<?>> getDatabaseClasses() {
        return Arrays.asList(EBeanAccount.class, EBeanAccountChest.class);
    }

    @Override
    public synchronized void shutdown() {
        // probably handled by Bukkit?
    }

}
