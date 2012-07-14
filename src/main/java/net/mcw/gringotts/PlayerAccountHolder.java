package net.mcw.gringotts;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;



public class PlayerAccountHolder extends AccountHolder {
	
	static {
		ConfigurationSerialization.registerClass(PlayerAccountHolder.class);
	}

	public final Player accountHolder;
	
	public PlayerAccountHolder(Map<String,Object> serialized) {
		this((String)serialized.get("owner"));
	}
	
	public PlayerAccountHolder(Player player) {
		this.accountHolder = player;
	}

	public PlayerAccountHolder(String name) {
		this.accountHolder = Bukkit.getPlayer(name);
		if (accountHolder == null)
			throw new NullPointerException("Could not retrieve player for name: " + name);
	}

	@Override
	public String getName() {
		return accountHolder.getName();
	}

	@Override
	public void sendMessage(String message) {
		accountHolder.sendMessage(message);
		
	}

	public Map<String, Object> serialize() {
		Map<String, Object> serialized = new HashMap<String, Object>();
		serialized.put("owner", accountHolder.getName()); // TODO is the uuid really persistent?
		return serialized;
	}
	
	public static PlayerAccountHolder deserialize(Map<String,Object> serialized) {
		return new PlayerAccountHolder(serialized);
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
			if (other.accountHolder.getName() != null)
				return false;
		} else if (!accountHolder.getName().equals(other.accountHolder.getName()))
			return false;
		return true;
	}
}
