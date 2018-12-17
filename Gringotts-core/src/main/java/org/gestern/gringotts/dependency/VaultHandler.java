package org.gestern.gringotts.dependency;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.gestern.gringotts.Configuration;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.GringottsAccount;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.api.TransactionResult;

import java.util.ArrayList;
import java.util.List;

public class VaultHandler implements DependencyHandler {

    private final Plugin plugin;

    public VaultHandler(Plugin plugin) {
        this.plugin = plugin;

        if (plugin != null) {
            Bukkit.getServicesManager().register(Economy.class, new VaultEconomy(), plugin, ServicePriority.Normal);
        }
    }

    @Override
    public boolean enabled() {
        return plugin != null && plugin.isEnabled();
    }

    @Override
    public boolean exists() {
        return plugin != null;
    }
}

class VaultEconomy extends AbstractEconomy {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "Gringotts";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double amount) {
        return Double.toString(amount);
    }

    @Override
    public String currencyNamePlural() {
        return Configuration.CONF.currency.namePlural;
    }

    @Override
    public String currencyNameSingular() {
        return Configuration.CONF.currency.name;
    }

    @Override
    public boolean hasAccount(String playerName) {
        AccountHolder owner = Gringotts.G.accountHolderFactory.get(playerName);
        if (owner == null) {
            return false;
        }
        return Gringotts.G.accounting.getAccount(owner) != null;
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public double getBalance(String playerName) {
        AccountHolder owner = Gringotts.G.accountHolderFactory.get(playerName);
        if (owner == null) {
            return 0;
        }
        GringottsAccount account = Gringotts.G.accounting.getAccount(owner);
        return account.balance();
    }

    @Override
    public double getBalance(String playerName, String worldName) {
        return getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        if( amount < 0 ) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw a negative amount.");
        }

        AccountHolder accountHolder = Gringotts.G.accountHolderFactory.get(playerName);
        if (accountHolder == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, playerName + " is not a valid account holder.");
        }

        GringottsAccount account = Gringotts.G.accounting.getAccount( accountHolder );

        TransactionResult result = account.remove(Math.round(amount*100));

        switch(result) {
            case SUCCESS:
                return new EconomyResponse(amount, account.balance(), EconomyResponse.ResponseType.SUCCESS, null);
            case INSUFFICIENT_FUNDS:
                return new EconomyResponse(0, account.balance(), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
            case INSUFFICIENT_SPACE:
                return new EconomyResponse(0, account.balance(), EconomyResponse.ResponseType.FAILURE, "Not enough capacity to store that amount!");
        }

        return new EconomyResponse(0, account.balance(), EconomyResponse.ResponseType.FAILURE, "Unknown Error");
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds");
        }

        AccountHolder accountHolder = Gringotts.G.accountHolderFactory.get(playerName);
        if (accountHolder == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, playerName + " is not a valid account holder.");
        }

        GringottsAccount account = Gringotts.G.accounting.getAccount( accountHolder );

        TransactionResult result = account.add(Math.round(amount*100));

        switch(result) {
            case SUCCESS:
                return new EconomyResponse( amount, account.balance(), EconomyResponse.ResponseType.SUCCESS, null);
            case INSUFFICIENT_FUNDS:
                return new EconomyResponse(0, account.balance(), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
            case INSUFFICIENT_SPACE:
                return new EconomyResponse(0, account.balance(), EconomyResponse.ResponseType.FAILURE, "Not enough capacity to store that amount!");
        }

        return new EconomyResponse(0, account.balance(), EconomyResponse.ResponseType.FAILURE, "Unknown Error");
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<String>();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }
}
