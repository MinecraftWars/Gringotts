package net.mcw.gringotts;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.mcw.gringotts.Gringotts;

public class gCommand implements CommandExecutor  {
	Logger log = Bukkit.getServer().getLogger();
	 
	private Gringotts plugin;
	 
	public gCommand(Gringotts plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		log.info("Command Received");

		Player player;
		if (sender instanceof Player) {
			player = (Player)sender;
		} else {
			sender.sendMessage("this command can only be run by a player");
			return false; // for now, no console commands
		}

		if(cmd.getName().equalsIgnoreCase("balance")){
			AccountHolder accountOwner = new PlayerAccountHolder(player);
			Accounting accounting = new Accounting();
			Account account = accounting.getAccount(accountOwner);
			accountOwner.sendMessage("Your current balance: " + account.balance());
			return true;
		} //If this has happened the function will break and return true. if this hasn't happened the a value of false will be returned.
		return false; 
	}

}
