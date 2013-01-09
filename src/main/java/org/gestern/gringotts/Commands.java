package org.gestern.gringotts;

import static org.gestern.gringotts.api.TransactionResult.SUCCESS;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gestern.gringotts.api.Account;
import org.gestern.gringotts.api.Eco;
import org.gestern.gringotts.api.TaxedTransaction;
import org.gestern.gringotts.api.TransactionResult;
import org.gestern.gringotts.api.impl.GringottsEco;


/**
 * Handlers for player and console commands.
 * 
 * @author jast
 *
 */
public class Commands {
    Logger log = Gringotts.G.getLogger();

    private Gringotts plugin;
    private Configuration conf = Configuration.config;
	
	Eco eco = new GringottsEco();

    
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
	            sender.sendMessage("This command can only be run by a player.");
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
            } 

            if(args.length == 3) {
                // /money pay <amount> <player>
                if (command.equals("pay")) {
                	if (!Permissions.transfer.allowed(player)) {
                		player.sendMessage("You do not have permission to transfer money.");
                		return true;
                	}
                	
                    String recipientName = args[2];
                    
                    Account from = eco.player(player.getName());
                    Account to = eco.player(recipientName);
                    
                    TaxedTransaction transaction = eco.player(player.getName()).send(value).withTaxes();
                    TransactionResult result = eco.player(player.getName()).send(value).withTaxes().to(eco.player(recipientName));
                    
                    double tax = transaction.tax();
                    double valueAdded = value + tax;
                    
                    String formattedBalance = eco.currency().format(from.balance());
                    String formattedValue = eco.currency().format(valueAdded);
                    
                    switch (result) {
	                    case SUCCESS:
	                    	String taxMessage = "Transaction tax deducted from your account: " + formattedValue;
	                        from.message("Sent " + formattedValue + " to " + recipientName +". " + (tax>0? taxMessage : ""));
	                        to.message("Received " + formattedValue + " from " + player.getName() +".");
	                    	return true;
	                    case INSUFFICIENT_FUNDS:
	                    	from.message(
	                                "Your account has insufficient balance. Current balance: " + formattedBalance 
	                                + ". Required: " + formattedValue);
	                    	return true;
	                    case INSUFFICIENT_SPACE:
	                    	from.message(recipientName + " has insufficient storage space for "+formattedValue);
	                    	to.message(from.id() + " tried to send "+formattedValue+", but you don't have enough space for that amount.");
	                    	return true;
	                    	
	                    default:
	                    	from.message("Your attempt to send "+formattedValue+" to "+recipientName+" failed for unknown reasons.");
	                    	return true;
	                    }
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
        	
            if (cmd.getName().equalsIgnoreCase("money") || cmd.getName().equalsIgnoreCase("balance")) {
            	
            } else if (cmd.getName().equalsIgnoreCase("moneyadmin")) {
            	            	
            	String command;
                if (args.length >= 2) {
                    command = args[0];
                } else return false;
                
                // admin command: balance of player / faction
                if (args.length == 2)  {
                	
                	String targetAccountHolderStr = args[1];
                	Account target = eco.player(targetAccountHolderStr);
                
                	if (! target.exists()) {
                		invalidAccount(sender, targetAccountHolderStr);
                		return false;
                	}
                	
                	if (command.equalsIgnoreCase("b")) {
                    	String formattedBalance = eco.currency().format(target.balance());
	                	sender.sendMessage("Balance of account " + targetAccountHolderStr + ": " + formattedBalance);
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
                	Account target = eco.account(targetAccountHolderStr);
                	if (! target.exists()) {
                		invalidAccount(sender, targetAccountHolderStr);
                		return false;
                	}
                	
                	String formatValue = eco.currency().format(value);
                	
                	if (command.equalsIgnoreCase("add")) {
                		TransactionResult added = target.add(value);
                        if (added == SUCCESS) {
                        	sender.sendMessage("Added " + formatValue + " to account " + target.id());
                        	target.message("Added to your account: " + formatValue);
                        } else {
                        	sender.sendMessage("Could not add " + formatValue + " to account " + target.id());
                        }
                        
                        return true;
                        
                	} else if (command.equalsIgnoreCase("rm")) {
                		TransactionResult removed = target.remove(value); 
                        if (removed == SUCCESS) {
                        	sender.sendMessage("Removed " + formatValue + " from account " + target.id());
                        	target.message("Removed from your account: " + formatValue);
                        } else {
                        	sender.sendMessage("Could not remove " + formatValue + " from account " + target.id());
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

    
    private void balanceMessage(Account account) {
        account.message("Your current balance: " + eco.currency().format(account.balance()) );
    }
    
    private static void invalidAccount(CommandSender sender, String accountName) {
    	sender.sendMessage("Invalid account: " + accountName);
    }

}
