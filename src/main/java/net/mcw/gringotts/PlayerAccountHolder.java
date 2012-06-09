package net.mcw.gringotts;

import org.bukkit.entity.Player;



public class PlayerAccountHolder extends AccountHolder {

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accountHolder == null) ? 0 : accountHolder.getName().hashCode());
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
		if (accountHolder == null) {
			if (other.accountHolder.getName() != null)
				return false;
		} else if (!accountHolder.getName().equals(other.accountHolder.getName()))
			return false;
		return true;
	}

	public final Player accountHolder;
	
	public PlayerAccountHolder(Player player) {
		this.accountHolder = player;
	}

	@Override
	public String getName() {
		return accountHolder.getName();
	}

	@Override
	public void sendMessage(String message) {
		accountHolder.sendMessage(message);
		
	}
}
