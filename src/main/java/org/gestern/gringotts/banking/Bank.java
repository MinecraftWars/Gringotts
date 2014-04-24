package org.gestern.gringotts.banking;

import java.util.TreeSet;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.accountholder.AccountHolder;

public class Bank implements Corporation {

    public final String name;
    private TreeSet<UUID> owners;

    public Bank(String name) {
        this.name = name;
        owners = new TreeSet<UUID>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return "bank."+name+".reserve";
    }

    @Override
    public String getType() {
        return "bank";
    }

    @Override
    public TreeSet<UUID> getOwners() {
        return owners;
    }

    @Override
    public boolean isOwner(UUID id) {
        return owners.contains(id);
    }

    @Override
    public boolean addOwner(UUID id) {
        if(owners.contains(id)) return false;
        owners.add(id);
        return true;
    }

    @Override
    public boolean removeOwner(UUID id) {
        if (owners.contains(id)) {
            owners.remove(id);
            return true;
        }
        return false;
    }

    public AccountHolder getAccountHolder(String playerName) {
        if (playerName == null) {
            return null;
        }
        
        OfflinePlayer player = Gringotts.G.getServer().getOfflinePlayer(playerName);
        if (player == null) {
            return null;
        }
        return new BankAccountHolder(player, this);
    }

    public void sendMessage(String message) {

    }
}
