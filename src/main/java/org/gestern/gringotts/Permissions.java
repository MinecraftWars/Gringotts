package org.gestern.gringotts;

import org.bukkit.entity.Player;

public enum Permissions {
    USEVAULT_INVENTORY("gringotts.usevault.inventory"),
    USEVAULT_ENDERCHEST("gringotts.usevault.enderchest"),

    CREATEVAULT_ADMIN("gringotts.createvault.admin"),
    CREATEVAULT_PLAYER("gringotts.createvault.player"),
    CREATEVAULT_FACTION("gringotts.createvault.faction"),
    CREATEVAULT_TOWN("gringotts.createvault.town"),
    CREATEVAULT_NATION("gringotts.createvault.nation"),
    CREATEVAULT_WORLDGUARD("gringotts.createvault.worldguard"),

    TRANSFER("gringotts.transfer"),
    COMMAND_WITHDRAW("gringotts.command.withdraw"),
    COMMAND_DEPOSIT("gringotts.command.deposit")
    ;

    public final String node;
    Permissions(String node) {
        this.node = node;
    }

    /**
     * Check if a player has this permission.
     * @param player player to check
     * @return whether given player has this permission
     */
    public boolean allowed(Player player) {
        return player.hasPermission(this.node);
    }
}
