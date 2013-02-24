package org.gestern.gringotts.api.impl;

import static org.gestern.gringotts.api.TransactionResult.*;

import org.gestern.gringotts.api.Account;
import org.gestern.gringotts.api.TaxedTransaction;
import org.gestern.gringotts.api.TransactionResult;

public class GringottsTaxedTransaction extends GringottsTransaction implements TaxedTransaction {

    /**
     * Taxes to apply to transaction.
     */
    private final double taxes;

    @Override
    public TransactionResult to(Account recipient) {
        TransactionResult taxResult = from.remove(taxes);
        if (taxResult != SUCCESS) return taxResult;

        TransactionResult result = super.to(recipient);
        // undo taxing if transaction failed
        if (result != SUCCESS) from.add(taxes);
        return result; 
    }

    /**
     * Create taxed transaction, adding given amount of taxes to the given base transaction
     * @param base
     * @param taxes
     */
    protected GringottsTaxedTransaction(GringottsTransaction base, double taxes) {
        super(base);
        this.taxes = taxes;
    }

    @Override
    public TaxedTransaction collectedBy(Account taxCollector) {
        throw new RuntimeException("tax collector account not yet implemented");
    }

    @Override
    public double tax() {
        return taxes;
    }

}
