package org.gestern.gringotts;

import static org.gestern.gringotts.Util.format;
import static org.gestern.gringotts.api.TransactionResult.*;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.accountholder.AccountHolderFactory;
import org.gestern.gringotts.accountholder.PlayerAccountHolder;
import org.gestern.gringotts.api.TransactionResult;
import org.gestern.gringotts.currency.GringottsCurrency;


/**
 * Handlers for player and console commands.
 * 
 * @author jast
 *
 */
public class Commands {
    Logger log = Gringotts.gringotts.getLogger();

    private Gringotts plugin;
    private Configuration conf = Configuration.config;
    
	private final AccountHolderFactory ahf = new AccountHolderFactory();

    
    public Commands(Gringotts plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Player commands.
     */
    public class Money implements CommandExecutor{
    	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    		
    		Accounting accounting = plugin.accounting;
    		
    		Player player;
	        if (sender instanceof Player) {
	            player = (Player)sender;
	        } else {
	            sender.sendMessage("This command can only be run by a player.");
	            return false;
	            // TODO actually, refactor the whole thing already! take the business logic out of here, oh gosh!
	            // how many more times must I slap myself to do this!!!11
	        }
	        
            AccountHolder accountOwner = new PlayerAccountHolder(player);
            GringottsAccount account = accounting.getAccount(accountOwner);
            
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
                	if (!Permissions.transfer.allowed(player)) {
                		player.sendMessage("You do not have permission to transfer money.");
                		return true;
                	}
                	
                    String recipientName = args[2];
                    
                    AccountHolder recipient = ahf.get(recipientName);
                    if (recipient == null) {
                		invalidAccount(sender, recipientName);
                		return true;
                	}
                    
                    GringottsAccount recipientAccount = accounting.getAccount(recipient);
                    
                    GringottsCurrency cur = Configuration.config.currency;
                    
                    // FIXME take the damn business logic out of the command handler
                    double tax = conf.transactionTaxFlat + value * conf.transactionTaxRate;
                    // round tax value when fractions are disabled
                    
                    long valueCents = cur.centValue(value);
                    long taxCent = cur.centValue(tax);

                    long balance = account.balance();
                    long valueAdded = valueCents + taxCent;
                    
                    String formattedBalance = format(cur.displayValue(balance));
                    String formattedValue = format(cur.displayValue(valueAdded));
                    
                    // TODO move the business logic to API
                    TransactionResult taxed = account.remove(taxCent);
                    TransactionResult transfer = account.transfer(valueCents, recipientAccount);
                    if (taxed == SUCCESS) {
                    	if (transfer == SUCCESS) {
                    		account.remove(taxCent);
                            
                            String taxMessage = "Transaction tax deducted from your account: " + formattedValue;
                            accountOwner.sendMessage("Sent " + formattedValue + " to " + recipientName +". " + (tax>0? taxMessage : ""));
                            recipient.sendMessage("Received " + formattedValue + " from " + accountOwner.getName() +".");
                    	} else {
                    		// transfer failed, refund tax
                    		account.add(taxCent); // this better not fail!
                    		if (transfer == INSUFFICIENT_FUNDS) {
                    			accountOwner.sendMessage(
                                        "Your account has insufficient balance. Current balance: " + formattedBalance 
                                        + ". Required: " + formattedValue);
                    		} else if (transfer == INSUFFICIENT_SPACE) {
                    			accountOwner.sendMessage(recipientName + " has insufficient storage space for this amount.");
                    		}
                    	}
                    } else {
                    	accountOwner.sendMessage("Transfer failed. You couldn't even pay the taxes!");
                    }
                    return true;
                }
            }
            
            return false;
    	}
    }
    
    /**
     * Admin commands for managing ingame aspects.
     */
    public class Moneyadmin implements CommandExecutor {
    	
    	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        	
            Accounting accounting = plugin.accounting;

            if (cmd.getName().equalsIgnoreCase("money") || cmd.getName().equalsIgnoreCase("balance")) {
            	
            } else if (cmd.getName().equalsIgnoreCase("moneyadmin")) {
            	            	
            	String command;
                if (args.length >= 2) {
                    command = args[0];
                } else return false;
                
                // admin command: balance of player / faction
                if (args.length == 2)  {
                	
                	String targetAccountHolderStr = args[1];
                	AccountHolder targetAccountHolder = ahf.get(targetAccountHolderStr);
                	if (targetAccountHolder == null) {
                		invalidAccount(sender, targetAccountHolderStr);
                		return true;
                	}
                	GringottsAccount targetAccount = accounting.getAccount(targetAccountHolder);
                	String formattedBalance = format(conf.currency.displayValue(targetAccount.balance()));
                	if (command.equalsIgnoreCase("b")) {
	                	sender.sendMessage("Balance of account " + targetAccountHolder.getName() + ": " + formattedBalance);
	                	return true;
                	} else
                		return false;
                }
                
                // moneyadmin add/remove
                if (args.length == 3) {
                	String amountStr = args[1];
                	double value;
                	try { value = Double.parseDouble(amountStr); } 
                	catch(NumberFormatException x) { return false; }
                	
                	String targetAccountHolderStr = args[2];
                	AccountHolder targetAccountHolder = ahf.get(targetAccountHolderStr);
                	if (targetAccountHolder == null) {
                		invalidAccount(sender, targetAccountHolderStr);
                		return true;
                	}
                	
                	GringottsAccount targetAccount = accounting.getAccount(targetAccountHolder);
                	
                	String formatValue = format(value);
                	long valueCents = conf.currency.centValue(value);
                	if (command.equalsIgnoreCase("add")) {
                		TransactionResult added = targetAccount.add(valueCents);
                        if (added == SUCCESS) {
                        	sender.sendMessage("Added " + formatValue + " to account " + targetAccountHolder.getName());
                        	targetAccountHolder.sendMessage("Added to your account: " + formatValue);
                        } else {
                        	sender.sendMessage("Could not add " + formatValue + " to account " + targetAccountHolder.getName());
                        }
                        
                        return true;
                        
                	} else if (command.equalsIgnoreCase("rm")) {
                		TransactionResult removed = targetAccount.remove(valueCents); 
                        if (removed == SUCCESS) {
                        	sender.sendMessage("Removed " + formatValue + " from account " + targetAccountHolder.getName());
                        	targetAccountHolder.sendMessage("Removed from your account: " + formatValue);
                        } else {
                        	sender.sendMessage("Could not remove " + formatValue + " from account " + targetAccountHolder.getName());
                        }
                        
                        return true;
                	}
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
				conf.readConfig(plugin.getConfig());
				sender.sendMessage("Gringotts: Reloaded configuration!");
				return true;
			}
			
			return false;
		}
    	
    }

    
    private static void balanceMessage(GringottsAccount account, AccountHolder owner) {
    	long balance = account.balance();
        owner.sendMessage("Your current balance: " + format(Configuration.config.currency.displayValue(balance)) + ".");
    }
    
    private static void invalidAccount(CommandSender sender, String accountName) {
    	sender.sendMessage("Invalid account: " + accountName);
    }

}
