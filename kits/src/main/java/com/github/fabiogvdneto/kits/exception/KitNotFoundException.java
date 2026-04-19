package com.github.fabiogvdneto.kits.exception;

public class KitNotFoundException extends RuntimeException {
    public KitNotFoundException(String kitName) {
        super("couldn't find a kit with the name " + kitName);
    }
}
