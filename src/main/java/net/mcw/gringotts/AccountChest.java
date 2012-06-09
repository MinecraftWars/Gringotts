package net.mcw.gringotts;

import org.bukkit.block.Chest;
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
	
	/** Physical chest backing this chest representation. */
	private final Chest chest;
	
	/** Account that this chest belongs to. */
	private Account account;
	
	public AccountChest(Chest chest, Account account) {
		this.chest = chest;
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
	 * Triggered on destruction of physical chest.
	 */
	private void destroy() {
		account.removeChest(this);
		// TODO implement 
	}
}
