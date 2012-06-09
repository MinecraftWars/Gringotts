package net.mcw.gringotts;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class Gringotts extends JavaPlugin {

	Logger log = Bukkit.getServer().getLogger();
	
	/** Manager of accounts, listener of events. */
	private Accounting accounting = new Accounting();
	
	public static final ItemStack currency =  
			new ItemStack(Material.INK_SACK, 1, (short) 0, DyeColor.BLUE.getData());
	
	public Gringotts() {
	}
	
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(accounting, this);
		log.info("Gringotts enabled");
	}
	
	public void onDisable() {
		log.info("Gringotts disabled");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player player;
		if (sender instanceof Player) {
			player = (Player)sender;
		} else 
			return false; // for now, no console commands
		
		if(cmd.getName().equalsIgnoreCase("balance")){
			AccountHolder accountOwner = new PlayerAccountHolder(player);
			Account account = accounting.getAccount(accountOwner);
			accountOwner.sendMessage("Your current balance: " + account.balance());
			return true;
		} //If this has happened the function will break and return true. if this hasn't happened the a value of false will be returned.
		return false; 
	}
	
	// TODO add optional dependency to factions. how?
	// TODO add support to vault
	// TODO event handlers: chest/account creation, destruction
	// 
	/*
	 * TODO various items
	 * do we need permissions?
	 * multiworld?
	 */
}
