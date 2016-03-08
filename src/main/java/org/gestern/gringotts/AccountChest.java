package org.gestern.gringotts;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.gestern.gringotts.data.DAO;

import java.util.logging.Logger;

import static org.gestern.gringotts.Configuration.CONF;

/**
 * Represents a storage unit for an account.
 * 
 * @author jast
 *
 */
public class AccountChest {

    private final Logger log = Gringotts.G.getLogger();

    private final DAO dao = Gringotts.G.dao;

    /** Sign marking the chest as an account chest. */
    public final Sign sign;

    /** Account this chest belongs to. */
    public final GringottsAccount account;

    /**
     * Create Account chest based on a sign marking its position and belonging to an account.
     * @param sign the marker sign
     * @param account the account
     */
    public AccountChest(Sign sign, GringottsAccount account) {
        if (sign == null || account == null)
            throw new IllegalArgumentException("null arguments to AccountChest() not allowed. args were: sign: " + sign + ", account: " + account);
        this.sign = sign;
        this.account = account;
    }

    /**
     * The actual "chest" containing this account chest's stuff.
     * @return InventoryHolder for this account chest
     */
    private InventoryHolder chest() { 
        Block block = Util.chestBlock(sign);
        if (block == null) {
            return null;
        } else {
            return (InventoryHolder)block.getState();
        }
    }

    /** 
     * Location of the storage block of this account chest.
     * @return Location of the storage block of this account chest.
     */
    private Location chestLocation() {
        Block block = Util.chestBlock(sign);
        return block != null? block.getLocation() : null;
    }

    /**
     * Get inventory of this account chest.
     * @return inventory of this AccountChest, if any. otherwise null.
     */
    private Inventory inventory() {
        InventoryHolder chest = chest();
        return (chest != null)? chest.getInventory() : null;
    }

    /**
     * Get account inventory of this account chest, which is based on the container inventory.
     * @return account inventory of this account chest
     */
    private AccountInventory accountInventory() {
        Inventory inv = inventory();	
        return inv!=null? new AccountInventory(inv) : null;
    }

    /**
     * Test if this chest is valid, and if not, removes it from storage.
     * @return true if valid, false if not and was removed from storage.
     */
    private boolean updateValid() {
        if (notValid()) {
            log.info("Destroying orphaned vault: " + this);
            destroy();
            return false;
        }
        else return true;
    }

    /**
     * Return balance of this chest.
     * @return balance of this chest
     */
    public long balance() {

        if (!updateValid())
            return 0;

        AccountInventory inv = accountInventory();
        if (inv==null) return 0;

        return inv.balance();
    }

    /**
     * Attempts to add given amount to this chest. 
     * If the amount is larger than available space, the space is filled and the actually
     * added amount returned.
     * @return amount actually added
     */
    public long add(long value) {

        if (!updateValid())
            return 0;

        AccountInventory inv = accountInventory();
        if (inv==null) return 0;

        return inv.add(value);
    }

    /**
     * Attempts to remove given amount from this chest.
     * If the amount is larger than available items, everything is removed and the number of
     * removed items returned.
     * @param value amount to remove
     * @return amount actually removed from this chest
     */
    public long remove(long value) {

        if (!updateValid())
            return 0;

        AccountInventory inv = accountInventory();
        if (inv==null) return 0;

        return inv.remove(value);
    }

    /**
     * Checks whether this chest is currently a valid vault.
     * It is considered valid when the sign block contains [vault] or [(type) vault] on the first line,
     * a name on the third line and has a chest associated with it.
     * 
     * @return false if the chest can be considered a valid vault
     */
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean notValid() {
        // is it still a sign?
        if ( ! Util.isSignBlock(sign.getBlock()) ) 
            return true;

        // TODO refactor: common definition of valid vault types
        String[] lines = sign.getLines();
        String line0 = lines[0].toLowerCase();

        if ( ! line0.matches(CONF.vaultPattern)) 
            return true;
        if ( lines[2] == null || lines[2].length() == 0) 
            return true;

        return chest() == null;

    }

    /**
     * Triggered on destruction of physical chest or sign.
     */
    void destroy() {
        dao.destroyAccountChest(this);
        sign.getBlock().breakNaturally();
    }

    @Override
    public String toString() {
        Location loc = sign.getLocation();
        return "[vault] " 
        + loc.getBlockX() + ", "
        + loc.getBlockY() + ", "
        + loc.getBlockZ() + ", "
        + loc.getWorld();    			
    }

    /**
     * Connected chests that comprise the inventory of this account chest.
     * @return chest blocks connected to this chest, if any
     */
    private Chest[] connectedChests() {
        Inventory inv = inventory();
        if (inv == null)
            return new Chest[0];

        if (inv instanceof DoubleChestInventory) {
            DoubleChestInventory dinv = (DoubleChestInventory)inv;
            Chest left = (Chest)(dinv.getLeftSide().getHolder());
            Chest right = (Chest)(dinv.getRightSide().getHolder());

            return new Chest[] {left, right};
        } else {
            InventoryHolder invHolder = inv.getHolder();
            if (invHolder instanceof Chest)
                return new Chest[] {(Chest)(inv.getHolder())};
        }

        return new Chest[0];
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + sign.getLocation().hashCode();
        return result;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        AccountChest other = (AccountChest) obj;
        return this.sign.getLocation().equals(other.sign.getLocation());
    }

    /**
     * Determine whether the chest of another AccountChest would be connected to this chest.
     * @param chest another AccountChest
     * @return whether the chest of another AccountChest would be connected to this chest
     */
    public boolean connected(AccountChest chest) {

        // no valid account chest anymore -> no connection
        if (! updateValid())
            return false;

        Location myLoc = chestLocation();

        if (myLoc == null)
            return false;

        if (myLoc.equals(chest.chestLocation()))
            return true;

        // no double chest -> no further connection possible
        if (! (inventory() instanceof DoubleChestInventory))
            return false;


        for (Chest c : chest.connectedChests())
            if (c.getLocation().equals(myLoc))
                return true;

        return false;
    }

    public GringottsAccount getAccount() {
        return account;
    }

}
