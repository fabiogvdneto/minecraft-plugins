package com.github.fabiogvdneto.cursedwarps.exception;

import com.github.fabiogvdneto.cursedwarps.user.TeleportationRequest;

public class TeleportationRequestClosedException extends IllegalStateException {

    private final TeleportationRequest.State state;

    public TeleportationRequestClosedException(TeleportationRequest.State state) {
        this.state = state;
    }

    public TeleportationRequest.State getState() {
        return state;
    }
}
