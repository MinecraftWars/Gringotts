package org.gestern.gringotts.api.impl;

import static org.gestern.gringotts.Configuration.CONF;
import static org.gestern.gringotts.api.TransactionResult.ERROR;
import static org.gestern.gringotts.api.TransactionResult.SUCCESS;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.gestern.gringotts.AccountInventory;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.GringottsAccount;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.accountholder.AccountHolderFactory;
import org.gestern.gringotts.accountholder.PlayerAccountHolder;
import org.gestern.gringotts.api.Account;
import org.gestern.gringotts.api.BankAccount;
import org.gestern.gringotts.api.Currency;
import org.gestern.gringotts.api.Eco;
import org.gestern.gringotts.api.PlayerAccount;
import org.gestern.gringotts.api.Transaction;
import org.gestern.gringotts.api.TransactionResult;
import org.gestern.gringotts.currency.GringottsCurrency;
import org.gestern.gringotts.data.DAO;

public class GringottsEco implements Eco {

    private final AccountHolderFactory accountOwners = Gringotts.G.accountHolderFactory;
    private final DAO dao = Gringotts.G.dao;

    @Override
    public Account account(String id) {
        AccountHolder owner = accountOwners.get(id);
        if (owner == null) return new InvalidAccount("virtual", id);

        GringottsAccount gAccount = Gringotts.G.accounting.getAccount(owner);
        return new ValidAccount(gAccount);

    }

    @Override
    public PlayerAccount player(String name) {
        AccountHolder owner = accountOwners.get("player", name);
        if (owner instanceof PlayerAccountHolder)
            return new ValidPlayerAccount(Gringotts.G.accounting.getAccount(owner));

        return new InvalidAccount("player", name);
    }

    @Override
    public Account bank(String id)
    {
        AccountHolder holder = this.accountOwners.get("bank", id);
        if (holder == null) {
            return new InvalidAccount("bank", id);
        }
        return new ValidAccount(Gringotts.G.accounting.getAccount(holder));
    }

    @Override
    public Account custom(String type, String id) {
        AccountHolder owner = accountOwners.get(type, id);
        if (owner == null) return new InvalidAccount(type, id);
        GringottsAccount acc = new GringottsAccount(owner);
        return new ValidAccount(acc);
    }

    @Override
    public Account faction(String id) {
        return custom ("faction", id);
    }

    @Override
    public Account town(String id) {
        return custom ("town", id);

    }

    @Override
    public Account nation(String id) {
        return custom ("nation", id);
    }

    @Override
    public Currency currency() {
        return new Curr(CONF.currency);
    }

    @Override
    public boolean supportsBanks() {
        return true;
    }

    private class InvalidAccount implements Account, BankAccount, PlayerAccount {

        private final String type;
        private final String id;

        InvalidAccount(String type, String id) {
            this.type = type;
            this.id = id;
        }

        @Override
        public boolean exists() {
            return false;
        }

        @Override
        public Account create() {
            // TODO if account type allows virtual accounts, create it
            return this;
        }

        @Override
        public Account delete() {
            return this; // delete invalid account is still invalid
        }

        @Override
        public double balance() {
            return 0; // invalid account has 0 balance
        }

        @Override
        public boolean has(double value) {
            return false; // invalid account has nothing
        }

        @Override
        public TransactionResult setBalance(double newBalance) {
            return ERROR;
        }

        @Override
        public TransactionResult add(double value) {
            return ERROR;
        }

        @Override
        public TransactionResult remove(double value) {
            return ERROR;
        }

        @Override
        public Transaction send(double value) {
            return new GringottsTransaction(this, value);
        }

        @Override
        public String type() {
            return type;
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public void message(String message) {
            // do nothing - no owner on this
        }

        @Override
        public BankAccount addOwner(String player) {
            return this;
        }

        @Override
        public BankAccount addMember(String player) {
            return this;
        }

        @Override
        public boolean isOwner(String player) {
            return false;
        }

        @Override
        public boolean isMember(String player) {
            return false;
        }

        @Override
        public TransactionResult deposit(double value) {
            return ERROR;
        }

        @Override
        public TransactionResult withdraw(double value) {
            return ERROR;
        }

    }

    private class ValidAccount implements Account {

        protected GringottsAccount acc;

        public ValidAccount(GringottsAccount acc) {
            this.acc = acc;
        }

        @Override
        public boolean exists() {
            // since this is a valid account, returns true
            return true;
        }

        @Override
        public Account create() {
            return this;
        }

        @Override
        public Account delete() {
            dao.deleteAccount(acc);
            throw new RuntimeException("deleting accounts not supported by Gringotts");
        }

        @Override
        public double balance() {
            return CONF.currency.displayValue(acc.balance());
        }

        @Override
        public boolean has(double value) {
            return acc.balance() >= CONF.currency.centValue(value);
        }

        @Override
        public TransactionResult setBalance(double newBalance) {
            return add(balance() - newBalance);
        }

        @Override
        public TransactionResult add(double value) {
            if (value < 0) return remove(-value);
            return acc.add(CONF.currency.centValue(value));
        }

        @Override
        public TransactionResult remove(double value) {
            if (value < 0) return add(-value);
            return acc.remove(CONF.currency.centValue(value));
        }

        @Override
        public Transaction send(double value) {
            return new GringottsTransaction(this, value);
        }

        @Override
        public String type() {
            return acc.owner.getType();
        }

        @Override
        public String id() {
            return acc.owner.getId();
        }

        @Override
        public void message(String message) {
            acc.owner.sendMessage(message);
        }

    }

    private class ValidPlayerAccount extends ValidAccount implements PlayerAccount {

        public ValidPlayerAccount(GringottsAccount acc) {
            super(acc);
        }

        @Override
        public TransactionResult deposit(double value) {
            PlayerAccountHolder owner = (PlayerAccountHolder) acc.owner;
            Player player = Bukkit.getPlayer(owner.getId());
            AccountInventory playerInventory = new AccountInventory(player.getInventory());
            long centValue = CONF.currency.centValue(value);

            long toDeposit = playerInventory.remove(centValue);
            if (toDeposit > centValue) 
                toDeposit -= playerInventory.add(toDeposit - centValue);

            TransactionResult result = player(player.getName()).add(CONF.currency.displayValue(toDeposit));
            if (result != SUCCESS)
                playerInventory.add(toDeposit);

            return result;
        }

        @Override
        public TransactionResult withdraw(double value) {
            PlayerAccountHolder owner = (PlayerAccountHolder) acc.owner;
            Player player = Bukkit.getPlayer(owner.getId());
            AccountInventory playerInventory = new AccountInventory(player.getInventory());
            long centValue = CONF.currency.centValue(value);

            TransactionResult remove = acc.remove(centValue);
            if (remove == SUCCESS) {
                long withdrawn = playerInventory.add(centValue);
                return acc.add(centValue-withdrawn); // add possible leftovers back to account
            }
            return remove;
        }

    }

    private class Curr implements Currency {

        final GringottsCurrency gcurr;
        final String formatString; 

        Curr(GringottsCurrency curr) {
            this.gcurr = curr;
            formatString = "%."+curr.digits+"f %s";
        }

        @Override
        public String name() {
            return gcurr.name;
        }

        @Override
        public String namePlural() {
            return gcurr.namePlural;
        }

        @Override
        public String format(double value) {
            return String.format(formatString, value, value==1.0? gcurr.name : gcurr.namePlural);
        }

        @Override
        public int fractionalDigits() {
            return gcurr.digits;
        }

    }

    @Override
    public Set<String> getBanks() {
        // TODO implement banks
        return new HashSet<String>();
    }

}
