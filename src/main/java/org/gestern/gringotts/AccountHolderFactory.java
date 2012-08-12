package org.gestern.gringotts;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.massivecraft.factions.Factions;

public class AccountHolderFactory {

    public AccountHolder getAccount(String owner) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
        // if this player has ever played on the server, they are a legit account holder
        if (player.getLastPlayed() > 0)
            return new PlayerAccountHolder(player);

        if (owner.startsWith("faction-")) {
            String factionId = owner.substring(8);
            if (Factions.i.exists(factionId))
                return new FactionAccountHolder(Factions.i.get(factionId));
        }

        return null;
    }
}
