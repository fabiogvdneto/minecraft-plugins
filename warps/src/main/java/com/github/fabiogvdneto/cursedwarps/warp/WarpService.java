package com.github.fabiogvdneto.cursedwarps.warp;

import com.github.fabiogvdneto.cursedwarps.exception.WarpAlreadyExistsException;
import com.github.fabiogvdneto.cursedwarps.exception.WarpNotFoundException;
import org.bukkit.Location;

import java.util.Collection;

public interface WarpService {

    Collection<Warp> getAll();

    Warp get(String name) throws WarpNotFoundException;

    Warp create(String name, Location location) throws WarpAlreadyExistsException;

    void delete(String name) throws WarpNotFoundException;

}
