package org.gestern.gringotts.dependency;

import java.util.logging.Logger;

import net.milkbowl.vault.Vault;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.gestern.gringotts.Gringotts;

import com.massivecraft.factions.P;
import com.palmergames.bukkit.towny.Towny;

import static org.gestern.gringotts.Util.versionAtLeast;

/**
 * Manages optional plugin dependencies.
 * 
 * @author jast
 *
 */
public enum Dependency {
	
	/** Singleton instance. */
	D;
	
	private final Logger log = Gringotts.gringotts.getLogger();
		
	public final P factions;
	public final Towny towny;
	public final Vault vault;
	
	
	/**
	 * Initialize plugin dependencies. We expect them to have loaded before this is called.
	 */
	private Dependency() {
		
		factions = (P)hookPlugin("Factions", "com.massivecraft.factions.P","1.6.9.1");
		towny = (Towny)hookPlugin("Towny","com.palmergames.bukkit.towny.Towny","0.82.0.0");
		vault = (Vault)hookPlugin("Vault","net.milkbowl.vault.Vault","1.2.17");
	}
	

	/**
	 * Attempt to hook a plugin dependency.
	 * @param name Name of the plugin.
	 * @param classpath classpath to check for
	 * @param minVersion minimum version of the plugin. The plugin will still be hooked if this version is not satisfied,
	 * 		but a warning will mbe emitted.
	 * @return the plugin object when hooked successfully, or null if not.
	 */
	private Plugin hookPlugin(String name, String classpath, String minVersion) {
		Plugin plugin;
		if (packagesExists(classpath)) {
			plugin = Bukkit.getServer().getPluginManager().getPlugin(name);
			log.info("[Gringotts] Plugin "+name+" hooked.");
			
			PluginDescriptionFile desc = plugin.getDescription();
			String version = desc.getVersion();
			if (!versionAtLeast(version, minVersion)) {
				log.warning("[Gringotts] Plugin dependency "+ name +" is version " + version + 
						". Expected at least "+ minVersion +" -- Errors may occur.");
			}
		} else {
        	log.info("[Gringotts] Unable to hook plugin " + name);
        	plugin = null;
        }
		
		return plugin;
	}
	
    /**
     * Determines if all packages in a String array are within the Classpath
     * This is the best way to determine if a specific plugin exists and will be
     * loaded. If the plugin package isn't loaded, we shouldn't bother waiting
     * for it!
     * @param packages String Array of package names to check
     * @return Success or Failure
     */
    private static boolean packagesExists(String...packages) {
        try {
            for (String pkg : packages) {
                Class.forName(pkg);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
}
