package org.gestern.gringotts;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gestern.gringotts.api.*;
import org.gestern.gringotts.api.impl.GringottsEco;

import static org.gestern.gringotts.Language.LANG;
import static org.gestern.gringotts.Permissions.command_deposit;
import static org.gestern.gringotts.Permissions.command_withdraw;
import static org.gestern.gringotts.api.TransactionResult.SUCCESS;


/**
 * Handlers for player and console commands.
 * 
 * @author jast
 *
 */
class Commands {

    private final Gringotts plugin;

    private final Eco eco = new GringottsEco();


    public Commands(Gringotts plugin) {
        this.plugin = plugin;
    }

    /**
     * Player commands.
     */
    public class Money implements CommandExecutor{
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
                balanceMessage(eco.account(player.getName()));
                return true;
            } 

            String command = "";
            if (args.length >= 1) {
                command = args[0];
            }

            double value = 0;
            if (args.length >= 2) {
                try { value = Double.parseDouble(args[1]); } 
                catch (NumberFormatException e) { return false; }

                if (command.equals("withdraw")) {
                    withdraw(player, value);
                    return true;
                } else if (command.equals("deposit")) {
                    deposit(player, value);
                    return true;
                }
            } 

            if(args.length == 3) {
                // /money pay <amount> <player>
                if (command.equals("pay"))
                    return pay(player, value, args);
            }

            return false;
        }
    }



    private boolean pay(Player player, double value, String[] args) {
        if (!Permissions.transfer.allowed(player)) {
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
            String succ_taxMessage = LANG.pay_success_tax.replace("%value", formattedTax);
            String succ_sentMessage = LANG.pay_success_sender.replace("%value", formattedValue).replace("%player", recipientName);
            from.message(succ_sentMessage + (tax>0? succ_taxMessage : ""));
            String succ_receivedMessage = LANG.pay_success_target.replace("%value", formattedValue).replace("%player", player.getName());
            to.message(succ_receivedMessage);
            return true;
        case INSUFFICIENT_FUNDS:
            String insF_Message = LANG.pay_insufficientFunds.replace("%balance", formattedBalance).replace("%value", formattedValuePlusTax);
            from.message(insF_Message);
            return true;
        case INSUFFICIENT_SPACE:
            String insS_sentMessage = LANG.pay_insS_sender.replace("%player", recipientName).replace("%value", formattedValue);
            from.message(insS_sentMessage);
            String insS_receiveMessage = LANG.pay_insS_target.replace("%player", from.id()).replace("%value", formattedValue);
            to.message(insS_receiveMessage);
            return true;
        default:
            String error = LANG.pay_error.replace("%value", formattedValue).replace("%player", recipientName);
            from.message(error);
            return true;
        }
    }

    private void deposit(Player player, double value) {

        if (command_deposit.allowed(player)) {
            TransactionResult result = eco.player(player.getUniqueId()).deposit(value);
            String formattedValue = eco.currency().format(value);
            if (result == SUCCESS) {
                String success = LANG.deposit_success.replace("%value", formattedValue);
                player.sendMessage(success);
            } else {
                String error = LANG.deposit_error.replace("%value", formattedValue);
                player.sendMessage(error);
            }
        }
    }

    private void withdraw(Player player, double value) {
        if (command_withdraw.allowed(player)) {
            TransactionResult result = eco.player(player.getUniqueId()).withdraw(value);
            String formattedValue = eco.currency().format(value);
            if (result == SUCCESS){
                String success = LANG.withdraw_success.replace("%value", formattedValue);
                player.sendMessage(success);
            }
            else{
                String error = LANG.withdraw_error.replace("%value", formattedValue);
                player.sendMessage(error);
            }
        }
    }

    /**
     * Admin commands for managing ingame aspects.
     */
    public class Moneyadmin implements CommandExecutor {

        public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

            String command;
            if (args.length >= 2) {
                command = args[0];
            } else return false;

            // admin command: x of player / faction
            if (args.length >= 2 && command.equalsIgnoreCase("b"))  {

                String targetAccountHolderStr = args[1];

                // explicit or automatic account type
                Account target = args.length==3? eco.custom(args[2], targetAccountHolderStr) : eco.account(targetAccountHolderStr);

                if (! target.exists()) {
                    invalidAccount(sender, targetAccountHolderStr);
                    return false;
                }

                String formattedBalance = eco.currency().format(target.balance());
                String senderMessage = LANG.moneyadmin_b.replace("%balance", formattedBalance).replace("%player", targetAccountHolderStr);
                sender.sendMessage(senderMessage);
                return true;

            }

            // moneyadmin add/remove
            if (args.length >= 3) {
                String amountStr = args[1];
                double value;
                try { value = Double.parseDouble(amountStr); } 
                catch(NumberFormatException x) { return false; }

                String targetAccountHolderStr = args[2];
                Account target = args.length==4? eco.custom(args[3], targetAccountHolderStr) : eco.account(targetAccountHolderStr);
                if (! target.exists()) {
                    invalidAccount(sender, targetAccountHolderStr);
                    return false;
                }

                String formatValue = eco.currency().format(value);

                if (command.equalsIgnoreCase("add")) {
                    TransactionResult added = target.add(value);
                    if (added == SUCCESS) {
                        String senderMessage = LANG.moneyadmin_add_sender.replace("%value", formatValue).replace("%player", target.id());
                        sender.sendMessage(senderMessage);
                        String targetMessage = LANG.moneyadmin_add_target.replace("%value", formatValue);
                        target.message(targetMessage);
                    } else {
                        String errorMessage = LANG.moneyadmin_add_error.replace("%value", formatValue).replace("%player", target.id());
                        sender.sendMessage(errorMessage);
                    }

                    return true;

                } else if (command.equalsIgnoreCase("rm")) {
                    TransactionResult removed = target.remove(value); 
                    if (removed == SUCCESS) {
                        String senderMessage = LANG.moneyadmin_rm_sender.replace("%value", formatValue).replace("%player", target.id());
                        sender.sendMessage(senderMessage);
                        String targetMessage = LANG.moneyadmin_rm_target.replace("%value", formatValue);
                        target.message(targetMessage);
                    } else {
                        String errorMessage = LANG.moneyadmin_rm_error.replace("%value", formatValue).replace("%player", target.id());
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
        account.message(LANG.balance.replace("%balance", eco.currency().format(account.balance()) ));
    }

    private static void invalidAccount(CommandSender sender, String accountName) {
        sender.sendMessage(LANG.invalid_account.replace("%player", accountName));
    }

}
