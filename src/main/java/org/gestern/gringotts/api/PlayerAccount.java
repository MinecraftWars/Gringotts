package org.gestern.gringotts.api;

public interface PlayerAccount extends Account {

    /**
     * Deposit an amount from the inventory or on-hand storage to the offline/chest storage.
     * @param value value to deposit
     * @return Result of transaction
     */
    TransactionResult deposit(double value);

    /**
     * Withdraw an amount from offline/chest storage to inventory or on-hand money.
     * @param value amount to withdraw
     * @return Result of transaction
     */
    TransactionResult withdraw(double value);
}
