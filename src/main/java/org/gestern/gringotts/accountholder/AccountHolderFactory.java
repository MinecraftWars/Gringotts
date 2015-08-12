package org.gestern.gringotts.accountholder;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages creating various types of AccountHolder centrally.
 * 
 * @author jast
 *
 */
public class AccountHolderFactory {

    private final Map<String, AccountHolderProvider> accountHolderProviders = new LinkedHashMap<>();

    public AccountHolderFactory() {
        // linked HashMap maintains iteration order -> prefer player to be checked first
        accountHolderProviders.put("player", new PlayerAccountHolderProvider());

        // TODO support banks
        // TODO support virtual accounts
    }

    /**
     * Get an account holder with automatically determined type, based on the owner's id.
     * @param owner name of the account holder
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
     * @param type type of the account
     * @param owner name of the account holder
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
        public AccountHolder getAccountHolder(String uuidOrName) {
            if (uuidOrName == null) return null;
            OfflinePlayer player;
            try {
                UUID playerId = UUID.fromString(uuidOrName);
                player = Bukkit.getOfflinePlayer(playerId);
            } catch (IllegalArgumentException ignored) {
                // don't use getOfflinePlayer(String) because that will do a blocking web request
                // rather iterate this array, should be quick enough
                for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                    if (uuidOrName.equals(p.getName())) return new PlayerAccountHolder(p);
                }
                return null;
            }

            // if this player has ever played on the server, they are a legit account holder
            if (player.isOnline() || player.hasPlayedBefore())
                return new PlayerAccountHolder(player);
            else 
                return null;
        }

    }

}
