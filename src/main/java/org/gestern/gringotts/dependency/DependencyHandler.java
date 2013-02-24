package org.gestern.gringotts.dependency;

/**
 * A DependencyHandler contains methods that deal with a specific Bukkit plugin dependency.
 * All interactions with any other plugin's API should happen through a handler, after checking that is is enabled.
 * This ensures that no unloaded code will be called.
 * 
 * @author jast
 *
 */
public interface DependencyHandler {

    /**
     * Return whether the plugin handled by this handler is enabled.
     * @return whether the plugin handled by this handler is enabled.
     */
    boolean enabled();

    /**
     * Return whether the dependency is loaded into classpath.
     * @return whether the dependency is loaded into classpath.
     */
    boolean exists();
}
