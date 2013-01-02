package org.gestern.gringotts.api.impl;

import static org.gestern.gringotts.api.TransactionResult.ERROR;
import static org.gestern.gringotts.api.TransactionResult.SUCCESS;

import org.gestern.gringotts.Configuration;
import org.gestern.gringotts.api.Account;
import org.gestern.gringotts.api.TaxedTransaction;
import org.gestern.gringotts.api.Transaction;
import org.gestern.gringotts.api.TransactionResult;

public class GringottsTransaction implements Transaction {

	/** Account from which this transaction will withdraw money. */
	protected final Account from;
	
	/** Base value of this transaction. */
	protected double value;
	
	/**
	 * Create Transaction based on another (copy ctor).
	 * @param base
	 */
	protected GringottsTransaction(GringottsTransaction base) {
		this.from = base.from;
		this.value = base.value;
	}
	
	/**
	 * Create transaction with given source account and value.
	 * @param from Account from which this transaction will withdraw money
	 * @param amount base amount of this transaction
	 */
	GringottsTransaction(Account from, double value) {
		this.from = from;
		this.value = value;
	}
	
	public TransactionResult to(Account to) {
		if (value < 0) return ERROR;
		
		TransactionResult removed = from.remove(value);
		if (removed == SUCCESS) {
			TransactionResult added = to.add(value);
			if (added != SUCCESS)
				// adding failed, refund source
				from.add(value);
			
			// returns success or reason add failed
			return added;
		}
		// return reason remove failed
        return removed;
	}
	
	public TaxedTransaction withTaxes() {
		
		Configuration conf = Configuration.config;
        double tax = conf.transactionTaxFlat + value * conf.transactionTaxRate;
        return new GringottsTaxedTransaction(this, tax);
	}
	
}
