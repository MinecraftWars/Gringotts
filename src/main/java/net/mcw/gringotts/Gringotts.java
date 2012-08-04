package net.mcw.gringotts;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class Gringotts extends JavaPlugin {
	
	static {
		ConfigurationSerialization.registerClass(Account.class);
		ConfigurationSerialization.registerClass(Accounting.class);
		ConfigurationSerialization.registerClass(AccountChest.class);
		ConfigurationSerialization.registerClass(PlayerAccountHolder.class);
		ConfigurationSerialization.registerClass(FactionAccountHolder.class);
	}
	
	PluginManager pluginmanager;
	Logger log = Bukkit.getServer().getLogger();
	
	/** Manager of accounts, listener of events. */
	
	private final Commands gcommand = new Commands(this);
	private static String directory = "plugins" + File.separator + "Gringotts" + File.separator;
	private final File dataFile = new File(directory + "data.yml");
	
	public Accounting accounting;
	private FileConfiguration data;
	
	public static final ItemStack currency =  
			new ItemStack(Material.INK_SACK, 1, (short)	0, (byte)4);
	
	
	@Override
	public void onEnable() {
		pluginmanager = getServer().getPluginManager();
		
		getCommand("balance").setExecutor(gcommand);
		getCommand("money").setExecutor(gcommand);
		
		// TODO do something useful with this later, like set currency item
		FileConfiguration config = getConfig();
		
		data = getData();
		accounting = (Accounting)data.get("accounting");
		if (accounting == null) accounting = new Accounting();
		
		if (accounting == null){
			log.info("Accounting is null");
			accounting = new Accounting();
		}
		
		data.set("accounting", accounting);
		
		registerEvents();
		
		log.info("[Gringotts] enabled");
	}
	
	@Override
	public void onDisable() {
		saveData(data);
		log.info("[Gringotts] disabled");
	}
	
    private FileConfiguration getData() {
    	
    	new File(directory).mkdir();

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
			log.severe("[Gringotts] Could not save Gringotts data.");
		}
	}

	
	
	private void registerEvents() {
		pluginmanager.registerEvents(new AccountListener(this), this);
	}
    
	
	// TODO add optional dependency to factions. how?
	// TODO add support to vault
	// 
	/*
	 * TODO various items
	 * do we need permissions?
	 * multiworld?
	 */
}
