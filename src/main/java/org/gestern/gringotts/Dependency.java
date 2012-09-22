package org.gestern.gringotts;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Manages optional plugin dependencies.
 * 
 * @author jast
 *
 */
public class Dependency {

	private static Dependency dependency;
	
	private final Logger log = Bukkit.getLogger();
		
	public final Plugin factions;
	
	
	/**
	 * 
	 */
	private Dependency() {
		
		if (packagesExists("com.massivecraft.factions.P")) {
	        factions = Bukkit.getServer().getPluginManager().getPlugin("Factions");
            log.info("[Gringotts] Factions hooked.");
        } else {
        	log.info("[Gringotts] Unable to hook factions.");
        	factions = null;
        }
    
	}
	
	/**
	 * Get the Dependency instance.
	 * @return the Dependency instance
	 */
	public static Dependency dependency() {
		// lazy initialization just to be sure it doesn't attempt loading before other stuff is ready
		if (dependency == null)
			dependency = new Dependency();
		
		return dependency;
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
