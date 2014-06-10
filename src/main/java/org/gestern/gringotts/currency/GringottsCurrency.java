package org.gestern.gringotts.currency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Representaiton of a currency. This contains information about the currency's denominations and their values.
 * The value is represented internally as "cents", that is, the smallest currency unit, and only gets transformed into display value
 * for communication with the user or vault.
 * 
 * @author jast
 */
public class GringottsCurrency {

    // yes, I want to be able to get the key from the key.
    // this is because I want to find a denomination's value based on its type.
    // TODO considering there are usually only very few denominations.. simplify this using just a simple friggin list or array
    private final Map<Denomination,Denomination> denoms = new HashMap<Denomination,Denomination>();
    private final List<Denomination> sortedDenoms = new ArrayList<Denomination>();

    /** Name of the currency. */
    public final String name;

    /** Name of the currency, plural version. */
    public final String namePlural;

    /** 
     * Currency unit divisor. Internally, all calculation is done in "cents". 
     * This multiplier changes the external representation.
     * For instance, with unit 100, every cent will be worth 0.01 currency units
     */
    public final int unit;

    /**
     * Fractional digits supported by this currency.
     * For example, with 2 digits the minimum currency value would be 0.01
     */
    public final int digits;

    /**
     * Create a Currency with given name and default plural name and 2 fractional digits.
     * @param name name of currency
     */
    public GringottsCurrency(String name) {
        this(name, name+'s', 2);
    }

    /**
     * Create currency.
     * @param name name of currency
     * @param namePlural plural of currency name
     * @param unit currency unit divisor
     */
    public GringottsCurrency(String name, String namePlural, int digits) {
        this.name = name;
        this.namePlural = namePlural;
        this.digits = digits;

        // calculate the "unit" from digits. It's just a power of 10!
        int d=digits, u = 1;
        while (d-->0) u*=10;
        this.unit = u;
    }

    /**
     * Add a denomination and value to this currency.
     * @param d the denomination
     * @param value the denomination's value
     */
    public Denomination addDenomination(ItemStack type, double value) {
        Denomination d = new Denomination(type, Math.round(centValue(value)));

        // Special-case for backwards compatibility for named denominations
        if (value == 1) {
            d.name = name;
            d.namePlural = namePlural;
        }
        denoms.put(d, d);
        // infrequent insertion, so I don't mind sorting on every insert
        sortedDenoms.add(d);
        Collections.sort(sortedDenoms);

        return d;
    }


    /**
     * Get the value of an item stack in cents.
     * This is calculated by value_type * stacksize.
     * If the given item stack is not a valid denomination, the value is 0;
     * @param type
     * @return
     */
    public long value(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) return 0;
        Denomination d = denominationOf(stack);
        return d!=null? d.value * stack.getAmount() : 0;
    }

    /**
     * The display value for a given cent value.
     * @param value value to calculate display value for
     * @return user representation of value
     */
    public double displayValue(long value) {
        return (double)value / unit;
    }

    /**
     * The internal calculation value of a display value.
     * @param value
     * @return
     */
    public long centValue(double value) {
        return Math.round(value * unit);
    }


    /**
     * List of denominations used in this currency, in order of descending value.
     * @return
     */
    public List<Denomination> denominations() {
        return new ArrayList<Denomination>(sortedDenoms);
    }

    /**
     * Get the denomination of an item stack.
     * @param stack
     * @return denomination for the item stack, or null if there is no such denomination
     */
    private Denomination denominationOf(ItemStack stack) {
        Denomination d = new Denomination(stack);
        return denoms.get(d);
    }

    public String format(String formatString, double value) {
        String output = "";
        String delimiter = "";
        int denomCount = sortedDenoms.size();
        for (int index = 0; index < denomCount; index++) {
            Denomination denomination = sortedDenoms.get(index);
            double denomationValue = denomination.value / unit;
            // Skip over unnamed denominations
            // This means the lowest denomination should probably have
            // a name, or you may not get output sometimes.
            // I think we'd need a list of just the named denominations
            // to really fix this.
            if (denomination.hasName()
                // If we haven't output anything yet, always process the final denomination
                && ((output.isEmpty() && index == denomCount -1 ) || value > denomationValue)
                // Sanity-check to avoid divide by zero error on misconfigured denominations
                && denomationValue != 0) {
                double denomAmount = value / denomationValue;
                value = value - Math.floor(denomAmount) * denomationValue;

                // Special-case lowest denomination, which gets decimal places for remainder.
                String formattedAmount = index == denomCount - 1 ? String.format(formatString, denomAmount) : Integer.toString((int)denomAmount);
                output = output + delimiter + formattedAmount + " " + (denomAmount==1.0? denomination.name : denomination.namePlural);
                delimiter = ", ";
            }
        }

        return output;
    }
}
