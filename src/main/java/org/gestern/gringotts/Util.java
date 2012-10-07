package org.gestern.gringotts;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

class Util {
	
	private final Configuration config = Configuration.config;
	
    /**
     * Attempts to add given amount to a given inventory.
     * If the amount is larger than available space, the space is filled and the actually
     * added amount returned.
     * @return amount actually added
     */
    public long addToInventory(long value, Inventory inv) {
        int stacksize = config.currency.getMaxStackSize();
        long remaining = value;		

        // fill up incomplete stacks
        while (remaining > 0) {
            ItemStack stack = new ItemStack(config.currency);
            int remainderStackSize = remaining > stacksize? stacksize : (int)remaining;
            stack.setAmount(remainderStackSize);

            int returned = 0;
            for (ItemStack leftover : inv.addItem(stack).values())
                returned += leftover.getAmount();

            // reduce remaining amount by whatever was deposited
            remaining -= remainderStackSize-returned;

            // stuff returned means no more space, leave this place
            if (returned > 0) break; 
        }

        return value - remaining;
    }
    
    /**
     * Attempts to remove given amount from an inventory.
     * If the amount is larger than available items, everything is removed and the number of
     * removed items returned.
     * @param value
     * @return amount actually removed from this chest
     */
	public long removeFromInventory(long value, Inventory inv) {
		int stacksize = config.currency.getMaxStackSize();
        long remaining = value;
        
        while (remaining > 0) {
            ItemStack stack = new ItemStack(config.currency);
            int remainderStackSize = remaining > stacksize? stacksize : (int)remaining;
            stack.setAmount(remainderStackSize);

            int returned = 0;
            for (ItemStack leftover : inv.removeItem(stack).values())
                returned += leftover.getAmount();

            // reduce remaining amount by whatever was deposited
            remaining -= remainderStackSize-returned;

            // stuff returned means no more space, leave this place
            if (returned > 0) break; 
        }

        return value - remaining;
	}
	
	/**
	 * Return the capacity of the given inventory.
	 * @param inv
	 * @return
	 */
	public long capacityInventory(Inventory inv) {
        long count = 0;
        for (ItemStack stack : inv) {
            Material currency = config.currency.getType();

            //If it's air or our currency material we can store a stack of it
            if( stack == null )
                count += currency.getMaxStackSize();
            else if( stack.getType() == currency )
                count += currency.getMaxStackSize() - stack.getAmount();

            //If not, the slot is blocked by something else so we can't store anything.

        }
        return count;

	}
	
    /**
     * Return balance of the given inventory.
     * @param inv
     * @return balance of inventory
     */
	public long balanceInventory(Inventory inv) {
		long count = 0;	
        for (ItemStack stack : inv) {
            Material material = config.currency.getType();
            if (stack == null || material != stack.getType())
                continue;

            MaterialData currencyData = config.currency.getData();
            MaterialData stackData = stack.getData();
            if (currencyData == null || currencyData.getData() == stackData.getData()) {
                count += stack.getAmount();
            }

        }

        return count;
	}
   
    

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
    	Material type = block.getType();
    	return Material.SIGN == type || Material.WALL_SIGN == type;
    }




    
}
