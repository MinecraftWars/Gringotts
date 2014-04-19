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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class EBeanDAO implements DAO {

    private final EbeanServer db = Gringotts.G.getDatabase();
    private final Logger log = Gringotts.G.getLogger();

    private static EBeanDAO dao;

    @Override
    public boolean storeAccountChest(AccountChest chest) {
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
    public boolean destroyAccountChest(AccountChest chest) {
        Sign mark = chest.sign;
        return deleteAccountChest(mark.getWorld().getName(), mark.getX(), mark.getY(), mark.getZ());
    }

    @Override
    public boolean storeAccount(GringottsAccount account) {
        
        if (hasAccount(account.owner))
            return false;
        
        EBeanAccount acc = new EBeanAccount();
        acc.setOwner(account.owner.getId());
        acc.setType(account.owner.getType());
        acc.setCents(0);
        db.save(acc);
        return true;
    }

    @Override
    public boolean hasAccount(AccountHolder accountHolder) {
        int accCount = db.find(EBeanAccount.class)
                .where().ieq("type", accountHolder.getType()).ieq("owner", accountHolder.getId()).findRowCount();
        return accCount == 1;
    }

    @Override
    public Set<AccountChest> getChests() {
        List<SqlRow> result = db.createSqlQuery("SELECT ac.world, ac.x, ac.y, ac.z, a.type, a.owner " +
                "FROM gringotts_accountchest ac JOIN gringotts_account a ON ac.account = a.id ").findList();

        Set<AccountChest> chests = new HashSet<>();

        for (SqlRow c : result) {
            String worldName = c.getString("world");
            int x = c.getInteger("x");
            int y = c.getInteger("y");
            int z = c.getInteger("z");

            String type = c.getString("type");
            String ownerId = c.getString("owner");

            World world = Bukkit.getWorld(worldName);
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

    public static DAO getDao() {
        if (dao!=null) return dao;
        dao = new EBeanDAO();
        return dao;
    }

    @Override
    public Set<AccountChest> getChests(GringottsAccount account) {
        SqlQuery getChests = db.createSqlQuery("SELECT ac.world, ac.x, ac.y, ac.z " +
                "FROM gringotts_accountchest ac JOIN gringotts_account a ON ac.account = a.id " +
                "WHERE a.owner = :owner and a.type = :type");

        getChests.setParameter("owner", account.owner.getId());
        getChests.setParameter("type", account.owner.getType());

        Set<AccountChest> chests = new HashSet<>();
        for (SqlRow result : getChests.findSet()) {
            String worldName = result.getString("world");
            int x = result.getInteger("x");
            int y = result.getInteger("y");
            int z = result.getInteger("z");

            World world = Bukkit.getWorld(worldName);
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
    public boolean storeCents(GringottsAccount account, long amount) {
        SqlUpdate up = db.createSqlUpdate("UPDATE gringotts_account SET cents = :cents WHERE owner = :owner and type = :type");
        up.setParameter("cents", amount);
        up.setParameter("owner", account.owner.getId());
        up.setParameter("type", account.owner.getType());
        
        return up.execute() == 1;
    }

    @Override
    public long getCents(GringottsAccount account) {
        // TODO can this NPE?
        return db.find(EBeanAccount.class)
                .where().ieq("type", account.owner.getType()).ieq("owner", account.owner.getId())
                .findUnique().cents;
    }

    @Override
    public void deleteAccount(GringottsAccount acc) {
        // TODO implement deleteAccount, mayhaps?
        throw new RuntimeException("delete account not supported yet in EBeanDAO");
    }

    public static List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<>();
        list.add(EBeanAccount.class);
        list.add(EBeanAccountChest.class);
        return list;
    }

    @Override
    public void shutdown() {
        // probably handled by Bukkit?
    }

}
