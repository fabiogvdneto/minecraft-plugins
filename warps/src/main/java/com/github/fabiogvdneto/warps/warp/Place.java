package com.github.fabiogvdneto.warps.warp;

import org.bukkit.Location;

public interface Place {

    String getName();

    Location getLocation();

    boolean isClosed();

    void setClosed(boolean closed);

}
