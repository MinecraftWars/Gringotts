package org.gestern.gringotts.banking;

import java.util.Set;
import java.util.HashSet;

import org.bukkit.OfflinePlayer;
import org.gestern.gringotts.Gringotts;

public class Bank {
    public final String name;
    private final Set<OfflinePlayer> managers = new HashSet<OfflinePlayer>();

    public Bank(String name) {
        this.name = name;
    }

    public Set<OfflinePlayer> getManagers() {
        return this.managers;
    }

    public boolean isManager(OfflinePlayer player) {
        return this.managers.contains(player);
    }

    public boolean addManager(OfflinePlayer player) {
        this.managers.add(player);
        return true;
    }

    public boolean removeManager(OfflinePlayer player) {
        if (this.managers.contains(player)) {
            this.managers.remove(player);
            return true;
        }
        return false;
    }

    public FinanceHolder getReserve() {
        return new FinanceHolder(this, "bankr");
    }

    public FinanceHolder getTrading() {
        return new FinanceHolder(this, "bankt");
    }

    public BankAccountHolder getAccountHolder(String playerName) {
        if (playerName == null) {
            return null;
        }
        
        OfflinePlayer player = Gringotts.G.getServer().getOfflinePlayer(playerName);
        if (player == null) {
            return null;
        }
        return new BankAccountHolder(player, this);
    }
}
