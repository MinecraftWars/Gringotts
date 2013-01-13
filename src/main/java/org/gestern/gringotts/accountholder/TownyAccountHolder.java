package org.gestern.gringotts.accountholder;


import com.palmergames.bukkit.towny.object.TownyEconomyObject;

public class TownyAccountHolder implements AccountHolder {
	
	TownyEconomyObject owner;
	
	public TownyAccountHolder(TownyEconomyObject owner) {
		this.owner = owner;
	}

	@Override
	public String getName() {
		return owner.getName();
	}

	@Override
	public void sendMessage(String message) {
		// TODO is it possible to send a message to a town?
	}

	@Override
	public String getType() {
		return "towny";
	}

	@Override
	public String getId() {
		return owner.getEconomyName();
	}
	
	@Override
	public String toString() {
		return "TownyAccountHolder("+owner.getName()+")";
	}

}
