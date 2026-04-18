package com.github.fabiogvdneto.kits.exception;

public class KitLimitException extends RuntimeException {

    private final int limit;

    public KitLimitException(int limit) {
        super("can't redeem a kit more than " + limit + " times.");
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }
}
