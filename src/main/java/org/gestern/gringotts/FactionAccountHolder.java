package org.gestern.gringotts;

import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerialization;

import com.massivecraft.factions.Faction;

public class FactionAccountHolder extends AccountHolder {
	
	static {
		ConfigurationSerialization.registerClass(FactionAccountHolder.class);
	}
	
	private final Faction owner;

	/**
	 * Default ctor.
	 */
	public FactionAccountHolder(Faction owner) {
		this.owner = owner;
	}
	
	/**
	 * Deserialization ctor.
	 * @param serialized
	 */
	public FactionAccountHolder(Map<String,Object> serialized) {
		this.owner = null;
		// TODO implement deserializing constructor
	}
	
	@Override
	public String getName() {
		return owner.getTag();
	}

	@Override
	public void sendMessage(String message) {
		owner.sendMessage(message);
	}


	public Map<String, Object> serialize() {
		// TODO implement serializer
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((owner == null) ? 0 : owner.getId().hashCode());
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
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.getId().equals(other.owner.getId()))
			return false;
		return true;
	}
	

}
