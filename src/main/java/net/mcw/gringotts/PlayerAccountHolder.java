package net.mcw.gringotts;

import org.bukkit.entity.Player;



public class PlayerAccountHolder extends AccountHolder {

	public final Player accountHolder;
	
	public PlayerAccountHolder(Player player) {
		this.accountHolder = player;
	}
}
