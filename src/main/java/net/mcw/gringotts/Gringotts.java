package net.mcw.gringotts;

import java.util.HashMap;
import java.util.Map;

public class Gringotts {

	Map<AccountHolder, Account> accounts = new HashMap<AccountHolder, Account>();
	
	
	
	public Gringotts() {
		
	}
	
	// TODO add dependency to bukkit, vault(?) and optional dependency to factions. how?
	// TODO add support to vault
	// TODO event handlers: chest/account creation, destruction
	// 
	/*
	 * TODO various items
	 * do we need permissions?
	 * multiworld?
	 */
}
