package org.gestern.gringotts;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Represents a storage unit for an account.
 * 
 * @author jast
 *
 */
public class AccountChest {

    private final Logger log = Bukkit.getLogger();

    private final Configuration config = Configuration.config;
    
    private final DAO dao = DAO.getDao();

    /** Sign marking the chest as an account chest. */
    public final Sign sign;
    
    /** Account this chest belongs to. */
    public final Account account;

    /** Account that this chest belongs to. */
    //	public final Account account;

    /**
     * 
     * @param sign
     * @param account
     */
    public AccountChest(Sign sign, Account account) {
        this.sign = sign;
        this.account = account;
    }
    
    private Chest chest() {
    	Block storage = sign.getBlock().getRelative(BlockFace.DOWN);
    	if (Material.CHEST.equals(storage.getType()))
    		return ((Chest)storage);
    	else
    		return null;
    }
    
    /**
     * Get inventory of this account chest.
     * @return inventory of this accountchest, if any. otherwise null.
     */
    private Inventory inventory() {
    	Chest chest = chest();
    	return (chest != null)? chest.getInventory() : null;
    }

    /**
     * Return balance of this chest.
     * @return balance of this chest
     */
    public long balance() {

    	Inventory inv = inventory();
    	if (inv==null) return 0;
    	
        long count = 0;	
        for (ItemStack stack : inv) {
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

    	Inventory inv = inventory();
    	if (inv==null) return 0;
    	
        long count = 0;
        for (ItemStack stack : inv) {
            Material currency = config.currency.getType();


            //If it's air or our currency material we can store a stack of it
            if( stack == null )
                count += currency.getMaxStackSize();
            else if( stack.getType() == currency )
                count += currency.getMaxStackSize() - stack.getAmount();

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

    	Inventory inv = inventory();
    	if (inv==null) return 0;
    	
        int stacksize = config.currency.getMaxStackSize();
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

    	Inventory inv = inventory();
    	if (inv==null) return 0;
    	
        int stacksize = config.currency.getMaxStackSize();
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
    	dao.destroyAccountChest(this);
        sign.getBlock().breakNaturally();
    }
    
    @Override
    public String toString() {
    	Location loc = sign.getLocation();
    	return "[vault] " 
    			+ loc.getBlockX() + ", "
    			+ loc.getBlockY() + ", "
    			+ loc.getBlockZ() + ", "
    			+ loc.getWorld();    			
    }

    /**
     * Connected chests that comprise the inventory of this account chest.
     * @return
     */
    private Chest[] connectedChests() {
        Inventory inv = inventory();
        if (inv == null)
        	return new Chest[0];
        
        if (inv instanceof DoubleChestInventory) {
            DoubleChestInventory dinv = (DoubleChestInventory)inv;
            Chest left = (Chest)(dinv.getLeftSide().getHolder());
            Chest right = (Chest)(dinv.getRightSide().getHolder());

            return new Chest[] {left, right};
        } else {
            return new Chest[] {(Chest)(inv.getHolder())};
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

    /**
     * Determine whether the chest of another AccountChest would be connected to this chest.
     * @param chest
     * @return
     */
	public boolean connected(AccountChest chest) {
		Chest myChest = chest();
		if (myChest == null)
			return false;
		
		Location myLoc = myChest.getLocation();
		for (Chest c : chest.connectedChests())
			if (c.getLocation().equals(myLoc))
				return true;
		
		return false;
	}

	public Account getAccount() {
		// TODO Auto-generated method stub
		return null;
	}

}
