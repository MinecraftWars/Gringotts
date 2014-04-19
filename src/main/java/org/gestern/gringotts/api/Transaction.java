package org.gestern.gringotts.api;

public interface Transaction {


    /**
     * Complete the transaction by sending the transaction amount to a given account.
     * @param to Account to which receives the value of this transaction.
     * @return result of the transaction.
     */
    TransactionResult to(Account to);

    /**
     * Apply taxes to this transaction, as configured by the economy plugin. 
     * Completing the transaction will fail if the taxes cannot be collected.
     * @return transaction with applied taxes
     */
    TaxedTransaction withTaxes();
}
