package org.gestern.gringotts.api;

import java.util.Set;

public interface Eco {

    /**
     * Access a generic account for the given id. The type of the account is defined by the economy plugin.
     * The recommended behavior is to return a player account if a player of the name exists, and return any other type of account
     * if no player of that name is known to the economy.
     * @param id id of the account
     * @return The accoutn representation.
     */
    Account account(String id);

    /**
     * Access a player account for the player with given name. If a player can have multiple accounts,
     * the account returned is defined by the economy plugin. For example, if the economy supports one account per world,
     * return the account associated with the world the player is currently in.
     * The representation of the money of a player account may be in-game items or virtual.
     * @param name The name of the player owning the account.
     * @return The player account representation
     */
    PlayerAccount player(String name);

    /**
     * Access a bank account with given name.
     * The representation of the money in a bank account may be in-game items or virtual.
     * Support for this method is optional.
     * @param name The name or id of the bank.
     * @return The bank account representation.
     */
    Account bank(String name);

    /**
     * Access custom account type. Implementors must support this method, but may choose to implement via another account type internally.
     * @param type
     * @param id
     * @return
     */
    Account custom(String type, String id);

    /**
     * Access a Factions faction account with the given id.
     * @param id
     * @return
     */
    Account faction(String id);

    /**
     * Access a Towny town account with the given id.
     * @param id/name of a Towny town
     * @return account for a Towny town
     */
    Account town(String id);

    /**
     * Access a Towny nation account with the given id.
     * @param id id/name of a Towny nation
     * @return account for a Towny nation with the given id.
     */
    Account nation(String id);

    /**
     * The currency for this Economy.
     * @return
     */
    Currency currency();

    /**
     * Return whether this economy supports banks.
     * @return true if this economy has bank support, false otherwise.
     */
    boolean supportsBanks();

    /**
     * Get a set of all banks currently registered by the economy.
     * @return a set of the names of all banks currently registered by the economy.
     */
    Set<String> getBanks();
}
