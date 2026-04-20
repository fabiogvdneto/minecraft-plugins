package com.github.fabiogvdneto.warps.warp;

import com.github.fabiogvdneto.warps.repository.data.LocationData;
import com.github.fabiogvdneto.warps.repository.data.WarpData;
import org.bukkit.Location;

public class Warp {

    private final String name;
    private final Location location;
    private boolean closed;

    protected Warp(WarpData data) {
        this.name = data.name();
        this.location = data.location().bukkit();
        this.closed = data.closed();
    }

    protected Warp(String name, Location location) {
        this.name = name;
        this.location = location;
        this.closed = false;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    protected WarpData memento() {
        return new WarpData(name, new LocationData(location), closed);
    }
}
