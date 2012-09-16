package org.gestern.gringotts;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor  {
    Logger log = Bukkit.getServer().getLogger();

    private Gringotts plugin;
    private Configuration conf = Configuration.config;
    
    public Commands(Gringotts plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        Player player;
        if (sender instanceof Player) {
            player = (Player)sender;
        } else {
            sender.sendMessage("This command can only be run by a player.");
            return false; // for now, no console commands
        }

        AccountHolder accountOwner = new PlayerAccountHolder(player);
        
        Accounting accounting = plugin.accounting;
        Account account = accounting.getAccount(accountOwner);

        if(cmd.getName().equalsIgnoreCase("balance")){
            balance(account, accountOwner);
            return true;
        } else if(cmd.getName().equalsIgnoreCase("money")){
            if (args.length == 0) {
                // same as balance
                balance(account, accountOwner);
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

                if (command.equals("add")) {
                    
                    return true;

                    /*
                    if(player.hasPermission("gringotts.admin")) {
                        sender.sendMessage("You do not have permission to do that.");
                        return true;
                    }

                    if (account.add(value))
                        accountOwner.sendMessage("added to your account: " + value);
                    else
                        accountOwner.sendMessage("could not add " + value + " to your account.");

                    return true;
                    */

                } else if (command.equals("remove")) {

                    if(player.hasPermission("gringotts.admin")) {
                        sender.sendMessage("You do not have permission to do that.");
                        return true;
                    }
                    if (account.remove(value))
                        accountOwner.sendMessage("removed from your account: " + value);
                    else
                        accountOwner.sendMessage("could not remove " + value + " from your account.");

                    return true;
                }
            } 

            if(args.length == 3) {
                // /money pay <amount> <player>
                // TODO support faction payment
                if (command.equals("pay")) {
                    String recipientName = args[2];
                    AccountHolder recipient = new PlayerAccountHolder(recipientName);
                    Account recipientAccount = accounting.getAccount(recipient);

                    double tax = conf.transactionTaxFlat + value * conf.transactionTaxRate;

                    double balance = account.balance();
                    double valueAdded = value + tax;
                    if (balance < valueAdded) {
                        accountOwner.sendMessage(
                                "Your account has insufficient balance. Current balance: " + balance + " " + numName(balance) 
                                + ". Required: " + (valueAdded) + " " + numName(valueAdded));
                        return true;
                    }
                    if (recipientAccount.capacity() < value) {
                        accountOwner.sendMessage(recipientName + " has insufficient storage space for this amount");
                        return true;
                    } else if (account.remove(value)) {
                        if (recipientAccount.add(value)) {
                            account.remove(tax);
                            String currencyName = numName(balance);
                            String taxMessage = "Transaction tax deducted from your account: " + tax + " " + numName(tax);
                            accountOwner.sendMessage("Sent " + value + " " + currencyName + " to " + recipientName +". " + (tax>0? taxMessage : ""));
                            recipient.sendMessage("Received " + value + " " + currencyName + " from " + accountOwner.getName() +".");
                            return true;
                        }
                    }
                }
            }
        }

        return false; 
    }

    private void balance(Account account, AccountHolder owner) {
        owner.sendMessage("Your current balance: " + account.balance());
    }

    /**
     * Currency name for a given value (singular or plural).
     * @return currency name for a given value (singular or plural)
     */
    private String numName(double value) {
        return value==1.0? conf.currencyNameSingular : conf.currencyNamePlural;
    }

}
