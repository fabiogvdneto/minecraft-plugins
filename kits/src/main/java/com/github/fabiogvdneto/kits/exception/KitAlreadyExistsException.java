package com.github.fabiogvdneto.kits.exception;

public class KitAlreadyExistsException extends RuntimeException {
    public KitAlreadyExistsException(String kitName) {
        super("a kit with the same id (" + kitName + ") already exists");
    }
}
