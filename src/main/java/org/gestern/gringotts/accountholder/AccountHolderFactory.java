package org.gestern.gringotts.accountholder;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * Manages creating various types of AccountHolder centrally.
 * 
 * @author jast
 *
 */
public class AccountHolderFactory {

    private final Map<String, AccountHolderProvider> accountHolderProviders;

    public AccountHolderFactory() {
        // linked hashmap maintains iteration order -> prefer player to be checked first
        accountHolderProviders = new LinkedHashMap<String, AccountHolderProvider>();
        accountHolderProviders.put("player", new PlayerAccountHolderProvider());

        // TODO support banks
        // TODO support virtual accounts
    }

    /**
     * Get an account holder with automatically determined type, based on the owner's name.
     * @param owner
     * @return account holder for the given owner name, or null if none could be determined
     */
    public AccountHolder get(String owner) {

        for (AccountHolderProvider provider : accountHolderProviders.values()) {
            AccountHolder accountHolder = provider.getAccountHolder(owner);
            if (accountHolder != null) return accountHolder;
        }

        return null;
    }

    /**
     * Get an account holder of known type.
     * @param type
     * @param owner
     * @return account holder of given type with given owner name, or null if none could be determined or type is not supported.
     */
    public AccountHolder get(String type, String owner) {

        AccountHolderProvider provider = accountHolderProviders.get(type);

        AccountHolder accountHolder = null;
        if (provider != null)
            accountHolder = provider.getAccountHolder(owner);

        return accountHolder;
    }

    public void registerAccountHolderProvider(String type, AccountHolderProvider provider) {
        accountHolderProviders.put(type, provider);
    }

    private static class PlayerAccountHolderProvider implements AccountHolderProvider {

        @Override
        public AccountHolder getAccountHolder(String id) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(id);
            // if this player has ever played on the server, they are a legit account holder
            if (player.isOnline() || player.hasPlayedBefore())
                return new PlayerAccountHolder(player);
            else 
                return null;
        }

    }

}
