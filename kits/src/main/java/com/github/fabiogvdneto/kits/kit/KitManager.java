package com.github.fabiogvdneto.kits.kit;

import com.github.fabiogvdneto.kits.exception.KitAlreadyExistsException;
import com.github.fabiogvdneto.kits.exception.KitNotFoundException;

import java.util.Collection;

public interface KitManager {

    /**
     *
     * @return all kits.
     */
    Collection<Kit> getAll();

    /**
     * @param name Kit name.
     * @return The kit.
     */
    Kit get(String name) throws KitNotFoundException;

    /**
     * Create a kit.
     * @param name Kit name.
     * @return The kit that was created.
     */
    Kit create(String name) throws KitAlreadyExistsException;

    /**
     * Delete a kit.
     * @param name Kit name.
     * @return The kit that was deleted.
     */
    Kit delete(String name) throws KitNotFoundException;

    /**
     * Check if a given kit exists.
     * @param name Kit name.
     * @return True if kit exists, false otherwise.
     */
    boolean exists(String name);

    /**
     * Set this data as dirty.
     * Dirty means that this data was modified and needs to be saved.
     * This method should be invoked everytime a kit is modified.
     */
    void dirty();

}
