package org.gestern.gringotts.currency;

import org.bukkit.ChatColor;

/**
 * Representation of a denomination within a currency.
 * <p>
 * Note: this class has a natural ordering that is inconsistent with equals.
 * Specifically, the ordering is based purely on the value of the denomination, but not the type.
 */
public class Denomination implements Comparable<Denomination> {

    private final DenominationKey key;

    /**
     * Value of one unit of this denomination in cents.
     */
    private final long value;

    /**
     * The name of a single unit of this denomination. The unit name is determined by explicit configuration,
     * configured displayName, or default item name (in this order).
     */
    private final String unitName;

    /**
     * The name for units of this denomination (plural). The unit name is determined by explicit configuration,
     * configured displayName, or default item name (in this order).
     */
    private final String unitNamePlural;


    public Denomination(DenominationKey key, long value, String unitName, String unitNamePlural) {
        this.key = key;
        this.value = value;

        this.unitName = unitName + ChatColor.RESET;
        this.unitNamePlural = unitNamePlural + ChatColor.RESET;
    }

    @Override
    public int compareTo(Denomination other) {
        // sort in descending value order
        return Long.compare(other.value, this.value); // Faster method.
    }

    @Override
    public String toString() {
        return String.format("{Denomination} %s : %d", key.type, value);
    }

    /**
     * Identification information for this denomination.
     */
    public DenominationKey getKey() {
        return key;
    }

    /**
     * Value of one unit of this denomination in cents.
     */
    public long getValue() {
        return value;
    }

    /**
     * The name of a single unit of this denomination. The unit name is determined by explicit configuration,
     * configured displayName, or default item name (in this order).
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * The name for units of this denomination (plural). The unit name is determined by explicit configuration,
     * configured displayName, or default item name (in this order).
     */
    public String getUnitNamePlural() {
        return unitNamePlural;
    }
}
