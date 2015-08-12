package org.gestern.gringotts.currency;

import org.bukkit.inventory.ItemStack;

/**
 * Hashable information to identify a denomination by it's ItemStack.
 */
public class DenominationKey {

    /** Item type of this denomination. */
    public final ItemStack type;


    /**
     * Create a denomination key based on an item stack.
     * @param type item type of denomination
     */
    public DenominationKey(ItemStack type) {
        this.type = new ItemStack(type);
        this.type.setAmount(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DenominationKey that = (DenominationKey) o;

        return type.equals(that.type);

    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
