package org.gestern.gringotts.banking;

import org.gestern.gringotts.accountholder.AccountHolder;
import java.util.TreeSet;
import java.util.UUID;


public interface Corporation extends AccountHolder {

    public TreeSet<UUID> getOwners();

    public boolean addOwner(UUID id);

    public boolean isOwner(UUID id);

    public boolean removeOwner(UUID id);

}