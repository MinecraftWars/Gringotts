package org.gestern.gringotts;

import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.gestern.gringotts.currency.Currency;
import org.gestern.gringotts.currency.Denomination;

/**
 * Singleton for global configuration information. 
 * Values are initialized when the plugin is enabled.
 * 
 * @author jast
 *
 */
public enum Configuration {
    config;
    
    private final Logger log = Gringotts.gringotts.getLogger();

    /** Currency item types. The item types are ordered by their respective unit value. */
    public Currency currency;

    /** Flat tax on every player-to-player transaction. This is a value in currency units. */
    public double transactionTaxFlat = 0;

    /** Rate tax on every player-to-player transaction. This is a fraction, e.g. 0.1 means 10% tax. */ 
    public double transactionTaxRate = 0;
    
    /** Support (virtual) fractional currency values. */
    public boolean currencyFractional;
    
    /** Use container vaults (chest, dispenser, furnace). */
    public boolean usevaultContainer;
    
    /** Use ender chests as player vaults. */
    public boolean usevaultEnderchest;
    
    

    /**
     * Set configuration form values in a file configuration.
     * @param savedConf
     */
    public void readConfig(FileConfiguration savedConfig) {
    	
        String version = Bukkit.getBukkitVersion();

        
        if (Util.versionAtLeast(version, "1.3.1")) {
        	log.info("Found Bukkit version: "+version+". All features enabled.");
    		
        	config.usevaultEnderchest = savedConfig.getBoolean("usevault.enderchest", true);
            
        } else {
        	log.info("Found Bukkit version: "+version+". Disabling 1.3+ features.");
        	
            config.usevaultEnderchest = false;
        }
        
        String currencyNameSingular, currencyNamePlural;
    	currencyNameSingular = savedConfig.getString("currency.name.singular", "Emerald");
        currencyNamePlural = savedConfig.getString("currency.name.plural", currencyNameSingular+"s");
        currency = new Currency(currencyNameSingular, currencyNamePlural);
        
    	// legacy currency config, overrides defaults if available
    	int currencyType = savedConfig.getInt("currency.type",-1);
    	if (currencyType > 0) {
    		byte currencyDataValue = (byte)savedConfig.getInt("currency.datavalue", 0);
    		ItemStack legacyCurrency = new ItemStack(currencyType, 0, (short)0);
    		legacyCurrency.setData(new MaterialData(currencyType, currencyDataValue));
    		Denomination denom = new Denomination(legacyCurrency, 1);
    		currency.addDenomination(denom);
    	} else {
    		// regular currency configuration (multi-denomination)
    		ConfigurationSection denomSection = savedConfig.getConfigurationSection("currency.denominations");
        	parseCurrency(denomSection);
    	}
        
        
        config.currencyFractional = savedConfig.getBoolean("currency.fractional", true);

        config.transactionTaxFlat = savedConfig.getDouble("transactiontax.flat", 0);
        config.transactionTaxRate = savedConfig.getDouble("transactiontax.rate", 0);
        
        config.usevaultContainer = savedConfig.getBoolean("usevault.container", true);
    }
    
    /**
     * Parse a multi-denomination currency from configuration.
     * A currency definition consists of a map of demoninations to value. 
     * A denomination type is defined either as the item id, 
     * or a semicolon-separated string of item id; damage value; data value
     * @param denomSection config section containaining denomination definition
     */
    private void parseCurrency(ConfigurationSection denomSection) {
    	
    	Set<String> denoms = denomSection.getKeys(false);
    	
		for (String denomStr : denoms) {
			String[] parts = denomStr.split(";");
			int type = 0;
			short dmg = 0;
			byte data = 0;
			try {
				// a denomination needs at least a valid item type
				type = Integer.parseInt(parts[0]);
    			if (parts.length >=2) dmg = Short.parseShort(parts[1]);
    			if (parts.length >=3) data = Byte.parseByte(parts[2]);
    			ItemStack denomType = new ItemStack(type, 1, dmg);
    			denomType.setData(new MaterialData(type, data));
    			
    			int value = denomSection.getInt(denomStr);
    			Denomination denom = new Denomination(denomType,value);
    			currency.addDenomination(denom);
			} catch (Exception e) {
				throw new RuntimeException("Encountered an error parsing currency. Please check your Gringotts configuration.", e);
			}
		}
    	
    }
    
}
