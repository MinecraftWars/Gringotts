package net.mcw.gringotts;

import org.bukkit.Material;
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
	
	/** Currency item type - default emerald. */
	public final ItemStack currency =  
			new ItemStack(Material.EMERALD, 1, (short)	0, (byte)0);
	
	/** Flat tax on every player-to-player transaction. This is a value in currency units. */
	public double transactionTaxFlat = 0;
	
	/** Rate tax on every player-to-player transaction. This is a fraction, e.g. 0.1 means 10% tax. */ 
	public double transactionTaxRate = 0;

}
