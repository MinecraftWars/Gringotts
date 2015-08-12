package org.gestern.gringotts.currency;

import org.bukkit.ChatColor;

/**
 * Representation of a denomination within a currency.
 * 
 * Note: this class has a natural ordering that is inconsistent with equals.
 * Specifically, the ordering is based purely on the value of the denomination, but not the type.
 *
 */
public class Denomination implements Comparable<Denomination> {

    /** Identification information for this denomination. */
    public final DenominationKey key;

    /** Value of one unit of this denomination in cents. */
    public final long value;

    /**
     * The name of a single unit of this denomination. The unit name is determined by explicit configuration,
     * configured displayName, or default item name (in this order).
     */
    public final String unitName;

    /**
     * The name for units of this denomination (plural). The unit name is determined by explicit configuration,
     * configured displayName, or default item name (in this order).
     */
    public final String unitNamePlural;


    public Denomination(DenominationKey key, long value, String unitName, String unitNamePlural) {
        this.key = key;
        this.value = value;

        this.unitName = unitName + ChatColor.RESET;
        this.unitNamePlural = unitNamePlural + ChatColor.RESET;
    }

    @Override
    public int compareTo(Denomination other) {
        // sort in descending value order
        return Long.valueOf(other.value).compareTo(this.value);
    }

    @Override
    public String toString() {
        return String.format("{Denomination} %s : %d", key.type, value);    }

}
