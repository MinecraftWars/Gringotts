package org.gestern.gringotts;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.gestern.gringotts.currency.Currency;

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
	public long capacity() {
		Currency cur = Configuration.config.currency;

        for (ItemStack stack : inventory) {
        	long val = cur.value(stack)
        	if (cur.value(stack) > 0)
        }
		return 0;
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
	 * Add a items to this inventory corresponding to given value.
	 * @param value
	 * @return amount actually added
	 */
	public long add(long value) {
		return 0;
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
