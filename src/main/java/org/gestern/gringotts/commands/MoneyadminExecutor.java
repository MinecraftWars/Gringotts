package org.gestern.gringotts.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.api.Account;
import org.gestern.gringotts.api.TransactionResult;

import static org.gestern.gringotts.Language.LANG;
import static org.gestern.gringotts.api.TransactionResult.SUCCESS;

/**
 * Admin commands for managing ingame aspects.
 */
public class MoneyadminExecutor extends GringottsAbstractExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        String command;
        if (args.length >= 2) {
            command = args[0];
        } else {
            return false;
        }

        // admin command: x of player / faction
        if ("b".equalsIgnoreCase(command)) { // You already check if that's true on line 199

            String targetAccountHolderStr = args[1];

            // explicit or automatic account type
            Account target = args.length == 3 ?
                    eco.custom(args[2], targetAccountHolderStr) :
                    eco.account(targetAccountHolderStr);

            if (!target.exists()) {
                invalidAccount(sender, targetAccountHolderStr);

                return false;
            }

            String formattedBalance = eco.currency().format(target.balance());
            String senderMessage = LANG.moneyadmin_b
                    .replace(TAG_BALANCE, formattedBalance)
                    .replace(TAG_PLAYER, targetAccountHolderStr);

            sender.sendMessage(senderMessage);

            return true;

        }

        // moneyadmin add/remove
        if (args.length >= 3) {
            String amountStr = args[1];
            double value;

            try {
                value = Double.parseDouble(amountStr);
            } catch (NumberFormatException ignored) {
                return false;
            }

            String targetAccountHolderStr = args[2];
            Account target = args.length == 4 ?
                    eco.custom(args[3], targetAccountHolderStr) :
                    eco.account(targetAccountHolderStr);

            if (!target.exists()) {
                invalidAccount(sender, targetAccountHolderStr);

                return false;
            }

            String formatValue = eco.currency().format(value);

            if ("add".equalsIgnoreCase(command)) {
                TransactionResult added = target.add(value);
                if (added == SUCCESS) {
                    String senderMessage = LANG.moneyadmin_add_sender.replace(TAG_VALUE, formatValue).replace
                            (TAG_PLAYER, target.id());
                    sender.sendMessage(senderMessage);
                    String targetMessage = LANG.moneyadmin_add_target.replace(TAG_VALUE, formatValue);
                    target.message(targetMessage);
                } else {
                    String errorMessage = LANG.moneyadmin_add_error.replace(TAG_VALUE, formatValue).replace
                            (TAG_PLAYER, target.id());
                    sender.sendMessage(errorMessage);
                }

                return true;

            } else if ("rm".equalsIgnoreCase(command)) {
                TransactionResult removed = target.remove(value);

                if (removed == SUCCESS) {
                    String senderMessage = LANG.moneyadmin_rm_sender
                            .replace(TAG_VALUE, formatValue)
                            .replace(TAG_PLAYER, target.id());

                    sender.sendMessage(senderMessage);

                    String targetMessage = LANG.moneyadmin_rm_target
                            .replace(TAG_VALUE, formatValue);

                    target.message(targetMessage);
                } else {
                    String errorMessage = LANG.moneyadmin_rm_error
                            .replace(TAG_VALUE, formatValue)
                            .replace(TAG_PLAYER, target.id());

                    sender.sendMessage(errorMessage);
                }

                return true;
            }
        }

        return false;
    }
}
