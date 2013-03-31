package org.gestern.gringotts.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.SqlUpdate;
import com.avaje.ebean.validation.NotNull;

public class EBeanDAO implements DAO {

    private final EbeanServer db = Gringotts.G.getDatabase();
    private final Logger log = Gringotts.G.getLogger();
    
    private static EBeanDAO dao;

    @Override
    public boolean storeAccountChest(AccountChest chest) {
        SqlUpdate storeChest = db.createSqlUpdate(
                "insert into gringotts_accountchest (world,x,y,z,account) values (:world, :x, :y, :z, (select id from gringotts_account where owner=:owner and type=:type))");
        Sign mark = chest.sign;
        storeChest.setParameter("world", mark.getWorld());
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
                .where().ieq("type", accountHolder.getType()).ieq("name", accountHolder.getName()).findRowCount();
        return accCount == 1;
    }

    @Override
    public Set<AccountChest> getChests() {
        List<SqlRow> result = db.createSqlQuery("SELECT ac.world, ac.x, ac.y, ac.z, a.type, a.owner " +
                "FROM gringotts_accountchest ac JOIN gringotts_account a ON ac.account = a.id ").findList();

        Set<AccountChest> chests = new HashSet<AccountChest>();

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
                "delete from accountchest where world = :world and x = :x and y = :y and z = :z");
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
                "FROM accountchest ac JOIN account a ON ac.account = a.id " +
                "WHERE a.owner = :owner and a.type = :type");

        getChests.setParameter("owner", account.owner.getId());
        getChests.setParameter("type", account.owner.getType());

        Set<AccountChest> chests = new HashSet<AccountChest>();
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
        EBeanAccount acc = new EBeanAccount();
        acc.setOwner(account.owner.getId());
        acc.setType(account.owner.getType());
        acc.setCents(amount);
        db.save(acc);
        // TODO does this do a proper update? or just a new insert?
        return true;
    }

    @Override
    public long getCents(GringottsAccount account) {
        // TODO can this NPE?
        return db.find(EBeanAccount.class)
                .where().ieq("type", account.owner.getType()).ieq("name", account.owner.getName())
                .findUnique().cents;
    }

    @Override
    public void deleteAccount(GringottsAccount acc) {
        // TODO implement deleteAccount, mayhaps?
        throw new RuntimeException("delete account not supported yet in EBeanDAO");
    }

    @Entity
    @Table(name="gringotts_account")
    @UniqueConstraint(columnNames={"type","owner"})
    public static class EBeanAccount {
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public long getCents() {
            return cents;
        }

        public void setCents(long cents) {
            this.cents = cents;
        }

        @Id int id;

        /** Type string. */
        @NotNull String type;

        /** Owner id. */
        @NotNull String owner;

        /** Virtual balance. */
        @NotNull long cents;

    }

    @Entity
    @Table(name="gringotts_accountchest")
    @UniqueConstraint(columnNames={"world","x","y","z"})
    public static class EBeanAccountChest {
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getWorld() {
            return world;
        }

        public void setWorld(String world) {
            this.world = world;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getZ() {
            return z;
        }

        public void setZ(int z) {
            this.z = z;
        }

        public int getAccount() {
            return account;
        }

        public void setAccount(int account) {
            this.account = account;
        }

        @Id int id;

        @NotNull String world;

        @NotNull int x;
        @NotNull int y;
        @NotNull int z;

        @NotNull int account;

    }

    public static List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(EBeanAccount.class);
        list.add(EBeanAccountChest.class);
        return list;
    }

    @Override
    public void shutdown() {
        // probably handled by Bukkit?
    }

}
