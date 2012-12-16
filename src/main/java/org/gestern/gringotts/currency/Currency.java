package org.gestern.gringotts.currency;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Representaiton of a currency. This contains information about the currency's denominations and their values.
 * @author jast
 *
 */
public class Currency {
	
	private final SortedSet<Denomination> denoms = new TreeSet<Denomination>();
	
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
	public void addDenomination(Denomination d) {
		denoms.add(d);
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
		Denomination d = denominationOf(stack);
		if (stack == null || stack.getData().getItemType() == Material.AIR) { 
			// open slots * highest denomination
			Denomination highest = denoms.first();
			return highest.value * highest.type.getMaxStackSize();
		} else if (d!=null) {
			long val = d.value;
			// free slots on this stack * denom item value
			return val * (stack.getMaxStackSize() - stack.getAmount());
		} else
			return 0;
	}
	
	/**
	 * Get the value of an item stack in cents.
	 * This is calculated by value_type * stacksize.
	 * If the given item stack is not a valid denomination, the value is 0;
	 * @param type
	 * @return
	 */
	public long value(ItemStack stack) {
		Denomination d = denominationOf(stack);
		return d!=null? d.value * stack.getAmount() : 0;
	}
	

	/**
	 * List of denominations used in this currency, in order of descending value.
	 * @return
	 */
	public List<Denomination> denominations() {
		return new ArrayList<Denomination>(denoms);
	}
	
	/**
	 * Get the denomination of an item stack.
	 * @param stack
	 * @return denomination for the item stack, or null if there is no such denomination
	 */
	private Denomination denominationOf(ItemStack stack) {
		Denomination d = new Denomination(stack);
		return denoms.contains(d)? denoms.tailSet(d).first() : null;
	}
	
	
}
