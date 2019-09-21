package org.gestern.gringotts.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.gestern.gringotts.Language.LANG;

/**
 * Player commands.
 */
public class MoneyExecutor extends GringottsAbstractExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(LANG.playerOnly);
            return false;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            // same as balance
            sendBalanceMessage(eco.player(player.getUniqueId()));
            return true;
        }

        String command = args[0];

        double value = 0;
        if (args.length == 2) {
            try {
                value = Double.parseDouble(args[1]);
            } catch (NumberFormatException ignored) {
                return false;
            }

            if ("withdraw".equals(command)) {
                withdraw(player, value);

                return true;
            } else if ("deposit".equals(command)) {
                deposit(player, value);

                return true;
            }
        } else if ( args.length == 3 && "pay".equals(command)) {
            try {
                value = Double.parseDouble(args[1]);
            } catch (NumberFormatException ignored) {
                return false;
            }

            // money pay <amount> <player>
            return pay(player, value, args);
        }
        return false;
    }
}
