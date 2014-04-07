package org.gestern.gringotts.banking;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.gestern.gringotts.AccountChest;
import org.gestern.gringotts.Accounting;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.accountholder.AccountHolderProvider;
import org.gestern.gringotts.data.DAO;
import org.gestern.gringotts.event.PlayerVaultCreationEvent;

public class Banker
  implements AccountHolderProvider, Listener
{
  static Gringotts dobby;
  static Logger log;
  static Accounting cesare;
  static DAO harold;
  
  public Banker(Gringotts plugin)
  {
    dobby = plugin;
    log = dobby.getLogger();
    cesare = dobby.accounting;
    harold = dobby.dao;
    
    dobby.accountHolderFactory.registerAccountHolderProvider("bank", this);
    dobby.accountHolderFactory.registerAccountHolderProvider("bankr", this);
    dobby.accountHolderFactory.registerAccountHolderProvider("bankt", this);
    dobby.accountHolderFactory.registerAccountHolderProvider("bankp", this);
    dobby.getServer().getPluginManager().registerEvents(this, dobby);
  }
  
  public AccountHolder getAccountHolder(String id)
  {
    if (id == null) {
      return null;
    }
    String[] parsed = id.split("-");
    if ((parsed.length < 2) || (parsed.length > 3)) {
      return null;
    }
    String account = parsed[0];
    Bank bank = getBank(parsed[1]);
    String playerName = null;
    if (parsed.length == 3) {
      playerName = parsed[2];
    }
    if (account.equalsIgnoreCase("bankr")) {
      return bank.getReserve();
    }
    if (account.equalsIgnoreCase("bankt")) {
      return bank.getTrading();
    }
    if (account.equalsIgnoreCase("bankp")) {
      return bank.getAccountHolder(playerName);
    }
    return null;
  }
  
  public Bank getBank(String name)
  {
    if (name.contains("-")) {
      return null;
    }
    return new Bank(name);
  }
  
  public List<String> listBanks()
  {
    LinkedList<String> bankNames = new LinkedList<String>();
    return bankNames;
  }
  
  @EventHandler
  public void vaultCreation(PlayerVaultCreationEvent event)
  {
    if (event.isValid()) {
      return;
    }
    if (event.getType().equals("reserve"))
    {
      Bank bank = getBank(event.getCause().getLine(2));
      if (bank == null) {
        event.getCause().getPlayer().sendMessage("You must specify a valid bank name on line 3.");
      } else {
        event.setOwner(bank.getReserve());
      }
      event.setValid(true);
    }
    if (event.getType().equals("teller"))
    {
      Bank bank = getBank(event.getCause().getLine(2));
      if (bank == null) {
        event.getCause().getPlayer().sendMessage("You must specify a valid bank name on line 3.");
      } else {
        event.setOwner(bank.getTrading());
      }
      event.setValid(true);
    }
  }
  
  public void markTeller(Block tellerBlock)
  {
    log.info("Marking blocktype " + tellerBlock.getState().getTypeId());
    tellerBlock.setMetadata("GringottsTeller", new FixedMetadataValue(dobby, Boolean.valueOf(true)));
  }
  
  private void unmarkTeller(Block tellerBlock)
  {
    tellerBlock.removeMetadata("GringottsTeller", dobby);
  }
  
  private Set<AccountChest> getTellerChests()
  {
    return null;
  }
  
  @EventHandler
  public void accessTeller(InventoryOpenEvent event)
  {
    if ((event.getInventory().getHolder() instanceof Chest))
    {
      Chest chest = (Chest)event.getInventory().getHolder();
      if (chest.hasMetadata("GringottsTeller")) {
        log.info("meep");
      }
    }
  }
}
