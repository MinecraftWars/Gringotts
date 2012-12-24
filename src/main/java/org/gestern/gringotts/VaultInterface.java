package org.gestern.gringotts;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.gestern.gringotts.accountholder.AccountHolder;

/** 
 * Provides the vault interface, so that the economy adapter in vault does not need to be changed. 
 * 
 * @author jast
 *
 */
public class VaultInterface implements Economy {

    private static final Logger log = Gringotts.gringotts.getLogger();

    private final String name = "Gringotts";
    private Plugin plugin = null;
    private Gringotts gringotts = null;

    public VaultInterface(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
        // Load Plugin in case it was loaded before
        if (gringotts == null) {
            Plugin grngts = plugin.getServer().getPluginManager().getPlugin("Gringotts");
            if (grngts != null && grngts.isEnabled()) {
                gringotts = (Gringotts) grngts;
                log.info(String.format("[Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    public class EconomyServerListener implements Listener {
        VaultInterface economy = null;

        public EconomyServerListener(VaultInterface economy_Gringotts) {
            this.economy = economy_Gringotts;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.gringotts == null) {
                Plugin grngts = plugin.getServer().getPluginManager().getPlugin("Gringotts");

                if (grngts != null && grngts.isEnabled()) {
                    economy.gringotts = (Gringotts) grngts;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.gringotts != null) {
                if (event.getPlugin().getDescription().getName().equals("Gringotts")) {
                    economy.gringotts = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }
    }

    @Override
    public boolean isEnabled(){
        return gringotts != null && gringotts.isEnabled();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasBankSupport(){
        return false;
    }

    @Override
    public int fractionalDigits(){
        return 2;
    }

    @Override
    public String format(double amount) {
        return Util.format(amount);
    }

    @Override
    public String currencyNamePlural(){
        return org.gestern.gringotts.Configuration.config.currency.namePlural;
    }

    @Override
    public String currencyNameSingular(){
        return org.gestern.gringotts.Configuration.config.currency.name;
    }

    @Override
    public boolean hasAccount(String playerName) {
        AccountHolder owner = gringotts.accountHolderFactory.get(playerName);
        if (owner == null) return false;

        return gringotts.accounting.getAccount(owner) != null;
    }

    @Override
    public double getBalance(String playerName){
        AccountHolder owner = gringotts.accountHolderFactory.get(playerName);
        if (owner == null) return 0;
        Account account = gringotts.accounting.getAccount(owner);
        return account.balance();
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {

        if( amount < 0 ) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw a negative amount.");
        }

        AccountHolder accountHolder = gringotts.accountHolderFactory.get(playerName);
        if (accountHolder == null) 
            return new EconomyResponse(0, 0, ResponseType.FAILURE, playerName + " is not a valid account holder.");

        Account account = gringotts.accounting.getAccount( accountHolder );

        if(account.balance() >= amount && account.remove(amount)) {
            //We has mulah!
            return new EconomyResponse(amount, account.balance(), ResponseType.SUCCESS, null);
        } else {
            //Not enough money to withdraw this much.
            return new EconomyResponse(0, account.balance(), ResponseType.FAILURE, "Insufficient funds");
        }

    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount){
        if (amount < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot desposit negative funds");
        }

        AccountHolder accountHolder = gringotts.accountHolderFactory.get(playerName);
        if (accountHolder == null) 
            return new EconomyResponse(0, 0, ResponseType.FAILURE, playerName + " is not a valid account holder.");

        Account account = gringotts.accounting.getAccount( accountHolder );

        if (account.add(amount))        
            return new EconomyResponse( amount, account.balance(), ResponseType.SUCCESS, null);
        else
            return new EconomyResponse( 0, account.balance(), ResponseType.FAILURE, "Not enough capacity to store that amount!");

    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<String>();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return hasAccount(playerName);
    }

}
