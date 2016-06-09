
package org.gestern.gringotts.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.accountholder.PlayerAccountHolder;

import static org.gestern.gringotts.Language.LANG;
import static org.gestern.gringotts.Permissions.CREATEVAULT_ADMIN;
import static org.gestern.gringotts.Permissions.CREATEVAULT_PLAYER;

/**
 * This Vault listener handles vault creation events for player vaults.
 * 
 * @author jast
 */
public class PlayerVaultListener implements Listener {

    @EventHandler
    public void vaultCreated(PlayerVaultCreationEvent event) {
        // some listener already claimed this event
        if (event.isValid()) return;

        // only interested in player vaults
        if (!"player".equals(event.getType())) return;

        SignChangeEvent cause = event.getCause();
        String ownername = cause.getLine(2);

        Player player = cause.getPlayer();

        if (! CREATEVAULT_PLAYER.allowed(player)) {
            player.sendMessage(LANG.vault_noVaultPerm);
            return;
        }

        AccountHolder owner;
        if (ownername != null && ownername.length() > 0 && CREATEVAULT_ADMIN.allowed(player)) {
            // attempting to create account for other player
            owner = Gringotts.G.accountHolderFactory.get("player", ownername);
            if (owner==null) return;
        } else {
            // regular vault creation for self
            owner = new PlayerAccountHolder(player);
        }

        event.setOwner(owner);
        event.setValid(true);
    }
}
