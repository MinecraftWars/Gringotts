package org.gestern.gringotts.event;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.gestern.gringotts.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.gestern.gringotts.Configuration.CONF;

/**
 * Listens for chest creation and destruction events.
 * 
 * @author jast
 *
 */
public class AccountListener implements Listener { 

    private final Pattern vaultPattern = Pattern.compile(CONF.vaultPattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    /**
     * Create an account chest by adding a sign marker over it.
     * 
     * @param event Event data.
     */
    @EventHandler
    public void createVault(SignChangeEvent event) {
        String line0 = event.getLine(0);

        Matcher match = vaultPattern.matcher(line0);

        // consider only signs with proper formatting
        if (match.matches()) {
            String type = match.group(1).toLowerCase();

            // default vault is player
            if (type.isEmpty()) type = "player";

            // is sign attached to a valid vault container?
            BlockState signBlock = event.getBlock().getState();
            if (signBlock instanceof Sign &&
                    Util.chestBlock((Sign)signBlock) != null) {

                // we made it this far, throw the event to manage vault creation
                VaultCreationEvent creation = new PlayerVaultCreationEvent(type, event);
                Bukkit.getServer().getPluginManager().callEvent(creation);
            }
        }
    }
}
