package org.gestern.gringotts.currency;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a denomination within a currency.
 * 
 * Note: this class has a natural ordering that is inconsistent with equals.
 * Specifically, the ordering is based purely on the value of the denomination, but not the type.
 * Conversely, the equality of denominations is based purely on their respective types, and their value is not regarded.
 * 
 * @author jast
 *
 */
public class Denomination implements Comparable<Denomination> {

    /** Item type of this denomination. */
    public final ItemStack type;
    public final Material material;
    public final short damage;
    public final long value;
    public final String displayName;
    public final List<String> lore;

    public Denomination(ItemStack type) {
        this(type, 0);
    }

    public Denomination(ItemStack type, long value) {
        this.type = type;
        this.material = type.getType();
        this.damage = type.getDurability();
        this.value = value;

        ItemMeta meta = type.getItemMeta();
        this.displayName = meta.hasDisplayName() ? meta.getDisplayName() : "";
        this.lore = meta.hasLore() ? meta.getLore() : new ArrayList<String>();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + damage;
        result = prime * result + material.hashCode();
        result = prime * result + displayName.hashCode();
        result = prime * result + lore.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Denomination other = (Denomination) obj;
        return damage == other.damage &&
                material.equals(other.material) &&
                displayName.equals(other.displayName) &&
                lore.equals(other.lore);
    }

    @Override
    public int compareTo(Denomination other) {
        // sort in descending value order
        return Long.valueOf(other.value).compareTo(this.value);
    }

    @Override
    public String toString() {
        String loreString = lore.isEmpty()? "" : " - " + StringUtils.join(lore, ", ");
        return String.format("{Denomination} %s%s : %s;%d : %d", displayName, loreString, material.toString(), damage, value);    }

}
