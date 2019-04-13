package org.gestern.gringotts.api.impl;

import org.gestern.gringotts.api.Account;
import org.gestern.gringotts.api.TaxedTransaction;
import org.gestern.gringotts.api.TransactionResult;

import static org.gestern.gringotts.api.TransactionResult.SUCCESS;

public class GringottsTaxedTransaction extends GringottsTransaction implements TaxedTransaction {

    /**
     * Taxes to apply to transaction.
     */
    private final double taxes;

    /**
     * Taxes will be added to this account vault if any
     */
    private Account collector;

    /**
     * Create taxed transaction, adding given amount of taxes to the given base transaction
     *
     * @param base  transaction on which the tax is based
     * @param taxes taxes to apply to transaction
     */
    protected GringottsTaxedTransaction(GringottsTransaction base, double taxes) {
        super(base);

        this.taxes = taxes;
    }

    @Override
    public TransactionResult to(Account recipient) {
        TransactionResult taxResult = from.remove(taxes);

        if (taxResult != SUCCESS) {
            return taxResult;
        }

        TransactionResult result = super.to(recipient);

        // undo taxing if transaction failed
        if (result != SUCCESS) {
            from.add(taxes);
        } else {
            if (collector != null)
                collector.add(taxes);
        }

        return result;
    }

    @Override
    public TaxedTransaction setCollectedBy(Account taxCollector) {
        if (this.collector != null) {
            throw new RuntimeException("Collector is already set");
        }
        this.collector = taxCollector;
        return this;
    }

    @Override
    public double getTax() {
        return taxes;
    }
}
