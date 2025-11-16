package com.github.fabiogvdneto.common;

import com.github.fabiogvdneto.common.exception.PermissionRequiredException;
import org.bukkit.permissions.Permissible;

public record PermissionWrapper(String name) {

    public boolean test(Permissible permissible) {
        return permissible.hasPermission(name);
    }

    public void require(Permissible permissible) throws PermissionRequiredException {
        if (!test(permissible))
            throw new PermissionRequiredException(name);
    }
}
