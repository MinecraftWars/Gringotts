package org.gestern.gringotts;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class Util {
	
    //Have these as utility functions for easy tweaking of conversion

	/**
	 * Conversion emeralds -> cents
	 * @param emeralds
	 * @return
	 */
    public static long toCents( double emeralds ) {
        return (long)(emeralds * 100);
    }

    /**
     * Conversion cents -> emeralds
     * @param cents
     * @return
     */
    public static double toEmeralds( long cents ) { 
        return (double)(cents) / 100.0;
    }
    
    /**
     * Check whether a block is a sign or wall sign type.
     * @param block
     * @return true if the block is a sign or wall sign
     */
    public static boolean isSignBlock(Block block) {
    	return block.getState() instanceof Sign;
    }
    
    /**
     * Compares whether a version string in standard format (dotted decimals) is greater than another.
     * @param version
     * @param atLeast
     * @return true if version is greater than greaterThanVersion, false otherwise
     */
     public static boolean versionAtLeast(String version, String atLeast) {
    	
    	int[] versionParts = versionParts(version);
    	int[] atLeastParts = versionParts(atLeast);
    	
    	for (int i=0; i<versionParts.length && i<atLeastParts.length; i++) {
    		// if any more major version part is larger, our version is newer
    		if (versionParts[i] > atLeastParts[i])
    			return true;
    		else if (versionParts[i] < atLeastParts[i])
    			return false;
    	}
    	
    	// the at least version has more digits
    	if (atLeastParts.length > versionParts.length)
    		return false;
    		
    	// supposedly the versions are equal
    	return true;
    }
    
    /**
     * Break a version string into parts.
     * @param version
     * @return
     */
    public static int[] versionParts(String version) {
    	String[] strparts = version.split("\\.");
    	int[] parts = new int[strparts.length];
    	
    	for (int i=0; i<strparts.length; i++) {
    		// just cut off any non-number part
    		String number = strparts[i].replaceAll("(\\d+).*","$1");
    		int part = 0;
    		try { part = Integer.parseInt(number); }
    		catch(NumberFormatException e) {}
    		
    		parts[i] = part;
    	}
    	
    	return parts;
    }
    
    /**
     * Get the name of the currency based on the value (singular or plural).
     * @return
     */
    public static String currencyName(double value) {
    	return value==1.0? Configuration.config.currencyNameSingular : Configuration.config.currencyNamePlural;
    }
}