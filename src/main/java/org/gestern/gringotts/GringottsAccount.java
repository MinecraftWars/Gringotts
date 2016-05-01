package org.gestern.gringotts;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.accountholder.PlayerAccountHolder;
import org.gestern.gringotts.api.TransactionResult;
import org.gestern.gringotts.currency.Denomination;
import org.gestern.gringotts.data.DAO;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.logging.Logger;

import static org.gestern.gringotts.Configuration.CONF;
import static org.gestern.gringotts.Gringotts.G;
import static org.gestern.gringotts.Permissions.USEVAULT_ENDERCHEST;
import static org.gestern.gringotts.Permissions.USEVAULT_INVENTORY;
import static org.gestern.gringotts.api.TransactionResult.*;

/**
 * Implementation of inventory-based accounts with a virtual overflow capacity.
 * Has support for player accounts specifically and works with any other container storage.
 * 
 * @author jast
 */
public class GringottsAccount {

    @SuppressWarnings("unused")
    private final Logger log = G.getLogger();
    private final DAO dao = G.dao;

    public final AccountHolder owner;

    public GringottsAccount(AccountHolder owner) {
        if (owner == null) {
            throw new IllegalArgumentException("owner parameter to Account constructor may not be null");
        }
        this.owner = owner;
    }

    /**
     * Current balance of this account in cents
     * @return current balance of this account in cents
     */
    public long balance() {

        CompletableFuture<Long> cents = getCents();
        CompletableFuture<Long> playerInv = countPlayerInventory();
        CompletableFuture<Long> chestInv = countChestInventories();

        // order of combination is important, because chestInv/playerInv might have to run on main thread
        CompletableFuture<Long> f =
            chestInv.thenCombine(playerInv, (c,p) -> c+p)
                    .thenCombine(cents, (b,c) -> b+c);

        return getTimeout(f);
    }

    /**
     * Current balance this account has in chest(s) in cents
     * @return current balance this account has in chest(s) in cents
     */
    public long vaultBalance() {
        return getTimeout(countChestInventories());
    }

    /**
     * Current balance this account has in inventory in cents
     * @return current balance this account has in inventory in cents
     */
    public long invBalance() {
        CompletableFuture<Long> cents = getCents();
        CompletableFuture<Long> playerInv = countPlayerInventory();

        CompletableFuture<Long> f =
            cents.thenCombine(playerInv, (p,c) -> p+c);

        return getTimeout(f);
    }

    /**
     * Add an amount in cents to this account if able to.
     * @param amount amount in cents to add
     * @return Whether amount successfully added
     */
    public TransactionResult add(long amount) {

        Callable<TransactionResult> callMe = () -> {

            // Cannot add negative amount
            if (amount < 0)
                return ERROR;

            long centsStored = dao.getCents(this);

            long remaining = amount + centsStored;

            // add currency to account's vaults
            if (CONF.usevaultContainer) {
                for (AccountChest chest : dao.getChests(this)) {
                    remaining -= chest.add(remaining);
                    if (remaining <= 0) break;
                }
            }

            // add stuff to player's inventory and enderchest too, when they are online
            Optional<Player> playerOpt = playerOwner();
            if (playerOpt.isPresent()) {
                Player player = playerOpt.get();
                if (USEVAULT_INVENTORY.allowed(player))
                    remaining -= new AccountInventory(player.getInventory()).add(remaining);
                if (CONF.usevaultEnderchest && USEVAULT_ENDERCHEST.allowed(player))
                    remaining -= new AccountInventory(player.getEnderChest()).add(remaining);
            }

            // allow smallest denom value as threshold for available space
            // TODO make maximum virtual amount configurable
            // this is under the assumption that there is always at least 1 denomination
            List<Denomination> denoms = CONF.currency.denominations();
            long smallestDenomValue = denoms.get(denoms.size()-1).value;
            if (remaining < smallestDenomValue) {
                dao.storeCents(this, remaining);
                remaining = 0;
            }

            if (remaining == 0)
                return SUCCESS;

            // failed, remove the stuff added so far
            remove(amount - remaining);

            return INSUFFICIENT_SPACE;
        };

        return getTimeout(callSync(callMe));
    }

    /**
     * Attempt to remove an amount in cents from this account. 
     * If the account contains less than the specified amount, returns false
     * @param amount amount in cents to remove
     * @return amount actually removed.
     */
    public TransactionResult remove(long amount) {

        Callable<TransactionResult> callMe = () -> {
            // Cannot remove negative amount
            if (amount < 0)
                return ERROR;

            // Make sure we have enough to remove
            if (balance() < amount)
                return INSUFFICIENT_FUNDS;

            long remaining = amount;

            // Now remove the physical amount left
            if (CONF.usevaultContainer) {
                for (AccountChest chest : dao.getChests(this))
                    remaining -= chest.remove(remaining);
            }

            Optional<Player> playerOpt = playerOwner();
            if (playerOpt.isPresent()) {
                Player player = playerOpt.get();
                if (USEVAULT_INVENTORY.allowed(player))
                    remaining -= new AccountInventory(player.getInventory()).remove(remaining);
                if (CONF.usevaultEnderchest && USEVAULT_ENDERCHEST.allowed(player))
                    remaining -= new AccountInventory(player.getEnderChest()).remove(remaining);
            }

            if (remaining < 0)
                // took too much, pay back the extra
                return add(-remaining);

            if (remaining > 0) {
                // cannot represent the leftover in our denominations, take them from the virtual reserve
                long cents = dao.getCents(this);
                dao.storeCents(this, cents - remaining);
            }

            return SUCCESS;
        };

        return getTimeout(callSync(callMe));
    }


    @Override
    public String toString() {
        return "Account ("+owner+")";
    }

    /**
     * Returns the player owning this account, if the owner is actually a player and online.
     * @return Optional of the player owning this account, if the owner is actually a player and online, otherwise empty.
     */
    private Optional<Player> playerOwner() {
        if (owner instanceof PlayerAccountHolder) {
            OfflinePlayer player = ((PlayerAccountHolder) owner).accountHolder;
            return Optional.ofNullable(player.getPlayer());
        }

        return Optional.empty();
    }


    /**
     * Call a function in the main thread. The returned CompletionStage will be completed after the function is called.
     * @param callMe function to call
     * @return will be completed after function is called
     */
    private static <V> CompletableFuture<V> callSync(Callable<V> callMe) {
        final CompletableFuture<V> f = new CompletableFuture<>();
        Runnable runMe = () -> {
            try {
                f.complete(callMe.call());
            } catch (Exception e) {
                f.completeExceptionally(e);
            }
        };

        if (Bukkit.isPrimaryThread()) runMe.run();
        else Bukkit.getScheduler().scheduleSyncDelayedTask(G, runMe);
        return f;
    }

    private CompletableFuture<Long> countChestInventories() {

        Callable<Long> callMe = () -> {
            List<AccountChest> chests = dao.getChests(this);
            long balance = 0;
            if (CONF.usevaultContainer) {
                for (AccountChest chest : chests)
                    balance += chest.balance();
            }

            Optional<Player> playerOpt = playerOwner();
            if (playerOpt.isPresent()) {
                Player player = playerOpt.get();
                if (CONF.usevaultEnderchest && USEVAULT_ENDERCHEST.allowed(player))
                    balance += new AccountInventory(player.getEnderChest()).balance();
            }
            return balance;
        };

        return callSync(callMe);
    }

    private CompletableFuture<Long> countPlayerInventory() {

        Callable<Long> callMe = () -> {
            long balance = 0;

            Optional<Player> playerOpt = playerOwner();
            if (playerOpt.isPresent() && USEVAULT_INVENTORY.allowed(playerOpt.get())) {
                Player player = playerOpt.get();
                balance += new AccountInventory(player.getInventory()).balance();
            }
            return balance;
        };

        return callSync(callMe);
    }

    private CompletableFuture<Long> getCents() {
        return CompletableFuture.supplyAsync(() -> dao.getCents(this));
    }

    private <V> V getTimeout(CompletableFuture<V> f) {
        try {
            return f.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException|ExecutionException|TimeoutException e) {
            throw new GringottsException(e);
        }
    }

}
