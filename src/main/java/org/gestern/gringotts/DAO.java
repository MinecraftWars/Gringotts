package org.gestern.gringotts;

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

/**
 * 
 * @author jast
 *
 */
public class DAO {
	
	static {
		// load derby embedded driver
		String driver = "org.apache.derby.jdbc.EmbeddedDriver";
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			throw new GringottsStorageException("Could not initialize database driver.", e);
		}
	}
	
	/** Singleton DAO instance. */
	private static final DAO dao = new DAO();
	
	private final Logger log = Bukkit.getLogger();
	
	private final Driver driver;
	private Connection connection;
	// TODO remove or use unused statements
	private PreparedStatement 
		storeAccountChest, destroyAccountChest, getAccountChest, 
		storeAccount, getAccount, getAccountList, getChests, 
		getChestsForAccount, getCents, storeCents;
	
	private static final String dbName = "GringottsDB";
	
	/** Full connection string for database, without connect options. */
	private final String dbString;
	
	private DAO() {
		
		String dbPath = Gringotts.gringotts.getDataFolder().getAbsolutePath();
		dbString = "jdbc:derby:" + dbPath+"/"+dbName;
		String connectString = dbString + ";create=true";

		try {
			driver = DriverManager.getDriver(connectString);
			connection = driver.connect(connectString, null);

			checkConnection();
			setupDB(connection);
			prepareStatements();
			
			log.info("[Gringotts] DAO setup successfully.");

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
    			log.info("[Gringotts] created table ACCOUNT");
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
    			log.info("[Gringotts] created table ACCOUNTCHEST");
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
		getAccountChest = connection.prepareStatement(
				"SELECT ac.world, ac.x, ac.y, ac.z, a.type, a.owner " +
				"FROM accountchest ac JOIN account a ON ac.account = a.id " + 
				"WHERE ac.world = ? and ac.x = ? and ac.y = ? and ac.z = ?");
		storeAccount = connection.prepareStatement(
				"insert into account (type, owner, cents) values (?,?,0)");

		getAccount = connection.prepareStatement(
				"select * from account where owner = ? and type = ?");
		getAccountList = connection.prepareStatement(
				"select * from account");
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
			log.warning("[Gringotts] Database connection lost. Reinitialized DB.");
    	}
    }


	/**
     * Save an account chest to database. 
     * @param chest
     * @return true if chest was stored, false otherwise
     * @throws GringottsStorageException when storage failed
     */
    public boolean storeAccountChest(AccountChest chest) {
    	Account account = chest.getAccount();
    	Location loc = chest.sign.getLocation();
    	
    	log.info("[Gringotts] storing account chest: " + chest + " for account: " + account);
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
				log.warning("[Gringotts] Unable to store account chest: " + e.getMessage());
				return false;
			}
			
			// other more serious error probably
			throw new GringottsStorageException("Failed to store account chest: " + chest, e);
		}
    }
    
    /**
     * 
     * @param chest
     * @return true if the chest was deleted, false if no chest was deleted.
     */
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
    
    /**
     * Store the given Account to DB.
     * @param account
     * @return true if an account was stored, false if it already existed
     */
    public boolean storeAccount(Account account) {
    	AccountHolder owner = account.owner;

    	if (getAccount(owner) != null)
    		return false;
    	
    	try {
    		checkConnection();
    		
			storeAccount.setString(1, owner.getType());
			storeAccount.setString(2, owner.getId());
			
			int updated = storeAccount.executeUpdate();
			return updated > 0;
		} catch (SQLException e) {
			throw new GringottsStorageException("Failed to store account: " + account, e);
		}
    }
   
    
    /**
     * Get account belonging to a given account owner. 
     * If an account seems to belong to an owner, but h
     * @param accountHolder
     * @return account belonging to the given owner, or null if the owner has no account
     */
    public Account getAccount(AccountHolder accountHolder) {

    	AccountHolderFactory ahf = new AccountHolderFactory();
    	
    	ResultSet result = null;
    	try {
    		checkConnection();
    		
			getAccount.setString(1, accountHolder.getId());
			getAccount.setString(2, accountHolder.getType());
			
			result = getAccount.executeQuery();
			if (result.next()) {
				String type = result.getString("type");
				String ownerName = result.getString("owner");
				
		    	AccountHolder owner = ahf.get(type, ownerName);
				return new Account(owner);
			} else return null;
			
		} catch (SQLException e) {
			throw new GringottsStorageException("Failed to get account for owner: " + accountHolder, e);
		} finally {
			try { if (result!=null) result.close(); } catch (SQLException e) {}
		}
    }

    
    /**
     * Get set of all chests registered with Gringotts. 
     * If a stored chest turns out to be invalid, that chest is removed from storage.
     * @return set of all chests registered with Gringotts
     */
    public Set<AccountChest> getChests() {
    	AccountHolderFactory ahf = new AccountHolderFactory();
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
				
				Block signBlock = loc.getBlock();
		    	if (Util.isSignBlock(signBlock)) {
					AccountHolder owner = ahf.get(type, ownerId);
					if (owner == null)
						throw new GringottsStorageException("AccountHolder "+type+":"+ownerId+" is not valid. Perhaps stored data is inconsistent?");
					Account ownerAccount = new Account(owner);
					Sign sign = (Sign) signBlock.getState();
					chests.add(new AccountChest(sign, ownerAccount));
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
    
    
    /**
     * Get all chests belonging to the given account.
     * If a stored chest turns out to be invalid, that chest is removed from storage.
     * @param account account to fetch chests for.
     * @return
     */
    public Set<AccountChest> getChests(Account account) {
	
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

    /**
	 * Store an amount of cents to a given account.
	 * @param account
	 * @param amount
	 * @return true if storing was successful, false otherwise.
	 */
	public boolean storeCents(Account account, int amount) {
		try {
			checkConnection();
    		
			storeCents.setInt(1, amount);
			storeCents.setString(2, account.owner.getId());
			storeCents.setString(3, account.owner.getType());
			
			int updated = storeCents.executeUpdate();
			return updated > 0;
	
		} catch (SQLException e) {
			throw new GringottsStorageException("Failed to get cents for account: " + account, e);
		}
	}

	/**
	 * Get the cents stored for a given account.
	 * @param account
	 * @return amount of cents stored in the account, 0 if the account is not stored
	 */
	public int getCents(Account account) {
		
		ResultSet result = null;
		try {
			checkConnection();
    		
			getCents.setString(1, account.owner.getId());
			getCents.setString(2, account.owner.getType());
			
			result = getCents.executeQuery();
			
			if (result.next()) {
				int cents = result.getInt("cents");
				return cents;
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
	public static DAO getDao() {
    	return dao;
    }
	
	/**
	 * Shutdown connections and DB.
	 */
	public void shutdown() {
		try {
			checkConnection();
    		
			connection.close();
			
			log.info("[Gringotts] shutting down database connection");
			// disconnect from derby completely
			String disconnectString = dbString + ";shutdown=true";
			Driver driver = DriverManager.getDriver(disconnectString);
			DriverManager.deregisterDriver(driver);
			
			// force garbage collection to unload the EmbeddedDriver
			// so Derby can be restarted (just to be sure)
			System.gc();
		} catch (SQLException e) {
			log.severe("[Gringotts] failed to shut down database correctly: " + e.getMessage());
			e.printStackTrace();
		}
	}
    
    @Override
    public void finalize() {
    	log.info("[Gringotts] shutting down database connection.");
    	try {
			connection.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}
