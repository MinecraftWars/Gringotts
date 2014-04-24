package org.gestern.gringotts.banking;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.gestern.gringotts.accountholder.AccountHolder;

public class BankAccountHolder implements AccountHolder {

    public final OfflinePlayer player;
    public final Bank bank;

    public BankAccountHolder(OfflinePlayer player, Bank bank) {
        this.player = player;
        this.bank=bank;
    }

    public String getName() {
        return player.getName();
    }

    public String getType() {
        return "bank";
    }

    public String getId() {
        return bank.getId() + "." + player.getUniqueId();
    }

    public void sendMessage(String message) {
        if(player.isOnline()) {
            ((Player)player).sendMessage(message);
        }
    }

    public int hashCode() {
        return new HashCodeBuilder(19, 29).append(getType()).append(this.bank.name).append(this.player.getName()).toHashCode();
    }

    public boolean equals(Object object) {
        if(object instanceof BankAccountHolder) {
            return ((BankAccountHolder)object).getId().equalsIgnoreCase(getId());
        }
        return false;
    }
}
