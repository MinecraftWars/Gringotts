package org.gestern.gringotts;

import org.bukkit.entity.Player;

public enum Permissions {
    USE_VAULT_INVENTORY("gringotts.usevault.inventory"),
    USE_VAULT_ENDERCHEST("gringotts.usevault.enderchest"),

    CREATE_VAULT_ADMIN("gringotts.createvault.admin"),
    CREATE_VAULT_PLAYER("gringotts.createvault.player"),
    CREATE_VAULT_FACTION("gringotts.createvault.faction"),
    CREATE_VAULT_TOWN("gringotts.createvault.town"),
    CREATE_VAULT_NATION("gringotts.createvault.nation"),
    CREATE_VAULT_WORLDGUARD("gringotts.createvault.worldguard"),

    TRANSFER("gringotts.transfer"),
    COMMAND_WITHDRAW("gringotts.command.withdraw"),
    COMMAND_DEPOSIT("gringotts.command.deposit");

    public final String node;

    Permissions(String node) {
        this.node = node;
    }

    /**
     * Check if a player has this permission.
     *
     * @param player player to check
     * @return whether given player has this permission
     */
    public boolean isAllowed(Player player) {
        return player.hasPermission(this.node);
    }
}
