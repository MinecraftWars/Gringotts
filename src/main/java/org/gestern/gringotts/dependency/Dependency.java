package org.gestern.gringotts.dependency;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Manages optional plugin dependencies.
 * 
 * @author jast
 *
 */
public enum Dependency {
	
	/** Singleton instance. */
	D;
	
	private final Logger log = Bukkit.getLogger();
		
	public final Plugin factions;
	public final Plugin towny;
	
	
	/**
	 * Initialize plugin dependencies. We expect them to have loaded before this is called.
	 */
	private Dependency() {
		
		factions = hookPlugin("Factions", "com.massivecraft.factions.P");
		towny = hookPlugin("Towny","com.palmergames.bukkit.towny.Towny");
	}
	

	private Plugin hookPlugin(String name, String classpath) {
		Plugin plugin;
		if (packagesExists(classpath)) {
			plugin = Bukkit.getServer().getPluginManager().getPlugin(name);
			log.info("[Gringotts] Plugin "+name+" hooked.");
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
