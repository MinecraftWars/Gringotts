package org.gestern.gringotts;

import java.util.Map;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.gestern.gringotts.currency.Currency;
import org.gestern.gringotts.currency.Denomination;

/**
 * Account inventories define operations that can be used on all inventories belonging to an account.
 * 
 * @author jast
 *
 */
public class AccountInventory {
	
	private final Inventory inventory;

	public AccountInventory(Inventory inventory) {
		this.inventory = inventory;
	}
	
	/**
	 * Free capacity of this inventory in terms of monetary value.
	 * Depending on the exact value being added and resulting stack sizes, the actual capacity may be more or less than
	 * the capacity returned by this method. Users should not rely on this method to ascertain a transaction can be completed.
	 * The capacity is defined as 
	 * <code>
	 * {free slots} * {maximum denomination value} * {maximum denomination slot size} +
	 * sum({stacks with valid coins in them} * {free coin slots in stack} * {coin value})
	 * </code>
	 * 
	 * @return 
	 */
	// TODO capacity is a bit of an unclear concept with multiple denominations. 
	// instead, create and use a hasCapacity(int) method
	// such a method can determine if there is enough space with optimal stacking
	// or maybe not? maybe safest is just adding stuff and rolling back on failure?
	public long capacity(Inventory inv) {
		Currency cur = Configuration.config.currency;
		long value = 0;
		for (ItemStack stack : inventory) {
			value += cur.value(stack);
		}
		
		return value;
	}
	
	/**
	 * Current balance of this inventory in cents (or rather atomic currency units).
	 * @return current balance of this inventory in cents
	 */
	public long balance() {
		Currency cur = Configuration.config.currency;
		long count = 0;	
        for (ItemStack stack : inventory)
        	count += cur.value(stack);

        return count;
	}

	/**
	 * Add items to this inventory corresponding to given value.
	 * If the amount is larger than available space, the space is filled and the actually
     * added amount returned.
	 * @param value
	 * @return amount actually added
	 */
    public long add(long value) {
		Currency cur = Configuration.config.currency;
        long remaining = value;
        
        Denomination[] denoms = cur.denominations();
        
        // try denominations from largest to smallest
        for(Denomination denom : denoms) {
        	denom.value;
        }

        // fill up incomplete stacks
        while (remaining > 0) {
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
	 * Remove a items from this inventory corresponding to given value.
	 * @param value
	 * @return
	 */
	public long remove(long value) {
		return 0;
	}
	
	/**
	 * Find a good distribution of denominations to represent the value.
	 * The implementation should minimize the amount of stacks used.
	 * @param value
	 * @return
	 */
	private Map<Integer, Integer> stacking(long value) {
		return null;
	}
}
