package com.github.fabiogvdneto.kits.exception;

import java.util.UUID;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(String name) {
        super("could't find a player with the name " + name);
    }

    public PlayerNotFoundException(UUID uid) {
        super("couldn't find a player with the uuid " + uid);
    }
}
