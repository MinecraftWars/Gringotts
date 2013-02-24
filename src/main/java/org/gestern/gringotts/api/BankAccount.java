package org.gestern.gringotts.api;

public interface BankAccount extends Account {

    /**
     * Add an owner to this bank account.
     * @param player Player to add as owner to this bank account.
     * @return This bank account with the owner added.
     */
    BankAccount addOwner(String player);

    /**
     * Add a member to this bank account.
     * @param player Player to add as owner to this bank account.
     * @return This bank account with the member added.
     */
    BankAccount addMember(String player);

    /**
     * Return whether a player is an owner of this bank account.
     * @param player player to check for ownership
     * @return whether a player is an owner of this bank account.
     */
    boolean isOwner(String player);

    /**
     * Return whether a player is a member of this bank account.
     * @param player player to check for membership
     * @return whether a player is an member of this bank account.
     */
    boolean isMember(String player);
}
