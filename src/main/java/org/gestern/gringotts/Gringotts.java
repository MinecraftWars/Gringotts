package org.gestern.gringotts;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class Gringotts extends JavaPlugin {

	/** The Gringotts plugin instance. */
	public static Gringotts gringotts;
	
    private PluginManager pluginmanager;
    private Logger log = Bukkit.getServer().getLogger();

    
    private final Commands gcommand = new Commands(this);
    public final AccountHolderFactory accountHolderFactory = new AccountHolderFactory();

    /** Manages accounts. */
    public Accounting accounting;


    @Override
    public void onEnable() {
    	
    	gringotts = this;
    	
        pluginmanager = getServer().getPluginManager();

        CommandExecutor playerCommands = gcommand.new Money();
        CommandExecutor adminCommands = gcommand.new Moneyadmin();

        getCommand("balance").setExecutor(playerCommands);
        getCommand("money").setExecutor(playerCommands);
        getCommand("moneyadmin").setExecutor(adminCommands);

        // load and init configuration
        FileConfiguration savedConfig = getConfig();
        Configuration.config.readConfig(savedConfig);

        accounting = new Accounting();

        registerEvents();
        
        log.info("[Gringotts] enabled");
    }

	@Override
    public void onDisable() {
    	log.info("[Gringotts] shutting down, saving configuration");
    	// fill config file
    	Configuration.config.saveConfig(getConfig());
    	// actually persist config
        saveConfig();
        
        // shut down db connection
        DAO.getDao().shutdown();
        
        log.info("[Gringotts] disabled");
    }



    private void registerEvents() {
        pluginmanager.registerEvents(new AccountListener(this), this);
    }


    // TODO add optional dependency to factions. how?
}
