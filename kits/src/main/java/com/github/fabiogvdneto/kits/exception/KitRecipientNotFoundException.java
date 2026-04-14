package com.github.fabiogvdneto.kits.exception;

import java.util.UUID;

public class KitRecipientNotFoundException extends RuntimeException {
    public KitRecipientNotFoundException(UUID uid) {
        super("couldn't find a kit recipient with the uid " + uid);
    }
}
