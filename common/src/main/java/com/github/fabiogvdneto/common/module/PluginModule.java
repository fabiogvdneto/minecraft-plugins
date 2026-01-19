package com.github.fabiogvdneto.common.module;

public interface PluginModule {

    /**
     * Called when plugin is being enabled.
     */
    void load();

    /**
     * Called when plugin is being disabled.
     */
    void unload();

}
