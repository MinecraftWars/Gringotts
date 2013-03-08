package org.gestern.gringotts.dependency;

import static org.gestern.gringotts.Language.LANG;
import static org.gestern.gringotts.Permissions.createvault_nation;
import static org.gestern.gringotts.Permissions.createvault_town;
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
            return new TownyAccountHolder(town);

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
            return new TownyAccountHolder(nation);

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
        TownyEconomyObject teo = townyObject(name);
        return teo!=null? new TownyAccountHolder(teo) : null;
    }

    /**
     * Get a towny Town or Nation for a given name.
     * @param name name of town or nation
     * @return Town or Nation object for given name
     */
    private TownyEconomyObject townyObject(String name) {

        TownyEconomyObject teo = null;

        if (name.startsWith("town-")) {
            try { teo = TownyUniverse.getDataSource().getTown(name.substring(5)); } 
            catch (NotRegisteredException  e) { }
        }

        if (name.startsWith("nation-")) {
            try { teo = TownyUniverse.getDataSource().getNation(name.substring(7));
            } catch (NotRegisteredException  e) { }
        }

        return teo;
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

        Player player = event.getCause().getPlayer();
        AccountHolder owner = null;
        if (event.getType().equals("town")) {
            if (!createvault_town.allowed(player)) {
                player.sendMessage(LANG.plugin_towny_noTownVaultPerm);
                return;
            }

            owner = DEP.towny.getTownAccountHolder(player);
            if (owner == null) {
                player.sendMessage(LANG.plugin_towny_noTownResident);
                return;
            }

        } else if (event.getType().equals("nation")) {
            if (!createvault_nation.allowed(player)) {
                player.sendMessage(LANG.plugin_towny_noNationVaultPerm);
                return;
            }

            owner = DEP.towny.getNationAccountHolder(player);
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

    TownyEconomyObject owner;

    public TownyAccountHolder(TownyEconomyObject owner) {
        this.owner = owner;
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
        return "towny";
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

