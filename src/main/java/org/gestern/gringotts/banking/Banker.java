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
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.accountholder.AccountHolderProvider;
import org.gestern.gringotts.data.DAO;
import org.gestern.gringotts.event.PlayerVaultCreationEvent;

import static org.gestern.gringotts.Gringotts.G;

public class Banker
        implements AccountHolderProvider, Listener {
    private final Logger log = G.getLogger();
    private final Accounting accounting = G.accounting;
    private final DAO dao = G.dao;

    public Banker() {
        G.accountHolderFactory.registerAccountHolderProvider("bank", this);
        G.getServer().getPluginManager().registerEvents(this, G);
    }

    public AccountHolder getAccountHolder(String id) {
        if (id == null) {
            return null;
        }
        String[] parsed = id.split(".");
        if (parsed.length!=2) return null;
        String account = parsed[0];
        Bank bank = getBank(parsed[1]);
        String ownerId = parsed[2];
        if(ownerId.equalsIgnoreCase("reserve")) {
            return bank;
        }
        else return bank.getAccountHolder(ownerId);
    }

    public Bank getBank(String name) {
        if (name.contains(".")) {
            return null;
        }
        return new Bank(name);
    }

    @EventHandler
    public void vaultCreation(PlayerVaultCreationEvent event) {
        if (event.isValid()) {
            return;
        }
        if (event.getType().equals("reserve")) {
            Bank bank = getBank(event.getCause().getLine(2));
            if (bank == null) {
                event.getCause().getPlayer().sendMessage("You must specify a valid bank name on line 3.");
            } else {
                event.setOwner(bank);
            }
            event.setValid(true);
        }
    }
}
