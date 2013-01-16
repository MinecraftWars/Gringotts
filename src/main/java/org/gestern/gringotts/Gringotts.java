package org.gestern.gringotts;

import static org.gestern.gringotts.Configuration.CONF;
import static org.gestern.gringotts.dependency.Dependency.DEP;

import java.io.IOException;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.gestern.gringotts.accountholder.AccountHolderFactory;
import org.gestern.gringotts.accountholder.AccountHolderProvider;
import org.gestern.gringotts.api.impl.VaultConnector;
import org.gestern.gringotts.data.DerbyDAO;
import org.gestern.gringotts.event.AccountListener;
import org.gestern.gringotts.event.PlayerVaultListener;
import org.gestern.gringotts.event.VaultCreator;
import org.mcstats.MetricsLite;


public class Gringotts extends JavaPlugin {

	/** The Gringotts plugin instance. */
	public static Gringotts G;
	
	private Logger log;
        
    private Commands gcommand;

    /** Manages accounts. */
    public Accounting accounting;
    
    /** 
     * The account holder factory is the place to go if you need an AccountHolder instance for an id.
     */
    public AccountHolderFactory accountHolderFactory = new AccountHolderFactory();
    
    @Override
    public void onEnable() {
    	
    	G = this;
    	
    	try {
	    	log = getLogger();
	    	
	        // load and init configuration
	        saveDefaultConfig(); // saves default configuration if no config.yml exists yet
	        FileConfiguration savedConfig = getConfig();
	        CONF.readConfig(savedConfig);
	    	
	    	gcommand = new Commands(this);
	
	        accounting = new Accounting();
	        
	        registerCommands();
	        registerEvents();
	        registerEconomy();
	        
	        try {
	            MetricsLite metrics = new MetricsLite(this);
	            metrics.start();
	        } catch (IOException e) {
	        	log.info("Failed to submit PluginMetrics stats");
	        }
	        
	        // just call DAO once to ensure it's loaded before startup is complete
	        DerbyDAO.getDao();
	        
    	} catch(GringottsStorageException e) { 
        	log.severe(e.getMessage()); 
        	disable();
        } catch(GringottsConfigurationException e) {
        	log.severe(e.getMessage());
        	disable();
        } catch (RuntimeException e) {
        	disable();
        	throw e;
        }
        
        
        log.fine("enabled");
    }
    
    private void disable() {
    	Bukkit.getPluginManager().disablePlugin(this);
    	log.warning("Gringotts disabled due to startup errors.");
    }

	@Override
    public void onDisable() {
        
        // shut down db connection
        try{ DerbyDAO.getDao().shutdown(); }
        catch (GringottsStorageException e) {
        	log.severe(e.toString()); 
        }
        
        log.info("disabled");
    }
	
	private void registerCommands() {
		CommandExecutor playerCommands = gcommand.new Money();
        CommandExecutor moneyAdminCommands = gcommand.new Moneyadmin();
        CommandExecutor adminCommands = gcommand.new GringottsCmd();
        
		getCommand("balance").setExecutor(playerCommands);
        getCommand("money").setExecutor(playerCommands);
        getCommand("moneyadmin").setExecutor(moneyAdminCommands);
        getCommand("gringotts").setExecutor(adminCommands);
	}
	
	private void registerEvents() {
		PluginManager manager = getServer().getPluginManager();
		
		manager.registerEvents(new AccountListener(), this);
		manager.registerEvents(new PlayerVaultListener(), this);
		manager.registerEvents(new VaultCreator(), this);
		
		// listeners for other account types are loaded with dependencies
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
	
	public void registerAccountHolderProvider(String type, AccountHolderProvider provider) {
		accountHolderFactory.registerAccountHolderProvider(type, provider);
	}

}
