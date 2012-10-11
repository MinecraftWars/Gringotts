package org.gestern.gringotts;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 * Singleton for global configuration information. 
 * Values are initialized when the plugin is enabled.
 * 
 * @author jast
 *
 */
public enum Configuration {
    config;

    /** Currency item type. */
    public ItemStack currency = null; 

    /** Flat tax on every player-to-player transaction. This is a value in currency units. */
    public double transactionTaxFlat = 0;

    /** Rate tax on every player-to-player transaction. This is a fraction, e.g. 0.1 means 10% tax. */ 
    public double transactionTaxRate = 0;

    /** Name of currency, singular. */
    public String currencyNameSingular;

    /** Name of currency, plural. */
    public String currencyNamePlural;
    
    /** Support (virtual) fractional currency values. */
    public boolean currencyFractional;
    
    /** Use container vaults (chest, dispenser, furnace). */
    public boolean usevaultContainer;
    
    

    /**
     * Set configuration form values in a file configuration.
     * @param savedConf
     */
    public void readConfig(FileConfiguration savedConfig) {
        int currencyType = savedConfig.getInt("currency.type", 266);
        byte currencyDataValue = (byte)savedConfig.getInt("currency.datavalue", 0);
        if (currencyType >=0)
        	currency = new ItemStack(currencyType, 0, (short)0, currencyDataValue);
        config.currencyNameSingular = savedConfig.getString("currency.name.singular", "Gold Ingot");
        config.currencyNamePlural = savedConfig.getString("currency.name.plural", config.currencyNameSingular+"s");
        config.currencyFractional = savedConfig.getBoolean("currency.fractional", true);

        config.transactionTaxFlat = savedConfig.getDouble("transactiontax.flat", 0);
        config.transactionTaxRate = savedConfig.getDouble("transactiontax.rate", 0);
        
        config.usevaultContainer = savedConfig.getBoolean("usevault.container", true);
    }

    public void saveConfig(FileConfiguration savedConfig) {
        savedConfig.set("currency.type", config.currency.getTypeId());
        savedConfig.set("currency.datavalue", config.currency.getData().getData());
        savedConfig.set("currency.name.singular", config.currencyNameSingular);
        savedConfig.set("currency.name.plural", config.currencyNamePlural);
        savedConfig.set("currency.fractional", config.currencyFractional);

        savedConfig.set("transactiontax.flat", config.transactionTaxFlat);
        savedConfig.set("transactiontax.rate", config.transactionTaxRate);
        savedConfig.set("usevault.container", config.usevaultContainer);
    }

}
