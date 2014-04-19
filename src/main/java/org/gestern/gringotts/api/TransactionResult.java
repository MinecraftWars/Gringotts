package org.gestern.gringotts.api;

/**
 * The result of a transaction.
 * @author jast
 *
 */
public enum TransactionResult {
    /**
     * Transaction was successful.
     */
    SUCCESS, 
    /**
     * Transaction failed due to insufficient funds of an account that money was to be taken from.
     */
    INSUFFICIENT_FUNDS, 
    /**
     * Transaction failed due to insufficient space in an account that money was to be deposited to.
     */
    INSUFFICIENT_SPACE, 
    /**
     * Transaction operation not supported.
     */
    UNSUPPORTED,
    /**
     * An error occurred while trying to process the transaction.
     */
    ERROR
}