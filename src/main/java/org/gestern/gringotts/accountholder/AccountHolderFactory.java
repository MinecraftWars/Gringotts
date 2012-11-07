package org.gestern.gringotts.accountholder;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.gestern.gringotts.dependency.Dependency;
import org.gestern.gringotts.dependency.FactionsHandler;
import org.gestern.gringotts.dependency.TownyHandler;

import com.massivecraft.factions.Factions;

/**
 * Manages creating various types of AccountHolder centrally.
 * 
 * @author jast
 *
 */
public class AccountHolderFactory {
	
	private final Logger log = Bukkit.getLogger();
	
	public AccountHolder getAccount(String owner) {
		return get(owner);
	}

	/**
	 * Get an account holder with automatically determined type, based on the owner's name.
	 * @param owner
	 * @return account holder for the given owner name, or null if none could be determined
	 */
    public AccountHolder get(String owner) {    	
        OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
        // if this player has ever played on the server, they are a legit account holder
        if (player.isOnline() || player.hasPlayedBefore()) {
            return new PlayerAccountHolder(player);
        }

        if (Dependency.D.factions != null) {
        	FactionsHandler handler = new FactionsHandler();
        	AccountHolder holder = handler.getAccountHolderByName(owner);
        	if (holder != null) return holder;
        }
        
        if (Dependency.D.towny != null) {
    		TownyHandler handler = new TownyHandler();
    		AccountHolder holder = handler.getAccountHolderByAccountName(owner);
    		if (holder!=null) return holder;
        }
        
        // TODO support banks
        // TODO support virtual accounts

        log.fine("[Gringotts] No account holder found for " + owner);
        
        return null;
    }
    
    /**
     * Get an account holder of known type.
     * @param type
     * @param owner
     * @return account holder of given type with given owner name, or null if none could be determined or type is not supported.
     */
    public AccountHolder get(String type, String owner) {
    	if (type.equals("player")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
            // if this player has ever played on the server, they are a legit account holder
            if (player.isOnline() || player.getLastPlayed() > 0) {
                return new PlayerAccountHolder(player);
            }
            else return null;
    	}
    	
    	if (Dependency.D.factions != null && type.equals("faction")) {
            if (Factions.i.exists(owner))
                return new FactionAccountHolder(Factions.i.get(owner));
            else return null;
    	}
    	
    	if (Dependency.D.towny != null && type.equals("towny")) {
    		TownyHandler handler = new TownyHandler();
    		AccountHolder holder = handler.getAccountHolderByAccountName(owner);
    		if (holder!=null) return holder;
    	} 
    	
    	// no valid type
    	return null;
    }
    

}
