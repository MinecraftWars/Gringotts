package org.gestern.gringotts;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gestern.gringotts.api.*;
import org.gestern.gringotts.api.impl.GringottsEco;

import static org.gestern.gringotts.Language.LANG;
import static org.gestern.gringotts.Permissions.COMMAND_DEPOSIT;
import static org.gestern.gringotts.Permissions.COMMAND_WITHDRAW;
import static org.gestern.gringotts.api.TransactionResult.SUCCESS;


/**
 * Handlers for player and console commands.
 * 
 * @author jast
 *
 */
class Commands {

    private static final String TAG_BALANCE = "%balance";

    private static final String TAG_PLAYER = "%player";

    private static final String TAG_VALUE = "%value";

    private final Gringotts plugin;

    private final Eco eco = new GringottsEco();


    public Commands(Gringotts plugin) {
        this.plugin = plugin;
    }

    /**
     * Player commands.
     */
    public class Money implements CommandExecutor{
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

            Player player;
            if (sender instanceof Player) {
                player = (Player)sender;
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

            String command = "";
            if (args.length >= 1) {
                command = args[0];
            }

            double value = 0;
            if (args.length >= 2) {
                try { value = Double.parseDouble(args[1]); } 
                catch (NumberFormatException ignored) { return false; }

                if ("withdraw".equals(command)) {
                    withdraw(player, value);
                    return true;
                } else if ("deposit".equals(command)) {
                    deposit(player, value);
                    return true;
                }
            } 

            // money pay <amount> <player>
            if(args.length == 3 && "pay".equals(command)) {
                return pay(player, value, args);
            }

            return false;
        }
    }



    private boolean pay(Player player, double value, String[] args) {
        if (!Permissions.TRANSFER.allowed(player)) {
            player.sendMessage(LANG.noperm);
            return true;
        }

        String recipientName = args[2];

        Account from = eco.player(player.getUniqueId());
        Account to = eco.account(recipientName);

        PlayerAccount playerAccount = eco.player(player.getUniqueId());

        TaxedTransaction transaction = playerAccount.send(value).withTaxes();
        TransactionResult result = playerAccount.send(value).withTaxes().to(eco.player(recipientName));

        double tax = transaction.tax();
        double valueAdded = value + tax;

        String formattedBalance = eco.currency().format(from.balance());
        String formattedValue = eco.currency().format(value);
        String formattedValuePlusTax = eco.currency().format(valueAdded);
        String formattedTax = eco.currency().format(tax);
        
        switch (result) {
        case SUCCESS:
            String succTaxMessage = LANG.pay_success_tax.replace(TAG_VALUE, formattedTax);
            String succSentMessage = LANG.pay_success_sender.replace(TAG_VALUE, formattedValue).replace(TAG_PLAYER, recipientName);
            from.message(succSentMessage + (tax>0? succTaxMessage : ""));
            String succReceivedMessage = LANG.pay_success_target.replace(TAG_VALUE, formattedValue).replace(TAG_PLAYER, player.getName());
            to.message(succReceivedMessage);
            return true;
        case INSUFFICIENT_FUNDS:
            String insFMessage = LANG.pay_insufficientFunds.replace(TAG_BALANCE, formattedBalance).replace(TAG_VALUE, formattedValuePlusTax);
            from.message(insFMessage);
            return true;
        case INSUFFICIENT_SPACE:
            String insSSentMessage = LANG.pay_insS_sender.replace(TAG_PLAYER, recipientName).replace(TAG_VALUE, formattedValue);
            from.message(insSSentMessage);
            String insSReceiveMessage = LANG.pay_insS_target.replace(TAG_PLAYER, from.id()).replace(TAG_VALUE, formattedValue);
            to.message(insSReceiveMessage);
            return true;
        default:
            String error = LANG.pay_error.replace(TAG_VALUE, formattedValue).replace(TAG_PLAYER, recipientName);
            from.message(error);
            return true;
        }
    }

    private void deposit(Player player, double value) {

        if (COMMAND_DEPOSIT.allowed(player)) {
            TransactionResult result = eco.player(player.getUniqueId()).deposit(value);
            String formattedValue = eco.currency().format(value);
            if (result == SUCCESS) {
                String success = LANG.deposit_success.replace(TAG_VALUE, formattedValue);
                player.sendMessage(success);
            } else {
                String error = LANG.deposit_error.replace(TAG_VALUE, formattedValue);
                player.sendMessage(error);
            }
        }
    }

    private void withdraw(Player player, double value) {
        if (COMMAND_WITHDRAW.allowed(player)) {
            TransactionResult result = eco.player(player.getUniqueId()).withdraw(value);
            String formattedValue = eco.currency().format(value);
            if (result == SUCCESS){
                String success = LANG.withdraw_success.replace(TAG_VALUE, formattedValue);
                player.sendMessage(success);
            }
            else{
                String error = LANG.withdraw_error.replace(TAG_VALUE, formattedValue);
                player.sendMessage(error);
            }
        }
    }

    /**
     * Admin commands for managing ingame aspects.
     */
    public class Moneyadmin implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

            String command;
            if (args.length >= 2) {
                command = args[0];
            } else return false;

            // admin command: x of player / faction
            if (args.length >= 2 && "b".equalsIgnoreCase(command))  {

                String targetAccountHolderStr = args[1];

                // explicit or automatic account type
                Account target = args.length==3? eco.custom(args[2], targetAccountHolderStr) : eco.account(targetAccountHolderStr);

                if (! target.exists()) {
                    invalidAccount(sender, targetAccountHolderStr);
                    return false;
                }

                String formattedBalance = eco.currency().format(target.balance());
                String senderMessage = LANG.moneyadmin_b.replace(TAG_BALANCE, formattedBalance).replace(TAG_PLAYER, targetAccountHolderStr);
                sender.sendMessage(senderMessage);
                return true;

            }

            // moneyadmin add/remove
            if (args.length >= 3) {
                String amountStr = args[1];
                double value;
                try { value = Double.parseDouble(amountStr); } 
                catch(NumberFormatException ignored) { return false; }

                String targetAccountHolderStr = args[2];
                Account target = args.length==4? eco.custom(args[3], targetAccountHolderStr) : eco.account(targetAccountHolderStr);
                if (! target.exists()) {
                    invalidAccount(sender, targetAccountHolderStr);
                    return false;
                }

                String formatValue = eco.currency().format(value);

                if ("add".equalsIgnoreCase(command)) {
                    TransactionResult added = target.add(value);
                    if (added == SUCCESS) {
                        String senderMessage = LANG.moneyadmin_add_sender.replace(TAG_VALUE, formatValue).replace(TAG_PLAYER, target.id());
                        sender.sendMessage(senderMessage);
                        String targetMessage = LANG.moneyadmin_add_target.replace(TAG_VALUE, formatValue);
                        target.message(targetMessage);
                    } else {
                        String errorMessage = LANG.moneyadmin_add_error.replace(TAG_VALUE, formatValue).replace(TAG_PLAYER, target.id());
                        sender.sendMessage(errorMessage);
                    }

                    return true;

                } else if ("rm".equalsIgnoreCase(command)) {
                    TransactionResult removed = target.remove(value); 
                    if (removed == SUCCESS) {
                        String senderMessage = LANG.moneyadmin_rm_sender.replace(TAG_VALUE, formatValue).replace(TAG_PLAYER, target.id());
                        sender.sendMessage(senderMessage);
                        String targetMessage = LANG.moneyadmin_rm_target.replace(TAG_VALUE, formatValue);
                        target.message(targetMessage);
                    } else {
                        String errorMessage = LANG.moneyadmin_rm_error.replace(TAG_VALUE, formatValue).replace(TAG_PLAYER, target.id());
                        sender.sendMessage(errorMessage);
                    }

                    return true;
                }
            }
        
            return false; 
        }
    }

    /**
     * Administrative commands not related to ingame money.
     */
    public class GringottsCmd implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

            if (args.length >=1 && "reload".equalsIgnoreCase(args[0])) {
                plugin.reloadConfig();
                sender.sendMessage(LANG.reload);
                return true;
            }

            return false;
        }

    }


    private void balanceMessage(Account account) {

        account.message(LANG.balance.replace(TAG_BALANCE, eco.currency().format(account.balance())));

        if (Configuration.CONF.balanceShowVault)
            account.message(LANG.vault_balance.replace(TAG_BALANCE, eco.currency().format(account.vaultBalance())));

        if (Configuration.CONF.balanceShowInventory)
            account.message(LANG.inv_balance.replace(TAG_BALANCE, eco.currency().format(account.invBalance())));
    }

    private static void invalidAccount(CommandSender sender, String accountName) {
        sender.sendMessage(LANG.invalid_account.replace(TAG_PLAYER, accountName));
    }

}
