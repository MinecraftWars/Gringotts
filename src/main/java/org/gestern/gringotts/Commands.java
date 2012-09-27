package org.gestern.gringotts;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Central handler for player and console commands.
 * 
 * @author jast
 *
 */
public class Commands {
    Logger log = Bukkit.getServer().getLogger();

    private Gringotts plugin;
    private Configuration conf = Configuration.config;
    
    public Commands(Gringotts plugin) {
        this.plugin = plugin;
    }
    
    public class Money implements CommandExecutor{
    	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    		
    		Accounting accounting = plugin.accounting;
    		
    		Player player;
	        if (sender instanceof Player) {
	            player = (Player)sender;
	        } else {
	            sender.sendMessage("This command can only be run by a player.");
	            return false;
	            // TODO actually, refactor the whole thing already!
	        }
	        
            AccountHolder accountOwner = new PlayerAccountHolder(player);
            Account account = accounting.getAccount(accountOwner);
            
            if (args.length == 0) {
                // same as balance
                balanceMessage(account, accountOwner);
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
            } 

            if(args.length == 3) {
                // /money pay <amount> <player>
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
            
            return false;
    	}
    }
    
    public class Moneyadmin implements CommandExecutor {
    	
    	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        	
            Accounting accounting = plugin.accounting;

            if (cmd.getName().equalsIgnoreCase("money") || cmd.getName().equalsIgnoreCase("balance")) {
            	
            } else if (cmd.getName().equalsIgnoreCase("moneyadmin")) {
            	
            	AccountHolderFactory ahf = new AccountHolderFactory();
            	
            	String command;
                if (args.length >= 2) {
                    command = args[0];
                } else return false;
                
                // admin command: balance of player / faction
                if (args.length == 2 && command.equalsIgnoreCase("b")) {
                	String targetAccountHolderStr = args[1];
                	AccountHolder targetAccountHolder = ahf.get(targetAccountHolderStr);
                	Account targetAccount = accounting.getAccount(targetAccountHolder);
                	sender.sendMessage("Balance of account " + targetAccountHolder.getName() + ": " + targetAccount.balance());
                	return true;
                }
                
                // moneyadmin add/remove
                if (args.length == 3) {
                	String amountStr = args[1];
                	double value;
                	try { value = Double.parseDouble(amountStr);} 
                	catch(NumberFormatException x) { return false; }
                	
                	String targetAccountHolderStr = args[2];
                	AccountHolder targetAccountHolder = ahf.get(targetAccountHolderStr);
                	Account targetAccount = accounting.getAccount(targetAccountHolder);
                	if (command.equalsIgnoreCase("add")) {
                        if (targetAccount.add(value)) {
                        	sender.sendMessage("Added " + value + " to account " + targetAccountHolder.getName());
                        	targetAccountHolder.sendMessage("Added to your account: " + value);
                        } else {
                        	sender.sendMessage("Could not add " + value + " to account " + targetAccountHolder.getName());
                        }
                        
                        return true;
                        
                	} else if (command.equalsIgnoreCase("rm")) {
                        if (targetAccount.remove(value)) {
                        	sender.sendMessage("Removed " + value + " from account " + targetAccountHolder.getName());
                        	targetAccountHolder.sendMessage("Removed from your account: " + value);
                        } else {
                        	sender.sendMessage("Could not remove " + value + " from account " + targetAccountHolder.getName());
                        }
                        
                        return true;
                	}
                }
            }

            return false; 
        }
    }

    
    private static void balanceMessage(Account account, AccountHolder owner) {
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
