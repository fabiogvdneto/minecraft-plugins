package com.github.fabiogvdneto.warps.user;

import com.github.fabiogvdneto.warps.repository.data.HomeData;
import com.github.fabiogvdneto.warps.repository.data.LocationData;
import org.bukkit.Location;

public class Home {

    private final String name;
    private final Location location;
    private boolean closed;

    protected Home(HomeData data) {
        this.name = data.name();
        this.location = data.location().bukkit();
        this.closed = data.closed();
    }

    protected Home(String name, Location location) {
        this.name = name;
        this.location = location;
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

    protected HomeData memento() {
        return new HomeData(name, new LocationData(location), closed);
    }
}
