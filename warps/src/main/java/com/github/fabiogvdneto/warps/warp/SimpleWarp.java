package com.github.fabiogvdneto.warps.warp;

import com.github.fabiogvdneto.warps.repository.data.LocationData;
import com.github.fabiogvdneto.warps.repository.data.WarpData;
import org.bukkit.Location;

class SimpleWarp implements Place {

    private final String name;
    private final Location location;
    private boolean closed;

    public SimpleWarp(WarpData data) {
        this.name = data.name();
        this.location = data.location().bukkit();
        this.closed = data.closed();
    }

    public SimpleWarp(String name, Location location) {
        this.name = name;
        this.location = location;
        this.closed = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public WarpData memento() {
        return new WarpData(name, new LocationData(location), closed);
    }
}
