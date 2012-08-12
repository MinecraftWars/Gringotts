package org.gestern.gringotts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.BlockVector;

/**
 * Represents a storage unit for an account.
 * 
 * @author jast
 *
 */
public class AccountChest implements ConfigurationSerializable {

    private final Logger log = Bukkit.getLogger();

    private final Configuration config = Configuration.config; 

    /** Physical chest backing this chest representation. */
    public final Chest chest;
    /** Sign marking the chest as an account chest. */
    public final Sign sign;

    /** Account that this chest belongs to. */
    //	public final Account account;

    public AccountChest(Chest chest, Sign sign) {
        if (chest == null || sign == null)
            throw new IllegalArgumentException("chest and sign arguments may not be null");

        this.chest = chest;
        this.sign = sign;
    }

    /**
     * Deserialization ctor.
     * @param serialized
     */
    public AccountChest(Map<String,Object> serialized) {
        log.finest("[Gringotts] deserializing AccountChest");

        String worldName = (String) serialized.get("world");
        World world = Bukkit.getWorld(worldName);

        BlockVector chestBV = (BlockVector) serialized.get("chest");
        this.chest = (Chest) chestBV.toLocation(world).getBlock().getState();

        BlockVector signBV = (BlockVector) serialized.get("sign");
        this.sign = (Sign) signBV.toLocation(world).getBlock().getState();

        //		this.account = (Account) serialized.get("account");
    }

    /**
     * Return balance of this chest.
     * @return balance of this chest
     */
    public long balance() {

        long count = 0;	
        for (ItemStack stack : chest.getInventory()) {
            Material material = config.currency.getType();
            if (stack == null || material != stack.getType())
                continue;

            MaterialData currencyData = config.currency.getData();
            MaterialData stackData = stack.getData();
            if (currencyData == null || currencyData.getData() == stackData.getData()) {
                count += stack.getAmount();
            }

        }

        return count;
    }

    /**
     * Return the capacity of this chest.
     * @return capacity of this chest
     */
    public long capacity() {

        long count = 0;
        for (ItemStack stack : chest.getInventory()) {
            Material currency = config.currency.getType();


            //If it's air or our currency material we can store a stack of it
            if( stack == null )
                count += currency.getMaxStackSize();
            else if( stack.getType() == currency )
                count += currency.getMaxStackSize();

            //If not, the slot is blocked by something else so we can't store anything.

        }
        return count;
    }

    /**
     * Attempts to add given amount to this chest. 
     * If the amount is larger than available space, the space is filled and the actually
     * added amount returned.
     * @return amount actually added
     */
    public long add(long value) {

        int stacksize = config.currency.getMaxStackSize();
        Inventory inv = chest.getInventory();
        long remaining = value;		

        // fill up incomplete stacks
        while (remaining > 0) {
            ItemStack stack = new ItemStack(config.currency);
            int remainderStackSize = remaining > stacksize? stacksize : (int)remaining;
            stack.setAmount(remainderStackSize);

            int returned = 0;
            for (ItemStack leftover : inv.addItem(stack).values())
                returned += leftover.getAmount();

            // reduce remaining amount by whatever was deposited
            remaining -= remainderStackSize-returned;

            // stuff returned means no more space, leave this place
            if (returned > 0) break; 
        }

        return value - remaining;
    }

    /**
     * Attempts to remove given amount from this chest.
     * If the amount is larger than available items, everything is removed and the number of
     * removed items returned.
     * @param value
     * @return amount actually removed from this chest
     */
    public long remove(long value) {

        int stacksize = config.currency.getMaxStackSize();
        Inventory inv = chest.getInventory();
        long remaining = value;

        while (remaining > 0) {
            ItemStack stack = new ItemStack(config.currency);
            int remainderStackSize = remaining > stacksize? stacksize : (int)remaining;
            stack.setAmount(remainderStackSize);

            int returned = 0;
            for (ItemStack leftover : inv.removeItem(stack).values())
                returned += leftover.getAmount();

            // reduce remaining amount by whatever was deposited
            remaining -= remainderStackSize-returned;

            // stuff returned means no more space, leave this place
            if (returned > 0) break; 
        }

        return value - remaining;
    }

    /**
     * Triggered on destruction of physical chest or sign
     * @return Blocks belonging to this account chest.
     */
    public void destroy() {
        sign.getBlock().breakNaturally();
    }

    /**
     * Get Blocks belonging to this AccountChest-
     * @return Blocks belonging to this AccountChest-
     */
    public Set<Block> getBlocks() {
        Set<Block> blocks = new HashSet<Block>();
        blocks.add(chest.getBlock());
        blocks.add(sign.getBlock());

        return blocks;
    }


    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<String, Object>();
        serialized.put("world", chest.getBlock().getWorld().getName());
        serialized.put("chest", chest.getBlock().getLocation().toVector().toBlockVector());
        serialized.put("sign", sign.getBlock().getLocation().toVector().toBlockVector());
        //		serialized.put("account", account);
        return serialized;
    }

    /**
     * Connected chests that comprise the inventory of this account chest.
     * @return
     */
    public Chest[] connectedChests() {
        Inventory inv = chest.getInventory();
        if (inv instanceof DoubleChestInventory) {
            DoubleChestInventory dinv = (DoubleChestInventory)inv;
            Chest left = (Chest)(dinv.getLeftSide().getHolder());
            Chest right = (Chest)(dinv.getRightSide().getHolder());

            return new Chest[] {left, right};
        } else {
            return new Chest[] {chest};
        }
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + sign.getLocation().hashCode();
        return result;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        // FIXME probably need to manually implement based on block locations
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        AccountChest other = (AccountChest) obj;
        return sign.getLocation().equals(other.sign.getLocation());
    }

}
