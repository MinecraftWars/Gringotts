package org.gestern.gringotts.accountholder;

import org.bukkit.OfflinePlayer;


public class PlayerAccountHolder implements AccountHolder {

    /** Actual player owning the account. */
    public final OfflinePlayer accountHolder;

    public PlayerAccountHolder(OfflinePlayer player) {		
        if (player != null)
            this.accountHolder = player;
        else throw new IllegalArgumentException("Attempted to create account holder with null player.");
    }

    @Override
    public String getName() {
        return accountHolder.getName();
    }

    @Override
    public void sendMessage(String message) {
        if (accountHolder.isOnline()) {
            accountHolder.getPlayer().sendMessage(message);
        }

    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (accountHolder.getName().hashCode());
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

        PlayerAccountHolder other = (PlayerAccountHolder) obj;
        return accountHolder.getName().equals(other.accountHolder.getName());
    }

    @Override
    public String getType() {
        return "player";
    }

    @Override
    public String toString() {
        return "PlayerAccountHolder("+getName()+")";
    }

    @Override
    public String getId() {
        return accountHolder.getUniqueId().toString();
    }
}
