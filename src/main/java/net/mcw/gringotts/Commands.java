package net.mcw.gringotts;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.mcw.gringotts.Gringotts;

public class Commands implements CommandExecutor  {
	Logger log = Bukkit.getServer().getLogger();
	 
	private Gringotts plugin;
	 
	public Commands(Gringotts plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		log.info("Command Received: " + cmd);

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
			} else if (args.length == 2) {
				double value;
				try {
					value = Double.parseDouble(args[1]);
				} catch (NumberFormatException e) {
					return false;
				}
				
				if (args[0].equals("add")) {
					if (account.add(value))
						accountOwner.sendMessage("added to your account: " + value);
					else
						accountOwner.sendMessage("could not add " + value + " to your account.");
					
				} else if (args[0].equals("remove")) {
					if (account.remove(value))
						accountOwner.sendMessage("removed from your account: " + value);
					else
						accountOwner.sendMessage("could not remove " + value + " from your account.");
				} else return false;
			} else return false;
			
			return true;
		}
		
		return false; 
	}
	
	private void balance(Account account, AccountHolder owner) {
		owner.sendMessage("Your current balance: " + account.balance());
	}

}
