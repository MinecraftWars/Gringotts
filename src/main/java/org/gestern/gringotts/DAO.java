package org.gestern.gringotts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;

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
	
	private final Connection connection;
	private final PreparedStatement 
		storeAccountChest, destroyAccountChest, getAccountChest, 
		storeAccount, getAccount, getAccountList, getChests;
	
	private DAO() {
		String dbName = "Gringotts";
		String connectString = "jdbc:derby:"+dbName+";create=true";
		try {
			connection = DriverManager.getConnection(connectString);
	
			storeAccountChest = connection.prepareStatement("");
			destroyAccountChest = connection.prepareStatement("delete from accountchest where world = ? and x = ? and y = ? and z = ?");
			getAccountChest = connection.prepareStatement("select * from accountchest where world = ? and x = ? and y = ? and z = ?");
			storeAccount = connection.prepareStatement(""); // insert or update?
			getAccount = connection.prepareStatement("select * from account where owner = ?");
			getAccountList = connection.prepareStatement("select * from account");
			getChests = connection.prepareStatement("select * from accountchest");

		} catch (SQLException e) {
			throw new GringottsStorageException("Failed to initialize database connection.", e);
		}
		
	};
	
	
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
    
    public Set<AccountChest> getChests() {
    	Set<AccountChest> chests = new HashSet<AccountChest>();
    	try {
			ResultSet result = getChests.executeQuery();
			
			while (result.next()) {
				String owner = result.getString("owner");
				String world = result.getString("world");
				int x = result.getInt("x");
				int y = result.getInt("y");
				int z = result.getInt("z");
				
//				Account account = new Account(owner);
//				new AccountChest(account, sign)
//				new accountch
			}
			result.get
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
