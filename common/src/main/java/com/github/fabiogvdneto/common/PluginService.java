package com.github.fabiogvdneto.common;

public interface PluginService {

    /**
     * Called when plugin is being enabled.
     */
    void enable();

    /**
     * Called when plugin is being disabled.
     */
    void disable();

}
