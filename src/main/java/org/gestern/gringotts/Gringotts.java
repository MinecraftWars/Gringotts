package org.gestern.gringotts;

import static org.gestern.gringotts.dependency.Dependency.DEP;

import java.io.IOException;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.gestern.gringotts.api.impl.VaultConnector;
import org.mcstats.MetricsLite;

import static org.gestern.gringotts.Configuration.CONF;



public class Gringotts extends JavaPlugin {

	/** The Gringotts plugin instance. */
	public static Gringotts G;
	
	private Logger log;
        
    private Commands gcommand;

    /** Manages accounts. */
    public Accounting accounting;


    @Override
    public void onEnable() {
    	
    	G = this;
    	log = getLogger();
    	
        // load and init configuration
        saveDefaultConfig(); // saves default configuration if no config.yml exists yet
        FileConfiguration savedConfig = getConfig();
        CONF.readConfig(savedConfig);
    	
    	gcommand = new Commands(this);
    	 
        CommandExecutor playerCommands = gcommand.new Money();
        CommandExecutor moneyAdminCommands = gcommand.new Moneyadmin();
        CommandExecutor adminCommands = gcommand.new GringottsCmd();

        getCommand("balance").setExecutor(playerCommands);
        getCommand("money").setExecutor(playerCommands);
        getCommand("moneyadmin").setExecutor(moneyAdminCommands);
        getCommand("gringotts").setExecutor(adminCommands);

        accounting = new Accounting();

        getServer().getPluginManager().registerEvents(new AccountListener(this), this);
        registerEconomy();
        
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
        	log.info("Failed to submit PluginMetrics stats");
        }
        
        // just call DAO once to ensure it's loaded before startup is complete
        DAO.getDao();
        
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
		if (DEP.vault.exists()) {
			final ServicesManager sm = getServer().getServicesManager();
			sm.register(Economy.class, new VaultConnector(), this, ServicePriority.Highest);
			log.info("Registered Vault interface.");
		} else {
			log.info("Vault not found. Other plugins may not be able to access Gringotts accounts.");
		}
	}

}
