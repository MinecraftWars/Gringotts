package org.gestern.gringotts.dependency;

import org.bukkit.entity.Player;
import org.gestern.gringotts.accountholder.TownyAccountHolder;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyEconomyObject;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class TownyHandler implements DependencyHandler {

	/**
	 * Get a TownyAccountHolder for the town of which player is a resident, if any.
	 * @param player
	 * @return TownyAccountHolder for the town of which player is a resident, if any. null otherwise.
	 */
	public TownyAccountHolder getTownAccountHolder(Player player) {
		try {
			Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
			Town town = resident.getTown();
			return new TownyAccountHolder(town);
				
		} catch (NotRegisteredException e) {
		}
		
		return null;
	}
	
	/**
	 * Get a TownyAccountHolder for the nation of which player is a resident, if any.
	 * @param player
	 * @return TownyAccountHolder for the nation of which player is a resident, if any. null otherwise.
	 */	
	public TownyAccountHolder getNationAccountHolder(Player player) {
		try {
			Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
			Town town = resident.getTown();
			Nation nation = town.getNation();
			return new TownyAccountHolder(nation);
				
		} catch (NotRegisteredException e) {
		}
		
		return null;
	}
	
	/**
	 * Get a TownyAccountHolder based on the name of the account. 
	 * Names beginning with "town-" will beget a town account holder and names beginning with "nation-"
	 * a nation account holder.
	 * @param name Name of the account.
	 * @return a TownyAccountHolder based on the name of the account
	 */
	public TownyAccountHolder getAccountHolderByAccountName(String name) {
		TownyEconomyObject teo = townyObject(name);
		return teo!=null? new TownyAccountHolder(teo) : null;
	}
	
    /**
     * Get a towny Town or Nation for a given name.
     * @param name name of town or nation
     * @return Town or Nation object for given name
     */
    private TownyEconomyObject townyObject(String name) {
    	
    	TownyEconomyObject teo = null;
    	
    	if (name.startsWith("town-")) {
	    	try { teo = TownyUniverse.getDataSource().getTown(name.substring(5)); } 
	    	catch (NotRegisteredException e) { }
    	}
    	
    	if (name.startsWith("nation-")) {
	    	try { teo = TownyUniverse.getDataSource().getNation(name.substring(7));
			} catch (NotRegisteredException e) { }
    	}
    	
    	return teo;
    }

}
