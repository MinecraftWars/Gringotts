package org.gestern.gringotts;

import org.bukkit.Material;
import org.bukkit.block.Block;

public final class Util {

    //Have these as utility functions for easy tweaking of conversion

    static long toCents( double emeralds ) {
        return (long)(emeralds * 100);
    }

    static double toEmeralds( long cents ) { 
        return (double)(cents) / 100.0;
    }
    
    /**
     * Check whether a block is a sign or wall sign type.
     * @param block
     * @return true if the block is a sign or wall sign
     */
    static boolean isSignBlock(Block block) {
    	Material type = block.getType();
    	return Material.SIGN == type || Material.WALL_SIGN == type;
    }
    
}
