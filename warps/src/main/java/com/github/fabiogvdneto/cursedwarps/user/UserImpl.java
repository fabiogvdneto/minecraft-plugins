package com.github.fabiogvdneto.cursedwarps.user;

import com.github.fabiogvdneto.cursedwarps.exception.HomeAlreadyExistsException;
import com.github.fabiogvdneto.cursedwarps.exception.HomeNotFoundException;
import com.github.fabiogvdneto.cursedwarps.exception.TeleportationRequestAlreadyExistsException;
import com.github.fabiogvdneto.cursedwarps.exception.TeleportationRequestNotFoundException;
import com.github.fabiogvdneto.cursedwarps.repository.data.HomeData;
import com.github.fabiogvdneto.cursedwarps.repository.data.UserData;
import org.bukkit.Location;

import java.time.Duration;
import java.util.*;

class UserImpl implements User {

    private final UUID uid;
    private final Map<String, Home> homes;
    private final Map<UUID, TeleportationRequest> tprequests;

    UserImpl(UserData userData) {
        this(userData.uid());

        for (HomeData homeData : userData.homes()) {
            homes.put(homeData.name().toLowerCase(), new HomeImpl(homeData));
        }
    }

    UserImpl(UUID uid) {
        this.uid = uid;
        this.homes = new HashMap<>();
        this.tprequests = new HashMap<>();
    }

    @Override
    public UUID getUID() {
        return uid;
    }

    @Override
    public Collection<Home> getHomes() {
        return homes.values();
    }

    @Override
    public Home getHome(String name) throws HomeNotFoundException {
        Home home = homes.get(name.toLowerCase());

        if (home == null)
            throw new HomeNotFoundException();

        return home;
    }

    @Override
    public Home createHome(String name, Location location) throws HomeAlreadyExistsException {
        Home home = new HomeImpl(name, location);

        if (homes.putIfAbsent(name.toLowerCase(), home) != null)
            throw new HomeAlreadyExistsException();

        return home;
    }

    @Override
    public void deleteHome(String name) throws HomeNotFoundException {
        if (homes.remove(name.toLowerCase()) == null)
            throw new HomeNotFoundException();
    }

    @Override
    public Collection<TeleportationRequest> getTeleportationRequests() {
        return tprequests.values();
    }

    @Override
    public TeleportationRequest getTeleportationRequest(UUID sender) throws TeleportationRequestNotFoundException {
        TeleportationRequest request = tprequests.get(sender);

        if (request == null)
            throw new TeleportationRequestNotFoundException();

        return request;
    }

    @Override
    public TeleportationRequest createTeleportationRequest(UUID sender, Duration duration)
            throws TeleportationRequestAlreadyExistsException {
        TeleportationRequest request = tprequests.get(sender);

        if (request != null && !request.hasExpired())
            throw new TeleportationRequestAlreadyExistsException(request);

        request = new TeleportationRequestImpl(sender, uid, duration);
        tprequests.put(sender, request);
        return request;
    }

    public UserData memento() {
        List<HomeData> homeData = homes.values().stream().map(home -> ((HomeImpl) home).memento()).toList();
        return new UserData(uid, homeData);
    }
}
