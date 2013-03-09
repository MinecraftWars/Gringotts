package org.gestern.gringotts;

import org.bukkit.entity.Player;

public enum Permissions {
    usevault_inventory("gringotts.usevault.inventory"),
    usevault_enderchest("gringotts.usevault.enderchest"),

    createvault_player("gringotts.createvault.player"),
    createvault_faction("gringotts.createvault.faction"),
    createvault_town("gringotts.createvault.town"),
    createvault_nation("gringotts.createvault.nation"),
    createvault_worldguard("gringotts.createvault.worldguard"),

    transfer("gringotts.transfer"),
    command_withdraw("gringotts.command.withdraw"),
    command_deposit("gringotts.command.deposit")
    ;

    public final String node;
    Permissions(String node) {
        this.node = node;
    }

    /**
     * Check if a player has this permission.
     * @param player
     * @return
     */
    public boolean allowed(Player player) {
        return player.hasPermission(this.node);
    }
}
