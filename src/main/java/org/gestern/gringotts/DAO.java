package org.gestern.gringotts;

import java.sql.Connection;
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
import org.bukkit.block.Sign;

/**
 * 
 * @author jast
 *
 */
public class DAO {
	
	static {
		String driver = "org.apache.derby.jdbc.EmbeddedDriver";
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			throw new GringottsStorageException("Could not initialize database driver.", e);
		}
	}
	
	
	private static final DAO dao = new DAO();
	
	private final Logger log = Bukkit.getLogger();
	
	private final Connection connection;
	private final PreparedStatement 
		storeAccountChest, destroyAccountChest, getAccountChest, 
		storeAccount, getAccount, getAccountList, getChests;
	
	private DAO() {
		String dbName = "Gringotts";
		String connectString = "jdbc:derby:"+dbName+";create=true";
		try {
			connection = DriverManager.getConnection(connectString);
			
			setupDB(connection);
	
			storeAccountChest = connection.prepareStatement("select * from accountchest where false");
			destroyAccountChest = connection.prepareStatement("delete from accountchest where world = ? and x = ? and y = ? and z = ?");
			getAccountChest = connection.prepareStatement("select * from accountchest where world = ? and x = ? and y = ? and z = ?");
			storeAccount = connection.prepareStatement("select * from accountchest where false"); // TODO insert or update?
			getAccount = connection.prepareStatement("select * from account where owner = ?");
			getAccountList = connection.prepareStatement("select * from account");
			getChests = connection.prepareStatement("select * from accountchest");
			
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
    	
    	String createAccount = 
    			"create table account (" +
    			"id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
    			"type varchar(64), owner varchar(64), " +
    			"primary key (id))";

    	String createAccountChest =		
    		"create table accountchest (id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
    				"world varchar(64), x integer, y integer, z integer, account integer, " + 
    				"primary key(id), unique(world,x,y,z), foreign key(account) references account(id))";


		connection.createStatement().executeQuery(createAccount);
		connection.createStatement().executeQuery(createAccountChest);
	}


	/**
     * Save an account chest to database. 
     * @param chest
     * @throws GringottsStorageException when storage failed
     */
    public void storeAccountChest(AccountChest chest) {
    }
    
    /**
     * 
     * @param chest
     * @return
     */
    public boolean destroyAccountChest(AccountChest chest) {
    	
        return false;
    }
    
    public boolean storeAccount(Account account) {
        return false;
    }
    
    /**
     * Get the account specified by an id.
     * @param id
     * @return
     */
    public Account getAccount(String id) {
        return null;
    }
    
    /**
     * Get the account owning the given account chest.
     * @param chest
     * @return
     */
    public Account getAccount(AccountChest chest) {
    	return null;
    }

    
    public AccountChest getAccountChest(Location location) {
    	return null;
    }
    
    public Set<Account> getAccountList() {
        return null;
    }
    
    /**
     * Get set of all chests registered with Gringotts.
     * @return
     */
    public Set<AccountChest> getChests() {
    	Set<AccountChest> chests = new HashSet<AccountChest>();
    	try {
			ResultSet result = getChests.executeQuery();
			
			while (result.next()) {
				String worldName = result.getString("world");
				int x = result.getInt("x");
				int y = result.getInt("y");
				int z = result.getInt("z");
				
				World world = Bukkit.getWorld(worldName);
				Location loc = new Location(world, x, y, z);
				
				Sign sign = (Sign) loc.getBlock();
				chests.add(new AccountChest(sign));
			}
		} catch (SQLException e) {
			throw new GringottsStorageException("Failed to get list of all chests", e);
		}
    	return null;
    }
    
    public static DAO getDao() {
    	return dao;
    }
    
    @Override
    public void finalize() {
    	try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}
