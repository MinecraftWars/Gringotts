package org.gestern.gringotts.api;

/**
 * Defines actions possible on an account in an economy.
 */
public interface Account {

    /**
     * Check if this account exists in the economy system.
     *
     * @return true if this account exists, false otherwise.
     */
    boolean exists();

    /**
     * Create this account, if it does not exist, and creation is possible.
     * If creation was successful, the Account object returned as result of this method must return true to exists().
     *
     * @return Representation of this account after it has been created.
     */
    Account create();

    /**
     * Delete this account from the economy system, if it exists, and account deletion is possible.
     * If deletion was successful, the Account object returned as result of this method must return false to exists().
     *
     * @return Representation of this account after it has been deleted.
     */
    Account delete();

    /**
     * Return the balance of this account.
     *
     * @return the balance of this account.
     */
    double balance();

    /**
     * Return the vault balance of this account.
     *
     * @return the vault balance of this account.
     */
    double vaultBalance();

    /**
     * Return the inventory balance of this account.
     *
     * @return the inventory balance of this account.
     */
    double invBalance();

    /**
     * Return whether this account has at least the specified amount.
     *
     * @param value amount to check
     * @return whether this account has at least the specified amount.
     */
    boolean has(double value);

    /**
     * Check if more money can be added to this account's balance.
     *
     * @param value The amount of money to check the space for.
     * @return whether the specified amount of money can fit in this account.
     */
    boolean canAdd(double value);

    /**
     * Set the balance of this account.
     * Note: it is preferred to use the add and remove methods for any transactions that actually have
     * the goal of adding or removing a certain amount.
     *
     * @param newBalance the new balance of this account.
     * @return result of setting the balance (success or failure type).
     */
    TransactionResult setBalance(double newBalance);

    /**
     * Add an amount to this account's balance.
     *
     * @param value The amount to be added.
     * @return The result of adding (success or failure type).
     */
    TransactionResult add(double value);

    /**
     * Remove an amount from this account's balance.
     *
     * @param value amount to be removed
     * @return result of removing (success or failure type)
     */
    TransactionResult remove(double value);

    /**
     * Send an amount to another account. If the transfer fails, both sender and recipient will
     * have unchanged account balance. To complete the transaction,
     * use the {@link Transaction#to(Account)} method on the result of this call.
     * Before sending, it is possible to apply taxes with the {@link Transaction#withTaxes()} method.
     *
     * @param value amount to be transferred
     * @return A transaction object, which may be used to complete the transaction or add additional properties.
     */
    Transaction send(double value);

    /**
     * Return the type of this account. Default account types are "player" and "bank".
     * The economy plugin specifies any other types.
     * The method call Eco.custom(type,id) result in this account for parameters this.type() and this.id().
     *
     * @return the type of this account.
     */
    String type();

    /**
     * Return the id of this account. For players, this is the player uuid. For banks, this is the name of the bank.
     * The method call Eco.custom(type,id) results in this account for parameters this.type() and this.id().
     *
     * @return the id of this account
     */
    String id();

    /**
     * Send a message to the owner or owners of this account.
     * <p>
     * Depending on the type of account, no player could be the owner of an account.
     * In this case, send the message to the console.
     * </p>
     *
     * @param message Message to send.
     */
    void message(String message);

}
