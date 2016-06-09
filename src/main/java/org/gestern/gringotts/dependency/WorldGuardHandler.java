package org.gestern.gringotts.dependency;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.GlobalRegionManager;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.accountholder.AccountHolderProvider;
import org.gestern.gringotts.event.PlayerVaultCreationEvent;

import static org.gestern.gringotts.Language.LANG;
import static org.gestern.gringotts.Permissions.CREATEVAULT_ADMIN;
import static org.gestern.gringotts.Permissions.CREATEVAULT_WORLDGUARD;

public class WorldGuardHandler implements DependencyHandler, AccountHolderProvider {

    private final WorldGuardPlugin plugin;

    public WorldGuardHandler(WorldGuardPlugin plugin) {
        this.plugin = plugin;

        if (plugin != null) {
            Bukkit.getPluginManager().registerEvents(new WorldGuardListener(), Gringotts.G);
            Gringotts.G.registerAccountHolderProvider("worldguard", this);
        }
    }

    @Override
    public boolean enabled() {
        return plugin.isEnabled();
    }

    @Override
    public boolean exists() {
        return plugin != null;
    }


    @Override
    public WorldGuardAccountHolder getAccountHolder(String id) {
        // FIXME use something more robust than - as world-id delimiter
        // try explicit world+id first 
        String[] parts = id.split("-", 2);
        if (parts.length == 2) {
            WorldGuardAccountHolder wgah = getAccountHolder(parts[0], parts[1]);
            if (wgah != null) return wgah;
        }

        // try bare id in all worlds
        GlobalRegionManager manager = plugin.getGlobalRegionManager();
        for (World world: Bukkit.getWorlds()) {
            RegionManager worldManager = manager.get(world);
            if (worldManager.hasRegion(id)) {
                ProtectedRegion region = worldManager.getRegion(id);
                return new WorldGuardAccountHolder(world.getName(), region);
            }
        }

        return null;
    }

    /**
     * Get account holder for known world and region id.
     * @param world name of world
     * @param id worldguard region id
     * @return account holder for the region
     */
    public WorldGuardAccountHolder getAccountHolder(String world, String id) {
        World w = Bukkit.getWorld(world);
        if (w == null) return null;
        RegionManager manager = plugin.getRegionManager(w);
        if (manager == null) return null;

        if (manager.hasRegion(id)) {
            ProtectedRegion region = manager.getRegion(id);
            return new WorldGuardAccountHolder(world, region);
        }

        return null;
    }

    public class WorldGuardListener implements Listener {

        @EventHandler
        public void vaultCreated(PlayerVaultCreationEvent event) {
            // some listener already claimed this event
            if (event.isValid()) return;

            if ("region".equals(event.getType())) {
                Player player = event.getCause().getPlayer();
                if (!CREATEVAULT_WORLDGUARD.allowed(player)) {
                    player.sendMessage(LANG.plugin_faction_noVaultPerm);
                    return;
                }

                String regionId = event.getCause().getLine(2);
                String[] regionComponents = regionId.split("-",1);

                WorldGuardAccountHolder owner;
                if (regionComponents.length == 1) {
                    // try to guess the world
                    owner = getAccountHolder(regionComponents[0]);
                } else {
                    String world = regionComponents[0];
                    String id = regionComponents[1];
                    owner = getAccountHolder(world, id);
                }

                if (owner != null && (owner.region.hasMembersOrOwners() || CREATEVAULT_ADMIN.allowed(player))) {
                    DefaultDomain regionOwners = owner.region.getOwners();
                    if (regionOwners.contains(player.getName())) {
                        event.setOwner(owner);
                        event.setValid(true);
                    }
                }
            }
        }
    }
}

class WorldGuardAccountHolder implements AccountHolder {

    final String world;
    final ProtectedRegion region;

    public WorldGuardAccountHolder(String world, ProtectedRegion region) {
        this.world = world;
        this.region = region;
    }

    @Override
    public String getName() {
        return region.getId();
    }

    @Override
    public void sendMessage(String message) {
        // TODO send message to all members?
    }

    @Override
    public String getType() {
        return "worldguard";
    }

    @Override
    public String getId() {
        return world+"-"+region.getId();
    }

}
