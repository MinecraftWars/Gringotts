package org.gestern.gringotts.api;

public interface TaxedTransaction extends Transaction {

    /**
     * Add a tax collector to this taxed transaction. The tax collector account receives the taxes from this transaction.
     * @param taxCollector account to receive the taxes.
     * @return taxed transaction with tax collector
     */
    TaxedTransaction collectedBy(Account taxCollector);

    /**
     * Return the amount of taxes to be paid in this transaction.
     * @return the amount of taxes to be paid in this transaction.
     */
    double tax();
}
