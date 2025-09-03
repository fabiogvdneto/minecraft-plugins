package com.github.fabiogvdneto.kits.exception;

import java.time.Instant;

public class KitCooldownException extends RuntimeException {

    private final Instant endOfCooldown;

    /**
     *
     * @param endOfCooldown when the player will be able to redeem the kit again.
     */
    public KitCooldownException(Instant endOfCooldown) {
        super("can't redeem a kit while on cooldown");
        this.endOfCooldown = endOfCooldown;
    }

    public Instant whenAvailable() {
        return endOfCooldown;
    }
}
