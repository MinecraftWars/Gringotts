package org.gestern.gringotts.api;

/**
 * The result of a transaction.
 * @author jast
 *
 */
public enum TransactionResult {
	SUCCESS, INSUFFICIENT_FUNDS, INSUFFICIENT_SPACE, ERROR;
}