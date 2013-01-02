package org.gestern.gringotts.api.impl;

import static org.gestern.gringotts.api.TransactionResult.*;

import org.gestern.gringotts.Configuration;
import org.gestern.gringotts.DAO;
import org.gestern.gringotts.GringottsAccount;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.accountholder.AccountHolderFactory;
import org.gestern.gringotts.api.Account;
import org.gestern.gringotts.api.Currency;
import org.gestern.gringotts.api.Eco;
import org.gestern.gringotts.api.TransactionResult;
import org.gestern.gringotts.currency.GringottsCurrency;

public class GringottsEco implements Eco {
	
	private final AccountHolderFactory accountOwners = new AccountHolderFactory();
	private final Curr curr = new Curr(Configuration.config.currency);
	private final DAO dao = DAO.getDao();

	@Override
    public Account player(String name) {
		return custom ("player", name);
    }

	@Override
    public Account bank(String name) {
		throw new RuntimeException("Banks not implemented yet in Gringotts");
    }

	@Override
    public Account custom(String type, String id) {
	    AccountHolder owner = accountOwners.get(type, id);
	    GringottsAccount acc = new GringottsAccount(owner);
	    return new Acc(acc);
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
	    return curr;
    }

	@Override
    public boolean supportsBanks() {
	    // TODO Auto-generated method stub
	    return false;
    }
	
	private class Acc implements Account {
		
		GringottsAccount acc;
		
		public Acc(GringottsAccount acc) {
			this.acc = acc;
		}

		@Override
        public boolean exists() {
	        // Gringotts accounts implicitly exist 
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
	        return curr.gcurr.displayValue(acc.balance());
        }

		@Override
        public boolean has(double value) {
			return acc.balance() >= curr.gcurr.centValue(value);
        }

		@Override
        public TransactionResult setBalance(double newBalance) {
	        return add(balance() - newBalance);
        }

		@Override
        public TransactionResult add(double value) {
			if (value < 0) return remove(-value);
			return acc.add(curr.gcurr.centValue(value));
        }

		@Override
        public TransactionResult remove(double value) {
			if (value < 0) return add(-value);
			return acc.remove(curr.gcurr.centValue(value));
        }

		@Override
        public TransactionResult sendTo(double value, Account recipient) {
			if (value < 0) return ERROR;
			TransactionResult removed = remove(value);
			if (removed == SUCCESS) {
				TransactionResult added = recipient.add(value);
				
				if (added != SUCCESS)
					// adding failed, refund this account
					this.add(value);
				
				// returns success or reason add failed
				return added;
			}
			// return reason remove failed
	        return removed;
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

}
