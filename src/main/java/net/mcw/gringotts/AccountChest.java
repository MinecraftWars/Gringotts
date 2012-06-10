package net.mcw.gringotts;

import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a storage unit for an account.
 * 
 * @author jast
 *
 */
public class AccountChest {
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// FIXME probably need to manually implement hashCode based on block location
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chest == null) ? 0 : chest.getBlock().hashCode());
		result = prime * result + ((sign == null) ? 0 : sign.getBlock().hashCode());
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
		if (chest == null) {
			if (other.chest != null)
				return false;
		} else if (!chest.equals(other.chest))
			return false;
		if (sign == null) {
			if (other.sign != null)
				return false;
		} else if (!sign.equals(other.sign))
			return false;
		return true;
	}

	/** Physical chest backing this chest representation. */
	public final Chest chest;
	/** Sign marking the chest as an account chest. */
	public final Sign sign;
	
	/** Account that this chest belongs to. */
	public final Account account;
	
	public AccountChest(Chest chest, Sign sign, Account account) {
		this.chest = chest;
		this.sign = sign;
		this.account = account;
		account.addChest(this);
	}
	
	
	/**
	 * Return balance of this chest.
	 * @return balance of this chest
	 */
	public long balance() {
		
		long count = 0;		
		for (ItemStack stack : chest.getInventory()) {
			if (stack.getData().equals(Gringotts.currency.getData()))
				count += stack.getAmount();
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
		
		int stacksize = Gringotts.currency.getMaxStackSize();
		Inventory inv = chest.getInventory();
		long remaining = value;		
		
		// fill up incomplete stacks
		while (remaining > 0) {
			ItemStack stack = new ItemStack(Gringotts.currency);
			if (remaining > stacksize)
				stack.setAmount(stacksize);
			else
				stack.setAmount((int)remaining);
			
			int returned = 0;
			for (ItemStack leftover : inv.addItem(stack).values())
				returned += leftover.getAmount();
				
			remaining = returned + stack.getAmount();
			if (returned > 0) break;
		}

		return value - remaining;
	}
	
	/**
	 * Attempts to remove given amount from this chest.
	 * If the amount is larger than available items, everything is removed and the number of
	 * removed items returned.
	 * @param value
	 * @return
	 */
	public long remove(long value) {
		long count = 0;
		// TODO: remove items stack by stack, count progress
		return count;
	}
	
	/**
	 * Triggered on destruction of physical chest or sign
	 */
	public void destroy() {
		account.removeChest(this);
		sign.getBlock().breakNaturally();
		// TODO implement 
	}	
}
