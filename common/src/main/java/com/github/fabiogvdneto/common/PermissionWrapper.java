package com.github.fabiogvdneto.common;

import com.github.fabiogvdneto.common.exception.PermissionRequiredException;
import org.bukkit.permissions.Permissible;

public class PermissionWrapper {

    private final String permission;

    public PermissionWrapper(String permission) {
        this.permission = permission;
    }

    public String extract() {
        return permission;
    }

    public boolean test(Permissible permissible) {
        return permissible.hasPermission(permission);
    }

    public void require(Permissible permissible) throws PermissionRequiredException {
        if (!test(permissible))
            throw new PermissionRequiredException(permission);
    }
}
