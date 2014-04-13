package org.gestern.gringotts.event;

import org.bukkit.event.HandlerList;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Vault creation event triggered by a player.
 * @author jast
 *
 */
public class PlayerVaultCreationEvent extends VaultCreationEvent {

    private final SignChangeEvent cause;

    public PlayerVaultCreationEvent(String type, SignChangeEvent cause) {
        super(type);
        this.cause = cause;
    }

    /**
     * Get the player involved in creating the vault.
     * @return the player involved in creating the vault
     */
    public SignChangeEvent getCause() {
        return cause;
    }

    public static HandlerList getHandlerList() {
        return VaultCreationEvent.handlers;
        // TODO ensure we can actually have superclass handle these safely
        // TODO find out what I meant by that?
    }

}
