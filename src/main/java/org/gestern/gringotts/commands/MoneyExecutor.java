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

        Player player;
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            sender.sendMessage(LANG.playerOnly);

            return false;
            // TODO actually, refactor the whole thing already!
        }

        if (args.length == 0) {
            // same as balance
            balanceMessage(eco.player(player.getUniqueId()));
            return true;
        }

        String command; // You don't need to init the variable.
        // You already check if that's true on line 157.
        // if (args.length >= 1) {
        //     command = args[0];
        // }
        command = args[0];

        double value = 0;
        if (args.length >= 2) {
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
        }

        // money pay <amount> <player>
        return args.length == 3 && "pay".equals(command) && pay(player, value, args);
    }
}
