package com.github.fabiogvdneto.cursedwarps.warp;

import com.github.fabiogvdneto.cursedwarps.repository.data.LocationData;
import com.github.fabiogvdneto.cursedwarps.repository.data.WarpData;
import org.bukkit.Location;

class WarpImpl implements Warp {

    private final String name;
    private final Location location;
    private boolean closed;

    WarpImpl(WarpData data) {
        this.name = data.name();
        this.location = data.location().bukkit();
        this.closed = data.closed();
    }

    WarpImpl(String name, Location location) {
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

    WarpData memento() {
        return new WarpData(name, new LocationData(location), closed);
    }
}
