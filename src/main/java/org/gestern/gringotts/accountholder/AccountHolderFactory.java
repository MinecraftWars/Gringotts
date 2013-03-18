package org.gestern.gringotts.accountholder;

import static org.gestern.gringotts.dependency.Dependency.DEP;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.gestern.gringotts.Gringotts;

/**
 * Manages creating various types of AccountHolder centrally.
 * 
 * @author jast
 *
 */
public class AccountHolderFactory {

    private final Logger log = Gringotts.G.getLogger();

    /**
     * Get an account holder with automatically determined type, based on the owner's name.
     * @param owner
     * @return account holder for the given owner name, or null if none could be determined
     */
    public AccountHolder get(String owner) {    	
        OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
        // if this player has ever played on the server, they are a legit account holder
        if (player.isOnline() || player.hasPlayedBefore()) {
            return new PlayerAccountHolder(player);
        }

        if (DEP.factions.enabled()) {
            AccountHolder holder = DEP.factions.getAccountHolderByName(owner);
            if (holder != null) return holder;
        }

        if (DEP.towny.enabled()) {
            AccountHolder holder = DEP.towny.getAccountHolderByAccountName(owner);
            if (holder!=null) return holder;
        }

        // TODO support banks
        // TODO support virtual accounts

        log.fine("No account holder found for " + owner);

        return null;
    }

    /**
     * Get an account holder of known type.
     * @param type
     * @param owner
     * @return account holder of given type with given owner name, or null if none could be determined or type is not supported.
     */
    public AccountHolder get(String type, String owner) {
        if (type.equals("player")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
            // if this player has ever played on the server, they are a legit account holder
            if (player.isOnline() || player.getLastPlayed() > 0) {
                return new PlayerAccountHolder(player);
            }
            else return null;
        }

        if (DEP.factions.enabled() && type.equals("faction")) {
            AccountHolder holder = DEP.factions.getAccountHolderById(owner); // try by id first, then by "faction-id" form name
            if (holder==null) holder = DEP.factions.getAccountHolderByName(owner); // maybe a bit hacky? whatever.
            if (holder!=null) return holder;
        }

        if (DEP.towny.enabled() && (type.equals("town") || type.equals("nation")) ) {
            AccountHolder holder = DEP.towny.getAccountHolderByAccountName(owner);
            if (holder!=null) return holder;
        } 

        // no valid type
        return null;
    }


}
