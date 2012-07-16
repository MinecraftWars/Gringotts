package net.mcw.gringotts;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class Gringotts extends JavaPlugin {
	PluginManager pluginmanager;
	Logger log = Bukkit.getServer().getLogger();
	
	/** Manager of accounts, listener of events. */
	
	private final Commands gcommand = new Commands(this);
	private static String directory = "plugins" + File.separator + "Gringotts" + File.separator;
	private final File dataFile = new File(directory + "data.yml");
	
	public Accounting accounting;
	private FileConfiguration data;
	
	public static final ItemStack currency =  
			new ItemStack(Material.INK_SACK, 1, (short) 0, DyeColor.BLUE.getData());
	
	
	@Override
	public void onEnable() {
		pluginmanager = getServer().getPluginManager();
		
		getCommand("balance").setExecutor(gcommand);
		getCommand("money").setExecutor(gcommand);
		
		// TODO do something useful with this later, like set currency item
		FileConfiguration config = getConfig();
		
		data = getData();
		accounting = (Accounting)data.get("accounting");
		
		if (accounting == null){
			log.info("Accounting is null");
			accounting = new Accounting();
		}
		
		registerEvents();
		
		log.info("Gringotts enabled");
	}
	
	public void onDisable() {
		saveData(data);
		log.info("Gringotts disabled");
	}
	
    private FileConfiguration getData() {
    	
    	new File(directory).mkdir();
    	
        if (!dataFile.exists())
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        return YamlConfiguration.loadConfiguration(dataFile);
    }
	
	private void saveData(FileConfiguration config) {
		try {
			log.info("[Gringotts] Saving to file..." + dataFile.getAbsolutePath());
			config.save(dataFile);
		} catch (IOException e) {
			log.severe("Could not save Gringotts data.");
		}
	}

	
	
	private void registerEvents() {
		registerEvent(new AccountListener(this));
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
