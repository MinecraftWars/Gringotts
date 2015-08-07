package org.gestern.gringotts.currency;

import net.milkbowl.vault.item.Items;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Hashable information to identify a denomination by it's ItemStack.
 */
public class DenominationKey {

    /** Item type of this denomination. */
    public final ItemStack type;


    /**
     * Create a denomination key based on an item stack.
     * @param type
     */
    public DenominationKey(ItemStack type) {
        this.type = new ItemStack(type);
        type.setAmount(0);

    }

    /** Derived name for this denomination. */
    public String getName() {
        String name = Items.itemByStack(type).getName(); // default
        ItemMeta meta = type.getItemMeta();
        if (type.hasItemMeta() && meta.hasDisplayName())
            name = meta.getDisplayName();

        return name;
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
