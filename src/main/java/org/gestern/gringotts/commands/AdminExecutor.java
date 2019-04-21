package org.gestern.gringotts.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import static org.gestern.gringotts.Language.LANG;

/**
 * Administrative commands not related to ingame money.
 */
public class AdminExecutor extends GringottsExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length >= 1 && "reload".equalsIgnoreCase(args[0])) {
            plugin.reloadConfig();
            sender.sendMessage(LANG.reload);
            return true;
        }
        return false;
    }
}
