package org.gestern.gringotts;

import java.io.IOException;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.gestern.gringotts.accountholder.AccountHolderFactory;
import org.gestern.gringotts.api.impl.VaultConnector;
import org.gestern.gringotts.dependency.Dependency;
import org.mcstats.MetricsLite;



public class Gringotts extends JavaPlugin {

	/** The Gringotts plugin instance. */
	public static Gringotts gringotts;
	
	private Logger log = getLogger();
	
    private PluginManager pluginmanager;
        
    private Commands gcommand;
    public AccountHolderFactory accountHolderFactory;

    /** Manages accounts. */
    public Accounting accounting;


    @Override
    public void onEnable() {
    	
    	gringotts = this;
    	log = getLogger();
    	pluginmanager = getServer().getPluginManager();
    	
    	gcommand = new Commands(this);
    	accountHolderFactory = new AccountHolderFactory();
    	 
        CommandExecutor playerCommands = gcommand.new Money();
        CommandExecutor moneyAdminCommands = gcommand.new Moneyadmin();
        CommandExecutor adminCommands = gcommand.new GringottsCmd();

        getCommand("balance").setExecutor(playerCommands);
        getCommand("money").setExecutor(playerCommands);
        getCommand("moneyadmin").setExecutor(moneyAdminCommands);
        getCommand("gringotts").setExecutor(adminCommands);

        // load and init configuration
        saveDefaultConfig(); // saves default configuration if no config.yml exists yet
        FileConfiguration savedConfig = getConfig();
        Configuration.config.readConfig(savedConfig);

        accounting = new Accounting();

        registerEvents();
        registerEconomy();
        
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
        	log.info("Failed to submit PluginMetrics stats");
        }
        
        log.fine("enabled");
    }

	@Override
    public void onDisable() {
        
        // shut down db connection
        DAO.getDao().shutdown();
        
        log.info("disabled");
    }


	/**
	 * Register Gringotts as economy provider for vault.
	 */
	private void registerEconomy() {
		if (Dependency.D.vault != null) {
			final ServicesManager sm = getServer().getServicesManager();
			sm.register(Economy.class, new VaultConnector(), this, ServicePriority.Highest);
			log.info("Registered Vault interface.");
		} else {
			log.info("Vault not found. Other plugins may not be able to access Gringotts accounts.");
		}
	}

    private void registerEvents() {
        pluginmanager.registerEvents(new AccountListener(this), this);
    }

}
