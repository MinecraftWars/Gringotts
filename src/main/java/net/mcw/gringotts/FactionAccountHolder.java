package net.mcw.gringotts;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;


public class FactionAccountHolder extends AccountHolder {
	
	private final Logger log = Bukkit.getServer().getLogger();

	public final Faction accountHolder;
	
	/**
	 * Default ctor.
	 */
	public FactionAccountHolder(Faction faction) {
		if (faction != null)
			this.accountHolder = faction;
		else throw new NullPointerException("Attempted to create account holder with null faction.");
	}
	
	public FactionAccountHolder(FPlayer player) {
		Faction faction = player.getFaction();
		
		if (faction != null)
			this.accountHolder = faction;
		else throw new NullPointerException("Attempted to create account holder with null faction.");
	}
	
	public FactionAccountHolder(String Id) {
		Faction faction = Factions.i.get(Id);
		
		if (faction != null)
			this.accountHolder = faction;
		else throw new NullPointerException("Attempted to create account holder with null faction.");
	}
	
	/**
	 * Deserialization ctor.
	 * @param serialized
	 */
	public FactionAccountHolder(Map<String,Object> serialized) {
		this((String)serialized.get("fowner"));
	}
	
	@Override
	public String getName() {
		return accountHolder.getId();
	}

	@Override
	public void sendMessage(String message) {
		accountHolder.sendMessage(message);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accountHolder == null) ? 0 : accountHolder.getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		FactionAccountHolder other = (FactionAccountHolder) obj;
		if (accountHolder == null) {
			if (other.accountHolder == null)
				return true;
			if (other.accountHolder.getId() != null)
				return false;
		} else if (!accountHolder.getId().equals(other.accountHolder.getId()))
			return false;
		return true;
	}

	public Map<String, Object> serialize() {
		Map<String, Object> serialized = new HashMap<String, Object>();
		serialized.put("fowner", accountHolder.getId());
		return serialized;
	}
	
	/*public FactionAccountHolder deserialize(Map<String,Object> serialized) {
		return new FactionAccountHolder(serialized);
	}*/
}
