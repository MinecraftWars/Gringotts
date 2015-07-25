package org.gestern.gringotts.event;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.gestern.gringotts.currency.Denomination;
import org.gestern.gringotts.currency.GringottsCurrency;

import static org.gestern.gringotts.Configuration.CONF;


/**
 * If set in the config, prevents money with display names from crafting anything other than other money
 * @author David Jones (Blue_Blaze72)
 */
public class CraftListener implements Listener {
	
	/**
	 * Runs when a player is about to craft an item
	 * @param event The event data associated with preparing a crafting recipe
	 */
	@EventHandler(ignoreCancelled=true)
	public void onPrepareItemCraft(PrepareItemCraftEvent event) {
		//If the result is something that could be a denomination (other than the name)
		//and that particular denomination is named, check that all the ingredients are denominations
		//If so, make the result named like a denomination
		Denomination similarDenomination = getSimilarDenomination(event.getRecipe().getResult());
		if(similarDenomination != null && !similarDenomination.displayName.isEmpty()) {
			craftNewMoney(event, similarDenomination);
		}
		
		//If the result isn't a denomination, then we need to make sure the ingredients also aren't denominations 
		//(we don't care if a denomination is used to craft another denomination)
		if(!CONF.isCraftable && CONF.currency.value(event.getInventory().getResult()) == 0) {
			preventMoneyCrafting(event);
		}
	}

	/**
	 * If all the ingredients from the given event are denominations, the result is set to the given denomination
	 * @param event The event of a craft recipe being prepared
	 * @param denomination The denomination that will be crafted if conditions are met
	 */
	private void craftNewMoney(PrepareItemCraftEvent event, Denomination denomination) {
		GringottsCurrency currency = CONF.currency;
		List<ItemStack> ingredients = Arrays.asList(event.getInventory().getMatrix());
		boolean areAllDenominations = true;
		for(ItemStack ingredient : ingredients) {
			if(ingredient != null && !ingredient.getType().equals(Material.AIR) && currency.value(ingredient) == 0) {
				areAllDenominations = false;
			}
		}
		if(areAllDenominations) {
			ItemStack newResult = denomination.type.clone();
			newResult.setAmount(event.getInventory().getResult().getAmount());
			event.getInventory().setResult(newResult);
		}
	}

	/**
	 * Checks if money is being used to craft and cancels it if so
	 * @param event The event of a crafting recipe being prepared
	 */
	private void preventMoneyCrafting(PrepareItemCraftEvent event) {
		GringottsCurrency currency = CONF.currency;
		List<Denomination> denominations = currency.denominations();
		List<ItemStack> ingredients = Arrays.asList(event.getInventory().getMatrix());
		for(Denomination denomination : denominations) {
			ItemStack type = denomination.type;
			String name = denomination.displayName;
			//if one of the ingredients is a named denomination, set the result to null, effectively "canceling" the event
			if(!name.isEmpty() && ingredients.contains(type)) {
				event.getInventory().setResult(null);
			}
		}
	}
	
	/**
	 * Checks if the given item could be a denomination (only checks material and durability)
	 * And returns that similar denomination
	 * @param item The item we are checking
	 * @return the similar denomination, if any
	 */
	private Denomination getSimilarDenomination(ItemStack item) {
		Denomination similarDenomination = null;
		Material type = item.getType();
		short damage = item.getDurability();
		for(Denomination denomination : CONF.currency.denominations()) {
			if(denomination.type.getType().equals(type) && denomination.damage == damage) {
				similarDenomination = denomination;
			}
		}
		return similarDenomination;
	}
}
