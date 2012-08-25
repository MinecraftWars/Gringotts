package org.gestern.gringotts;

import java.util.HashMap;
import java.util.Map;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class FactionAccountHolder implements AccountHolder {

    private final Faction owner;

    /**
     * Default ctor.
     */
    public FactionAccountHolder(Faction owner) {
        this.owner = owner;
    }

    public FactionAccountHolder(String Id) {
        Faction faction = Factions.i.get(Id);

        if (faction != null)
            this.owner = faction;
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
        return owner.getTag();
    }

    @Override
    public void sendMessage(String message) {
        owner.sendMessage(message);
    }


    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<String, Object>();
        serialized.put("fowner", owner.getId());
        return serialized;
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

	@Override
	public String getType() {
		return "faction";
	}


}
