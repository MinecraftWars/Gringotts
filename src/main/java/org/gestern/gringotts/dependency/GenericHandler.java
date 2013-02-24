package org.gestern.gringotts.dependency;

import org.bukkit.plugin.Plugin;

public class GenericHandler implements DependencyHandler {

    private final Plugin plugin;

    public GenericHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean enabled() {
        return plugin !=null && plugin.isEnabled();
    }

    @Override
    public boolean exists() {
        return plugin!=null;
    }

}
