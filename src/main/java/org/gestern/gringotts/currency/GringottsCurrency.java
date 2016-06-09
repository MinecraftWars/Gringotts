package org.gestern.gringotts.currency;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Representation of a currency. This contains information about the currency's denominations and their values.
 * The value is represented internally as "cents", that is, the smallest currency unit, and only gets transformed into display value
 * for communication with the user or vault.
 * 
 * @author jast
 */
public class GringottsCurrency {

    private final Map<DenominationKey,Denomination> denoms = new HashMap<>();
    private final List<Denomination> sortedDenoms = new ArrayList<>();

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

    /** Show balances and other currency values with individual denomination names. */
    public final boolean namedDenominations;

    /**
     * Create currency.
     * @param name name of currency
     * @param namePlural plural of currency name
     * @param digits decimal digits used in currency
     */
    public GringottsCurrency(String name, String namePlural, int digits, boolean namedDenominations) {
        this.name = name;
        this.namePlural = namePlural;
        this.digits = digits;
        this.namedDenominations = namedDenominations;

        // calculate the "unit" from digits. It's just a power of 10!
        int d=digits, u = 1;
        while (d-->0) u*=10;
        this.unit = u;
    }

    /**
     * Add a denomination and value to this currency.
     * @param type the denomination's item type
     * @param value the denomination's value
     */
    public void addDenomination(ItemStack type, double value, String unitName, String unitNamePlural) {
        DenominationKey k = new DenominationKey(type);
        Denomination d = new Denomination(k, centValue(value), unitName, unitNamePlural);
        denoms.put(k, d);
        // infrequent insertion, so I don't mind sorting on every insert
        sortedDenoms.add(d);
        Collections.sort(sortedDenoms);
    }


    /**
     * Get the value of an item stack in cents.
     * This is calculated by value_type * stacksize.
     * If the given item stack is not a valid denomination, the value is 0;
     * @param stack a stack of items
     * @return the value of given stack of items
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
     * @param value display value
     * @return Gringotts-internal value of given amount
     */
    public long centValue(double value) {
        return Math.round(value * unit);
    }


    /**
     * List of denominations used in this currency, in order of descending value.
     * @return Unmodifiable List of denominations used in this currency, in order of descending value
     */
    public List<Denomination> denominations() {
        return Collections.unmodifiableList(sortedDenoms);
    }

    public String format(String formatString, double value) {

        if (namedDenominations) {

            StringBuilder b = new StringBuilder();

            long cv = centValue(value);

            for (Denomination denom : sortedDenoms) {
                long dv = cv / denom.value;
                cv %= denom.value;

                if (dv > 0) {
                    String display = dv + " " + (dv == 1l ? denom.unitName : denom.unitNamePlural);
                    b.append(display);
                    if (cv > 0) b.append(", ");
                }
            }

            // might need this check for fractional values
            if (cv > 0 || b.length() == 0) {
                double displayVal = displayValue(cv);
                b.append(String.format(formatString, displayVal, displayVal==1.0? name : namePlural));
            }

            return b.toString();

        } else return String.format(formatString, value, value==1.0? name : namePlural);

    }

    /**
     * Get the denomination of an item stack.
     * @param stack the stack to get the denomination for
     * @return denomination for the item stack, or null if there is no such denomination
     */
    private Denomination denominationOf(ItemStack stack) {
        DenominationKey d = new DenominationKey(stack);
        return denoms.get(d);
    }

    @Override
    public String toString() {
        return StringUtils.join(sortedDenoms, '\n');
    }
}
