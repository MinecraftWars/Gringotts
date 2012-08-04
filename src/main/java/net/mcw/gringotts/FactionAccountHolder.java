package net.mcw.gringotts;

import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerialization;

public class FactionAccountHolder extends AccountHolder {
	
	static {
		ConfigurationSerialization.registerClass(FactionAccountHolder.class);
	}

	/**
	 * Default ctor.
	 */
	public FactionAccountHolder() {}
	
	/**
	 * Deserialization ctor.
	 * @param serialized
	 */
	public FactionAccountHolder(Map<String,Object> serialized) {
		// TODO implement deserializing constructor
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "faction owner unimlpemented";
	}

	@Override
	public void sendMessage(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean equals(Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public Map<String, Object> serialize() {
		// TODO implement serializer
		return null;
	}
	
	public static FactionAccountHolder deserialize(Map<String,Object> serialized) {
		return new FactionAccountHolder(serialized);
	}

}
