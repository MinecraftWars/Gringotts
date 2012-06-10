package net.mcw.gringotts;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class Gringotts extends JavaPlugin {
	PluginManager pluginmanager;
	Logger log = Bukkit.getServer().getLogger();
	
	/** Manager of accounts, listener of events. */
	public final Accounting accounting = new Accounting();
	private final Commands gcommand = new Commands(this);
	
	public static final ItemStack currency =  
			new ItemStack(Material.INK_SACK, 1, (short) 0, DyeColor.BLUE.getData());
	
	public Gringotts() {
	}
	
	@Override
	public void onEnable() {
		pluginmanager = getServer().getPluginManager();
		
		getCommand("balance").setExecutor(gcommand);
		
		registerEvents();
		
		log.info("Gringotts enabled");
	}
	
	public void onDisable() {
		log.info("Gringotts disabled");
	}
	
	private void registerEvents() {
		registerEvent(new Accounting());
	}
    
	public void registerEvent(Listener listener) {
		pluginmanager.registerEvents(listener, this);
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
