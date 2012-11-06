package org.gestern.gringotts.currency;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
	
	/**
	 * Denominations of the currency, in order of their respective values.
	 * @return
	 */
	public Denomination[] denominations() {
		Denomination[] d = denoms.keySet().toArray(new Denomination[0]);
		Arrays.sort(d);
		return d;
	}
	
}
