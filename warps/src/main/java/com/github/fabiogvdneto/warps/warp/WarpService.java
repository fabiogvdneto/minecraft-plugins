package com.github.fabiogvdneto.warps.warp;

import com.github.fabiogvdneto.warps.exception.WarpAlreadyExistsException;
import com.github.fabiogvdneto.warps.exception.WarpNotFoundException;
import org.bukkit.Location;

import java.util.Collection;

public interface WarpService {

    Collection<Warp> getAll();

    Warp get(String name) throws WarpNotFoundException;

    Warp create(String name, Location location) throws WarpAlreadyExistsException;

    void delete(String name) throws WarpNotFoundException;

}
