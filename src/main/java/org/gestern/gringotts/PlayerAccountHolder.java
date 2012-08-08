package org.gestern.gringotts;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;



public class PlayerAccountHolder extends AccountHolder {
	
	private final Logger log = Bukkit.getServer().getLogger();

	public final OfflinePlayer accountHolder;
	
	public PlayerAccountHolder(Map<String,Object> serialized) {
		this((OfflinePlayer)serialized.get("owner"));
	}
	
	public PlayerAccountHolder(OfflinePlayer player) {		
		if (player != null)
			this.accountHolder = player;
		else throw new NullPointerException("Attempted to create account holder with null player.");
	}

	public PlayerAccountHolder(String name) {
		this.accountHolder = Bukkit.getOfflinePlayer(name);
		if (accountHolder == null)
			throw new NullPointerException("Could not retrieve player for name: " + name);
	}

	@Override
	public String getName() {
		return accountHolder.getName();
	}

	@Override
	public void sendMessage(String message) {
		if (accountHolder.isOnline()) {
			accountHolder.getPlayer().sendMessage(message);
		}
		
	}

	public Map<String, Object> serialize() {
		Map<String, Object> serialized = new HashMap<String, Object>();
		serialized.put("owner", accountHolder);
		return serialized;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accountHolder == null) ? 0 : accountHolder.getName().hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		PlayerAccountHolder other = (PlayerAccountHolder) obj;
		if (accountHolder == null) {
			if (other.accountHolder == null)
				return true;
			if (other.accountHolder.getName() != null)
				return false;
		} else if (!accountHolder.getName().equals(other.accountHolder.getName()))
			return false;
		return true;
	}
}
