package org.gestern.gringotts.data;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.gestern.gringotts.AccountChest;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.GringottsAccount;
import org.gestern.gringotts.GringottsStorageException;
import org.gestern.gringotts.Util;
import org.gestern.gringotts.accountholder.AccountHolder;
import static org.gestern.gringotts.Configuration.CONF;

/**
 * The Data Access Object provides access to the datastore.
 * This implementation uses the Apache Derby embedded DB.
 * 
 * @author jast
 *
 */
public class DerbyDAO implements DAO {

    /** Singleton DAO instance. */
    private static DerbyDAO dao;

    private final Logger log = Gringotts.G.getLogger();

    private final Driver driver;
    private Connection connection;

    private PreparedStatement 
    storeAccountChest, destroyAccountChest, 
    storeAccount, getAccount, getChests, 
    getChestsForAccount, getCents, storeCents;

    private static final String dbName = "GringottsDB";

    /** Full connection string for database, without connect options. */
    private final String dbString;

    private DerbyDAO() {

        String dbPath = Gringotts.G.getDataFolder().getAbsolutePath();
        dbString = "jdbc:derby:" + dbPath+"/"+dbName;
        String connectString = dbString + ";create=true";

        try {
            driver = DriverManager.getDriver(connectString);
            connection = driver.connect(connectString, null);

            checkConnection();
            setupDB(connection);
            prepareStatements();

            log.fine("DAO setup successfully.");

        } catch (SQLException e) {
            throw new GringottsStorageException("Failed to initialize database connection.", e);
        }

    };

    /**
     * Configure DB for use with gringotts, if it isn't already.
     * @param connection Connection to the db
     * @throws SQLException 
     */
    private void setupDB(Connection connection) throws SQLException {

        // create tables only if they don't already exist. use metadata to determine this.
        DatabaseMetaData dbmd = connection.getMetaData();

        ResultSet rs1 = dbmd.getTables(null, null, "ACCOUNT", null);
        if(!rs1.next()) {
            String createAccount = 
                    "create table account (" +
                            "id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                            "type varchar(64), owner varchar(64), cents int not null, " +
                            "primary key (id), constraint unique_type_owner unique(type, owner))";

            int updated = connection.createStatement().executeUpdate(createAccount);
            if (updated > 0)
                log.info("created table ACCOUNT");
        }

        ResultSet rs2 = dbmd.getTables(null, null, "ACCOUNTCHEST", null);
        if(!rs2.next()) {
            String createAccountChest =        
                    "create table accountchest (" +
                            "id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                            "world varchar(64), x integer, y integer, z integer, account integer not null, " + 
                            "primary key(id), constraint unique_location unique(world,x,y,z), constraint fk_account foreign key(account) references account(id))";

            int updated = connection.createStatement().executeUpdate(createAccountChest);
            if (updated > 0)
                log.info("created table ACCOUNTCHEST");
        }
    }

    /**
     * Prepare sql statements for use in DAO.
     * 
     * @throws SQLException
     */
    private void prepareStatements() throws SQLException {

        storeAccountChest = connection.prepareStatement(
                "insert into accountchest (world,x,y,z,account) values (?, ?, ?, ?, (select id from account where owner=? and type=?))");
        destroyAccountChest = connection.prepareStatement(
                "delete from accountchest where world = ? and x = ? and y = ? and z = ?");
        storeAccount = connection.prepareStatement(
                "insert into account (type, owner, cents) values (?,?,?)");
        getAccount = connection.prepareStatement(
                "select * from account where owner = ? and type = ?");
        getChests = connection.prepareStatement(
                "SELECT ac.world, ac.x, ac.y, ac.z, a.type, a.owner " +
                "FROM accountchest ac JOIN account a ON ac.account = a.id ");
        getChestsForAccount = connection.prepareStatement(
                "SELECT ac.world, ac.x, ac.y, ac.z " +
                        "FROM accountchest ac JOIN account a ON ac.account = a.id " +
                "WHERE a.owner = ? and a.type = ?");
        getCents = connection.prepareStatement(
                "SELECT cents FROM account WHERE owner = ? and type = ?");
        storeCents = connection.prepareStatement(
                "UPDATE account SET cents = ? WHERE owner = ? and type = ?");
    }

    private void checkConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = driver.connect(dbString, null);
            prepareStatements();
            log.warning("Database connection lost. Reinitialized DB.");
        }
    }


    /* (non-Javadoc)
     * @see org.gestern.gringotts.data.DAO#storeAccountChest(org.gestern.gringotts.AccountChest)
     */
    @Override
    public boolean storeAccountChest(AccountChest chest) {
        GringottsAccount account = chest.getAccount();
        Location loc = chest.sign.getLocation();

        log.info("storing account chest: " + chest + " for account: " + account);
        try {
            checkConnection();

            storeAccountChest.setString(1, loc.getWorld().getName());
            storeAccountChest.setInt(2, loc.getBlockX());
            storeAccountChest.setInt(3, loc.getBlockY());
            storeAccountChest.setInt(4, loc.getBlockZ());
            storeAccountChest.setString(5, account.owner.getId());
            storeAccountChest.setString(6, account.owner.getType());

            int updated = storeAccountChest.executeUpdate();
            return updated > 0;
        } catch (SQLException e) {
            // unique constraint failed: chest already exists
            if (e.getErrorCode() == 23505) {
                log.warning("Unable to store account chest: " + e.getMessage());
                return false;
            }

            // other more serious error probably
            throw new GringottsStorageException("Failed to store account chest: " + chest, e);
        }
    }

    /* (non-Javadoc)
     * @see org.gestern.gringotts.data.DAO#destroyAccountChest(org.gestern.gringotts.AccountChest)
     */
    @Override
    public boolean destroyAccountChest(AccountChest chest) {
        Location loc = chest.sign.getLocation();
        try {
            checkConnection();

            return deleteAccountChest(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        } catch (SQLException e) {
            throw new GringottsStorageException("Failed to delete account chest: " + chest, e);
        }
    }

    private boolean deleteAccountChest(String world, int x, int y, int z) throws SQLException {
        destroyAccountChest.setString(1, world);
        destroyAccountChest.setInt(2, x);
        destroyAccountChest.setInt(3, y);
        destroyAccountChest.setInt(4, z);

        int updated = destroyAccountChest.executeUpdate();
        return updated > 0;
    }

    /* (non-Javadoc)
     * @see org.gestern.gringotts.data.DAO#storeAccount(org.gestern.gringotts.GringottsAccount)
     */
    @Override
    public boolean storeAccount(GringottsAccount account) {
        AccountHolder owner = account.owner;

        // don't store/overwrite if it's already there
        if (hasAccount(owner))
            return false;

        try {
            checkConnection();

            storeAccount.setString(1, owner.getType());
            storeAccount.setString(2, owner.getId());

            // TODO this is business logic and should probably be outside of the DAO implementation.
            // also find a more elegant way of handling different account types
            double value = 0;
            String type = account.owner.getType();
            if (type.equals("player"))
                value = CONF.startBalancePlayer;
            else if (type.equals("faction"))
                value = CONF.startBalanceFaction;
            else if (type.equals("town"))
                value = CONF.startBalanceTown;
            else if (type.equals("nation"))
                value = CONF.startBalanceNation;
            storeAccount.setLong(3, CONF.currency.centValue(value));

            int updated = storeAccount.executeUpdate();
            return updated > 0;
        } catch (SQLException e) {
            throw new GringottsStorageException("Failed to store account: " + account, e);
        }
    }


    /* (non-Javadoc)
     * @see org.gestern.gringotts.data.DAO#getAccount(org.gestern.gringotts.accountholder.AccountHolder)
     */
    @Override
    public boolean hasAccount(AccountHolder accountHolder) {

        ResultSet result = null;
        try {
            checkConnection();

            getAccount.setString(1, accountHolder.getId());
            getAccount.setString(2, accountHolder.getType());

            result = getAccount.executeQuery();
            return result.next();

        } catch (SQLException e) {
            throw new GringottsStorageException("Failed to get account for owner: " + accountHolder, e);
        } finally {
            try { if (result!=null) result.close(); } catch (SQLException e) {}
        }
    }


    /* (non-Javadoc)
     * @see org.gestern.gringotts.data.DAO#getChests()
     */
    @Override
    public Set<AccountChest> getChests() {
        Set<AccountChest> chests = new HashSet<AccountChest>();
        ResultSet result = null;
        try {
            checkConnection();

            result = getChests.executeQuery();

            while (result.next()) {
                String worldName = result.getString("world");
                int x = result.getInt("x");
                int y = result.getInt("y");
                int z = result.getInt("z");

                String type = result.getString("type");
                String ownerId = result.getString("owner");

                World world = Bukkit.getWorld(worldName);
                Location loc = new Location(world, x, y, z);

                if (world == null || loc == null) {
                    AccountHolder owner = Gringotts.G.accountHolderFactory.get(type, ownerId);
                    deleteAccountChest(worldName, x, y, x); // FIXME: Isn't actually removing the non-existent vaults..
                    Gringotts.G.getLogger().severe("Vault of " + owner + " located on a non-existent world. Deleteing Vault on world " + worldName);
                    continue;
                }

                Block signBlock = loc.getBlock();
                if (Util.isSignBlock(signBlock)) {
                    AccountHolder owner = Gringotts.G.accountHolderFactory.get(type, ownerId);
                    if (owner == null) {
                        // FIXME this logic really doesn't belong in DAO, I think?
                        log.info("AccountHolder "+type+":"+ownerId+" is not valid. Deleting associated account chest at " + signBlock.getLocation());
                        deleteAccountChest(signBlock.getWorld().getName(), signBlock.getX(), signBlock.getY(), signBlock.getZ());
                    } else {
                        GringottsAccount ownerAccount = new GringottsAccount(owner);
                        Sign sign = (Sign) signBlock.getState();
                        chests.add(new AccountChest(sign, ownerAccount));
                    }
                } else {
                    // remove accountchest from storage if it is not a valid chest
                    deleteAccountChest(signBlock.getWorld().getName(), signBlock.getX(), signBlock.getY(), signBlock.getZ());
                }
            }
        } catch (SQLException e) {
            throw new GringottsStorageException("Failed to get list of all chests", e);
        } finally {
            try { if (result!=null) result.close(); } catch (SQLException e) {}
        }

        return chests;
    }


    /* (non-Javadoc)
     * @see org.gestern.gringotts.data.DAO#getChests(org.gestern.gringotts.GringottsAccount)
     */
    @Override
    public Set<AccountChest> getChests(GringottsAccount account) {

        AccountHolder owner = account.owner;
        Set<AccountChest> chests = new HashSet<AccountChest>();
        ResultSet result = null;
        try {
            checkConnection();

            getChestsForAccount.setString(1, owner.getId());
            getChestsForAccount.setString(2, owner.getType());
            result = getChestsForAccount.executeQuery();

            while (result.next()) {
                String worldName = result.getString("world");
                int x = result.getInt("x");
                int y = result.getInt("y");
                int z = result.getInt("z");

                World world = Bukkit.getWorld(worldName);
                Location loc = new Location(world, x, y, z);

                if (world == null || loc == null) {
                    deleteAccountChest(worldName, x, y, x); // FIXME: Isn't actually removing the non-existent vaults..
                    Gringotts.G.getLogger().severe("Vault of " + account.owner.getName() + " located on a non-existent world. Deleteing Vault on world " + worldName);
                    continue;
                }

                Block signBlock = loc.getBlock();
                if (Util.isSignBlock(signBlock)) {
                    Sign sign = (Sign) loc.getBlock().getState();
                    chests.add(new AccountChest(sign, account));
                } else {
                    // remove accountchest from storage if it is not a valid chest
                    deleteAccountChest(signBlock.getWorld().toString(), signBlock.getX(), signBlock.getY(), signBlock.getZ());
                }
            }
        } catch (SQLException e) {
            throw new GringottsStorageException("Failed to get list of all chests", e);
        } finally {
            try { if (result!=null) result.close(); } catch (SQLException e) {}
        }

        return chests;
    }

    /* (non-Javadoc)
     * @see org.gestern.gringotts.data.DAO#storeCents(org.gestern.gringotts.GringottsAccount, long)
     */
    @Override
    public boolean storeCents(GringottsAccount account, long amount) {
        try {
            checkConnection();

            storeCents.setLong(1, amount);
            storeCents.setString(2, account.owner.getId());
            storeCents.setString(3, account.owner.getType());

            int updated = storeCents.executeUpdate();
            return updated > 0;

        } catch (SQLException e) {
            throw new GringottsStorageException("Failed to get cents for account: " + account, e);
        }
    }

    /* (non-Javadoc)
     * @see org.gestern.gringotts.data.DAO#getCents(org.gestern.gringotts.GringottsAccount)
     */
    @Override
    public long getCents(GringottsAccount account) {

        ResultSet result = null;
        try {
            checkConnection();

            getCents.setString(1, account.owner.getId());
            getCents.setString(2, account.owner.getType());

            result = getCents.executeQuery();

            if (result.next()) {
                return result.getLong("cents");
            }

            return 0;

        } catch (SQLException e) {
            throw new GringottsStorageException("Failed to get stored cents for account: " + account, e);
        } finally {
            try { if (result!=null) result.close(); } catch (SQLException e) {}
        }
    }

    /**
     * Get a DAO instance.
     * @return the DAO instance
     */
    public static DerbyDAO getDao() {

        if (dao != null) return dao;

        // load derby embedded driver
        String driver = "org.apache.derby.jdbc.EmbeddedDriver";
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            return null;
        }

        dao = new DerbyDAO();
        return dao;
    }

    /* (non-Javadoc)
     * @see org.gestern.gringotts.data.DAO#shutdown()
     */
    @Override
    public void shutdown() {
        try {
            log.info("shutting down database connection");
            // disconnect from derby completely
            String disconnectString = "jdbc:derby:;shutdown=true";
            DriverManager.getConnection(disconnectString);

        } catch (SQLException e) {
            // yes, derby actually throws an exception as a shutdown message ...
            log.info("Derby shutdown: " + e.getSQLState() + ": " + e.getMessage());
            System.gc();
        } 
    }

    /* (non-Javadoc)
     * @see org.gestern.gringotts.data.DAO#finalize()
     */
    @Override
    public void finalize() {
        shutdown();
    }

    /* (non-Javadoc)
     * @see org.gestern.gringotts.data.DAO#deleteAccount(org.gestern.gringotts.GringottsAccount)
     */
    @Override
    public void deleteAccount(GringottsAccount acc) {
        // TODO implement this, mayhaps?
        throw new RuntimeException("delete account not yet implemented");
    }
}
