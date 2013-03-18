package org.gestern.gringotts.dependency;

import static org.gestern.gringotts.Language.LANG;
import static org.gestern.gringotts.Permissions.*;
import static org.gestern.gringotts.dependency.Dependency.DEP;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.accountholder.AccountHolderProvider;
import org.gestern.gringotts.event.PlayerVaultCreationEvent;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyEconomyObject;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public abstract class TownyHandler implements DependencyHandler {
    abstract public TownyAccountHolder getTownAccountHolder(Player player);
    abstract public TownyAccountHolder getNationAccountHolder(Player player);
    abstract public TownyAccountHolder getAccountHolderByAccountName(String name);

    /**
     * Get a valid towny handler if the plugin instance is valid. Otherwise get a fake one.
     * Apparently Towny needs this special treatment, or it will throw exceptions with unavailable classes. 
     * The same doesn't happen with Factions. I wonder why?
     * @param towny
     * @return
     */
    public static TownyHandler getTownyHandler(Plugin towny) {
        if (towny instanceof Towny)
            return new ValidTownyHandler((Towny)towny);
        else return new InvalidTownyHandler();
    }
}

/**
 * Dummy implementation of towny handler, if the plugin cannot be loaded.
 * @author jast
 */
class InvalidTownyHandler extends TownyHandler {

    @Override public boolean enabled() { return false; }
    @Override public boolean exists() { return false; }

    @Override
    public TownyAccountHolder getTownAccountHolder(Player player) {
        return null;
    }

    @Override
    public TownyAccountHolder getNationAccountHolder(Player player) {
        return null;
    }

    @Override
    public TownyAccountHolder getAccountHolderByAccountName(String name) {
        return null;
    }

}

class ValidTownyHandler extends TownyHandler implements AccountHolderProvider {

    private final Towny plugin;

    public ValidTownyHandler(Towny plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(new TownyListener(), Gringotts.G);
        Gringotts.G.registerAccountHolderProvider("town", this);
        Gringotts.G.registerAccountHolderProvider("nation", this);
    }

    /**
     * Get a TownyAccountHolder for the town of which player is a resident, if any.
     * @param player
     * @return TownyAccountHolder for the town of which player is a resident, if any. null otherwise.
     */
    public TownyAccountHolder getTownAccountHolder(Player player) {
        try {
            Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
            Town town = resident.getTown();
            return new TownyAccountHolder(town, "town");

        } catch (NotRegisteredException e) { }

        return null;
    }

    /**
     * Get a TownyAccountHolder for the nation of which player is a resident, if any.
     * @param player
     * @return TownyAccountHolder for the nation of which player is a resident, if any. null otherwise.
     */	
    public TownyAccountHolder getNationAccountHolder(Player player) {
        try {
            Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
            Town town = resident.getTown();
            Nation nation = town.getNation();
            return new TownyAccountHolder(nation, "nation");

        } catch (NotRegisteredException e) { }

        return null;
    }

    /**
     * Get a TownyAccountHolder based on the name of the account. 
     * Names beginning with "town-" will beget a town account holder and names beginning with "nation-"
     * a nation account holder.
     * @param name Name of the account.
     * @return a TownyAccountHolder based on the name of the account
     */
    public TownyAccountHolder getAccountHolderByAccountName(String name) {

        if (name.startsWith("town-")) {
            try { 
                TownyEconomyObject teo = TownyUniverse.getDataSource().getTown(name.substring(5)); 
                return new TownyAccountHolder(teo, "town");
            } 
            catch (NotRegisteredException e) { }
        }

        if (name.startsWith("nation-")) {
            try { 
                TownyEconomyObject teo = TownyUniverse.getDataSource().getNation(name.substring(7));
                return new TownyAccountHolder(teo, "nation");
            } catch (NotRegisteredException e) { }
        }

        return null;
    }


    @Override
    public boolean enabled() {
        return plugin != null && true;
    }

    @Override
    public boolean exists() {
        return plugin!=null;
    }

    @Override
    public TownyAccountHolder getAccountHolder(String id) {
        return getAccountHolderByAccountName(id);
    }

}


class TownyListener implements Listener {

    @EventHandler
    public void vaultCreated(PlayerVaultCreationEvent event) {
        // some listener already claimed this event
        if (event.isValid()) return;

        if (! DEP.towny.enabled()) return;

        String ownername = event.getCause().getLine(2);
        Player player = event.getCause().getPlayer();
        boolean forOther = ownername!=null && ownername.length()>0 && createvault_forothers.allowed(player);

        AccountHolder owner = null;
        if (event.getType().equals("town")) {
            if (!createvault_town.allowed(player)) {
                player.sendMessage(LANG.plugin_towny_noTownVaultPerm);
                return;
            }

            if (forOther) {
                owner = DEP.towny.getAccountHolderByAccountName("town-"+ownername);
                if (owner==null) return;
            } else {
                owner = DEP.towny.getTownAccountHolder(player);
            }

            if (owner == null) {
                player.sendMessage(LANG.plugin_towny_noTownResident);
                return;
            }

        } else if (event.getType().equals("nation")) {
            if (!createvault_nation.allowed(player)) {
                player.sendMessage(LANG.plugin_towny_noNationVaultPerm);
                return;
            }

            if (forOther) {
                owner = DEP.towny.getAccountHolderByAccountName("nation-"+ownername);
                if (owner==null) return;
            } else {
                owner = DEP.towny.getNationAccountHolder(player);
            }

            if (owner == null) {
                player.sendMessage(LANG.plugin_towny_notInNation);
                return;
            }
        }

        event.setOwner(owner);
        event.setValid(true);
    }
}

class TownyAccountHolder implements AccountHolder {

    public final TownyEconomyObject owner;
    public final String type;

    public TownyAccountHolder(TownyEconomyObject owner, String type) {
        this.owner = owner;
        this.type = type;
    }

    @Override
    public String getName() {
        return owner.getName();
    }

    @Override
    public void sendMessage(String message) {
        // TODO is it possible to send a message to a town?
        // TODO maybe just manually send to online residents
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getId() {
        return owner.getEconomyName();
    }

    @Override
    public String toString() {
        return "TownyAccountHolder("+owner.getName()+")";
    }

}

