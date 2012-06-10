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
		log.info("Command Received");

		Player player;
		if (sender instanceof Player) {
			player = (Player)sender;
		} else {
			sender.sendMessage("This command can only be run by a player.");
			return false; // for now, no console commands
		}

		if(cmd.getName().equalsIgnoreCase("balance")){
			AccountHolder accountOwner = new PlayerAccountHolder(player);
			Accounting accounting = plugin.accounting;
			Account account = accounting.getAccount(accountOwner);
			accountOwner.sendMessage("Your current balance: " + account.balance());
			return true;
		}
		
		return false; 
	}

}
