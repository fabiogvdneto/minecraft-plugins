package com.github.fabiogvdneto.cursedwarps.user;

import com.github.fabiogvdneto.cursedwarps.exception.HomeAlreadyExistsException;
import com.github.fabiogvdneto.cursedwarps.exception.HomeNotFoundException;
import com.github.fabiogvdneto.cursedwarps.exception.TeleportationRequestAlreadyExistsException;
import com.github.fabiogvdneto.cursedwarps.exception.TeleportationRequestNotFoundException;
import org.bukkit.Location;

import java.time.Duration;
import java.util.Collection;
import java.util.UUID;

public interface User {

    UUID getUID();

    Collection<Home> getHomes();

    Home getHome(String name)
            throws HomeNotFoundException;

    Home createHome(String name, Location location)
            throws HomeAlreadyExistsException;

    void deleteHome(String name)
            throws HomeNotFoundException;

    Collection<TeleportationRequest> getTeleportationRequests();

    TeleportationRequest getTeleportationRequest(UUID sender)
            throws TeleportationRequestNotFoundException;

    TeleportationRequest createTeleportationRequest(UUID sender, Duration duration)
            throws TeleportationRequestAlreadyExistsException;
}
