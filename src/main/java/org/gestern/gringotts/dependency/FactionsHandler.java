package org.gestern.gringotts.dependency;

import static org.gestern.gringotts.Language.LANG;
import static org.gestern.gringotts.Permissions.createvault_faction;
import static org.gestern.gringotts.dependency.Dependency.DEP;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.accountholder.AccountHolderProvider;
import org.gestern.gringotts.event.PlayerVaultCreationEvent;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;

public class FactionsHandler implements DependencyHandler, AccountHolderProvider {

    private final P plugin;

    public FactionsHandler(P plugin) {
        this.plugin = plugin;

        if (plugin != null) {
            Bukkit.getPluginManager().registerEvents(new FactionsListener(), Gringotts.G);
            Gringotts.G.registerAccountHolderProvider("faction", this);
        }
    }

    /**
     * Get a FactionAccountHolder for the faction of which player is a member, if any.
     * @param player
     * @return FactionAccountHolder for the faction of which player is a member, if any. null otherwise.
     */
    public FactionAccountHolder getFactionAccountHolder(Player player) {

        FPlayer fplayer = FPlayers.i.get(player);
        Faction playerFaction = fplayer.getFaction();
        return playerFaction != null? new FactionAccountHolder(playerFaction) : null;
    }

    /**
     * Get a FactionAccountHolder by id of the faction.
     * @param id
     * @return
     */
    public FactionAccountHolder getAccountHolderById(String id) {
        Faction faction = Factions.i.get(id);
        return faction !=null? new FactionAccountHolder(faction) : null;
    }

    @Override
    public boolean enabled() {
        return plugin!=null && plugin.isEnabled();
    }

    @Override
    public boolean exists() {
        return plugin != null;
    }

    /**
     * Get a FactionAccountHolder based on the name of the account.
     * Valid ids for this method are either raw faction ids, or faction ids or tags prefixed with "faction-" 
     * Only names beginning with "faction-" will be considered, and the rest of the string 
     * can be either a faction id or a faction tag.
     * @param name Name of the account.
     * @return a FactionAccountHolder based on the name of the account, if a valid faction could be found. null otherwise.
     */
    @Override
    public FactionAccountHolder getAccountHolder(String id) {

        String factionId = id;
        if (id.startsWith("faction-"))
            // requested a prefixed id, cut off the prefix!
            factionId = id.substring(8);

        // first try raw id
        FactionAccountHolder owner = getAccountHolderById(factionId);
        if (owner != null) return owner;

        // just in case, also try the tag
        Faction faction = Factions.i.getByTag(id);

        if (faction != null) 
            return new FactionAccountHolder(faction);

        return null;
    }
}

class FactionsListener implements Listener {

    @EventHandler
    public void vaultCreated(PlayerVaultCreationEvent event) {
        // some listener already claimed this event
        if (event.isValid()) return;

        if (event.getType().equals("faction")) {
            Player player = event.getCause().getPlayer();
            if (!createvault_faction.allowed(player)) {
                player.sendMessage(LANG.plugin_faction_noVaultPerm);
                return;
            }

            AccountHolder owner = DEP.factions.getFactionAccountHolder(player);
            if (owner==null) {
                player.sendMessage(LANG.plugin_faction_notInFaction);
                return;
            }

            event.setOwner(owner);
            event.setValid(true);
        }
    }
}

class FactionAccountHolder implements AccountHolder {

    private final Faction owner;

    /**
     * Default ctor.
     */
    public FactionAccountHolder(Faction owner) {
        this.owner = owner;
    }

    public FactionAccountHolder(String Id) {
        Faction faction = Factions.i.get(Id);

        if (faction != null)
            this.owner = faction;
        else throw new NullPointerException("Attempted to create account holder with null faction.");
    }

    @Override
    public String getName() {
        return owner.getTag();
    }

    @Override
    public void sendMessage(String message) {
        owner.sendMessage(message);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((owner == null) ? 0 : owner.getId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FactionAccountHolder other = (FactionAccountHolder) obj;
        if (owner == null) {
            if (other.owner != null)
                return false;
        } else if (!owner.getId().equals(other.owner.getId()))
            return false;
        return true;
    }

    @Override
    public String getType() {
        return "faction";
    }

    @Override
    public String toString() {
        return "FactionAccountHolder("+getName()+")";
    }

    @Override
    public String getId() {
        return owner.getId();
    }


}
