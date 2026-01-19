package com.github.fabiogvdneto.cursedwarps.exception;

import com.github.fabiogvdneto.cursedwarps.user.TeleportationRequest;

public class TeleportationRequestAlreadyExistsException extends Exception {

    private final TeleportationRequest value;

    public TeleportationRequestAlreadyExistsException(TeleportationRequest value) {
        this.value = value;
    }

    public TeleportationRequest getValue() {
        return value;
    }
}
