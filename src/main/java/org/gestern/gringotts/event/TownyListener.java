package org.gestern.gringotts.event;

import static org.gestern.gringotts.Language.LANG;
import static org.gestern.gringotts.Permissions.createvault_nation;
import static org.gestern.gringotts.Permissions.createvault_town;
import static org.gestern.gringotts.dependency.Dependency.DEP;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.gestern.gringotts.accountholder.AccountHolder;

public class TownyListener implements Listener {

    @EventHandler
    public void vaultCreated(PlayerVaultCreationEvent event) {
        // some listener already claimed this event
        if (event.isValid()) return;

        if (! DEP.towny.enabled()) return;

        Player player = event.getCause().getPlayer();
        AccountHolder owner = null;
        if (event.getType().equals("town")) {
            if (!createvault_town.allowed(player)) {
                player.sendMessage(LANG.plugin_towny_noTownVaultPerm);
                return;
            }

            owner = DEP.towny.getTownAccountHolder(player);
            if (owner == null) {
                player.sendMessage(LANG.plugin_towny_noTownResident);
                return;
            }

        } else if (event.getType().equals("nation")) {
            if (!createvault_nation.allowed(player)) {
                player.sendMessage(LANG.plugin_towny_noNationVaultPerm);
                return;
            }

            owner = DEP.towny.getNationAccountHolder(player);
            if (owner == null) {
                player.sendMessage(LANG.plugin_towny_notInNation);
                return;
            }
        }

        event.setOwner(owner);
        event.setValid(true);
    }
}
