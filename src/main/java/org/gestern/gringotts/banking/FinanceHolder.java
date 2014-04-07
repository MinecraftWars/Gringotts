package org.gestern.gringotts.banking;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.gestern.gringotts.accountholder.AccountHolder;

public class FinanceHolder
  implements AccountHolder
{
  public final Bank bank;
  public final String type;
  
  public FinanceHolder(Bank bank, String type)
  {
    this.bank = bank;
    this.type = type;
  }
  
  public String getName()
  {
    return this.bank.name;
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public String getId()
  {
    return getType() + "-" + this.bank.name;
  }
  
  public void sendMessage(String string)
  {
    throw new RuntimeException("Messaging not implemented for banks.");
  }
  
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    FinanceHolder other = (FinanceHolder)obj;
    return getId().equals(other.getId());
  }
  
  public int hashCode()
  {
    return new HashCodeBuilder(19, 29).append(getType()).append(this.bank.name).toHashCode();
  }
}
