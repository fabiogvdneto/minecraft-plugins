package com.github.fabiogvdneto.cursedwarps.warp;

import org.bukkit.Location;

public interface Warp {

    String getName();

    Location getLocation();

    boolean isClosed();

    void setClosed(boolean closed);

}
