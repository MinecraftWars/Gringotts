package org.gestern.gringotts.currency;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Representaiton of a currency. This contains information about the currency's denominations and their values.
 * @author jast
 *
 */
public class Currency {
	
	private final Map<Denomination, Long> denoms = new HashMap<Denomination, Long>();
	
	
	public final String name;
	public final String namePlural;
	
	public Currency(String name) {
		this(name, name+'s');
	}
	
	public Currency(String name, String namePlural) {
		this.name = name;
		this.namePlural = namePlural;
	}
	
	/**
	 * Add a denomination and value to this currency.
	 * @param d the denomination
	 * @param value the denomination's value
	 */
	public void addDenomination(Denomination d, long value) {
		denoms.put(d, value);
	}
	
	/**
	 * Free capacity of an item stack in terms of monetary value.
	 * The capacity is defined as the maximum denomination value * stack size for empty stacks,
	 * for stacks partially filled with denomination items, it is number of free slots * value of that denomination,
	 * for stacks with other item types, it is 0.
	 * @param inv
	 * @return free capacity of an item stack in terms of monetary value
	 */
	public long capacity(ItemStack stack) {
		long value = 0;
		if (stack == null || stack.getData().getItemType() == Material.AIR) {
			// TODO use a more direct access to highest denomination 
			// open slots * highest denomination
			value += denominations()[0].type.getMaxStackSize();
		} else {
			Denomination d = new Denomination(stack);
			Long val = denoms.get(d);
			if (val!=null)
				// free slots on this stack * denom item value
				value += val * (stack.getMaxStackSize() - stack.getAmount());	
		}
		
		return value;
	}
	
	/**
	 * Get the value of an item stack in cents.
	 * This is calculated by value_type * stacksize.
	 * If the given item stack is not a valid denomination, the value is 0;
	 * @param type
	 * @return
	 */
	public long value(ItemStack stack) {
		Denomination d = new Denomination(stack);
		Long val = denoms.get(d);
		return val!=null? val * stack.getAmount() : 0;
	}
	

//	/**
//	 * Attempt to add a value amount to an item stack. 
//	 * If the stack is empty, adds items of the highest denomination lesser or equal to the given value.
//	 * If the stack contains items, only those kind of items will be added, insofar they
//	 * @param stack
//	 * @param value
//	 * @return
//	 */
//	public long add(ItemStack stack, long value) {
//		Denomination d = denominationOf(stack);
//		Material type = stack.getType();
//		if (type == Material.AIR) {
//			Denomination maxDenom = highestDenomination(value);
//			long denomValue = denoms.get(maxDenom);
//			long itemCount = value/denomValue;
//			stack.getAmount()
//			stack.setAmount(amount)
//		}
//		return 0;
//	}
	
	/**
	 * Denominations of the currency, in order of their respective values, from highest to lowest.
	 * 
	 * @return
	 */
	public Denomination[] denominations() {
		// FIXME this probably sorts lowest to highest? and also by hashcode??
		Denomination[] d = denoms.keySet().toArray(new Denomination[0]);
		Arrays.sort(d);
		return d;
	}
	
	/**
	 * Get the denomination of an item stack.
	 * @param stack
	 * @return denomination for the item stack, or null if there is no such denomination
	 */
	private Denomination denominationOf(ItemStack stack) {
		Denomination d = new Denomination(stack);
		return denoms.containsKey(d)? d : null;
	}
	
	/**
	 * Get the highest denomination lesser than or equal to a given value.
	 * @param value
	 * @return
	 */
	private Denomination highestDenomination(long value) {
		
	}
	
}
