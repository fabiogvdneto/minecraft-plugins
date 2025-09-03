package com.github.fabiogvdneto.warps.exception;

import com.github.fabiogvdneto.warps.user.TeleportationRequest;

public class TeleportationRequestAlreadyExistsException extends Exception {

    private final TeleportationRequest value;

    public TeleportationRequestAlreadyExistsException(TeleportationRequest value) {
        this.value = value;
    }

    public TeleportationRequest getValue() {
        return value;
    }
}
