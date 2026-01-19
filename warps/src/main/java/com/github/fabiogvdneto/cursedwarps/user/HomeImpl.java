package com.github.fabiogvdneto.cursedwarps.user;

import com.github.fabiogvdneto.cursedwarps.repository.data.HomeData;
import com.github.fabiogvdneto.cursedwarps.repository.data.LocationData;
import org.bukkit.Location;

class HomeImpl implements Home {

    private final String name;
    private final Location location;
    private boolean closed;

    HomeImpl(HomeData data) {
        this.name = data.name();
        this.location = data.location().bukkit();
        this.closed = data.closed();
    }

    HomeImpl(String name, Location location) {
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

    HomeData memento() {
        return new HomeData(name, new LocationData(location), closed);
    }
}
