package org.gestern.gringotts.banking;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.OfflinePlayer;

public class BankAccountHolder  extends FinanceHolder {

  public final OfflinePlayer player;
  
  public BankAccountHolder(OfflinePlayer player, Bank bank) {
    super(bank, "bankp");
    this.player = player;
  }
  
  public String getId() {
    return super.getId() + "-" + this.player.getName();
  }
  
  public int hashCode() {
    return new HashCodeBuilder(19, 29).append(getType()).append(this.bank.name).append(this.player.getName()).toHashCode();
  }

}
