package com.github.fabiogvdneto.common.exception;

public class PermissionRequiredException extends Exception {

    private final String permission;

    public PermissionRequiredException(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
