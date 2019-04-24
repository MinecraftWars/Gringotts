package org.gestern.gringotts.api;

import java.util.Set;
import java.util.UUID;

public interface Eco {

    /**
     * Access a generic account for the given id. The type of the account is defined by the economy plugin.
     * The recommended behavior is to return a player account if a player of the name exists, and return any other
     * type of account
     * if no player of that name is known to the economy.
     *
     * @param id id of the account
     * @return The account representation.
     */
    Account account(String id);

    /**
     * Access a player account for the player with given name.
     * <p>
     * If possible, it is recommended to use the player(UUID)
     * method instead of this one.
     * <p>
     * If a player can have multiple accounts, the account returned is defined by the economy plugin.
     * For example, if the economy supports one account per world,
     * return the account associated with the world the player is currently in.
     * <p>
     * The representation of the money of a player account may be in-game items or virtual.
     *
     * @param name The name of the player owning the account.
     * @return The player account representation
     * @deprecated use {@link #player(UUID)}
     */
    @Deprecated
    PlayerAccount player(String name);

    /**
     * Access a player account for the player with given uuid.
     * <p>
     * If a player can have multiple accounts,
     * the account returned is defined by the economy plugin. For example, if the economy supports one account per
     * world, return the account associated with the world the player is currently in.
     * <p>
     * The representation of the money of a player account may be in-game items or virtual.
     *
     * @param id The unique id of the player owning the account.
     * @return The player account representation
     */
    PlayerAccount player(UUID id);

    /**
     * Access a bank account with given name.
     * <p>
     * The representation of the money in a bank account may be in-game items or virtual.
     * <p>
     * Support for this method is optional.
     *
     * @param name The name or id of the bank.
     * @return The bank account representation.
     */
    BankAccount bank(String name);

    /**
     * Access custom account type. Implementors must support this method, but may choose to implement via another
     * account type internally.
     *
     * @param type type of account
     * @param id   account id
     * @return an account of the given type and id
     */
    Account custom(String type, String id);

    /**
     * Access a Factions faction account with the given id.
     *
     * @param id faction id
     * @return the faction's account
     */
    Account faction(String id);

    /**
     * Access a Towny town account with the given id.
     *
     * @param id/name of a Towny town
     * @return account for a Towny town
     */
    Account town(String id);

    /**
     * Access a Towny nation account with the given id.
     *
     * @param id id/name of a Towny nation
     * @return account for a Towny nation with the given id.
     */
    Account nation(String id);

    /**
     * The currency for this Economy.
     *
     * @return The currency for this Economy.
     */
    Currency currency();

    /**
     * Return whether this economy supports banks.
     *
     * @return true if this economy has bank support, false otherwise.
     */
    boolean supportsBanks();

    /**
     * Get a set of all banks currently registered by the economy.
     *
     * @return a set of the names of all banks currently registered by the economy.
     */
    Set<String> getBanks();
}
