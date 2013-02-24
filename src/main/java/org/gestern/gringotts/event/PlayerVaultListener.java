package org.gestern.gringotts.event;

import static org.gestern.gringotts.Permissions.createvault_player;
import static org.gestern.gringotts.Language.LANG;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.gestern.gringotts.accountholder.PlayerAccountHolder;


public class PlayerVaultListener implements Listener {

    @EventHandler
    public void vaultCreated(PlayerVaultCreationEvent event) {
        // some listener already claimed this event
        if (event.isValid()) return;

        // only interested in player vaults
        if (! event.getType().equals("player")) return;

        Player player = event.getCause().getPlayer();
        if (! createvault_player.allowed(player)) {
            player.sendMessage(LANG.vault_noVaultPerm);
            return;
        }

        event.setOwner(new PlayerAccountHolder(player));
        event.setValid(true);
    }
}
