package com.github.fabiogvdneto.cursedwarps.exception;

public class HomeLimitReachedException extends Exception {

    private final int limit;
    private final int count;

    public HomeLimitReachedException(int limit, int count) {
        this.limit = limit;
        this.count = count;
    }

    public int getLimit() {
        return limit;
    }

    public int getCount() {
        return count;
    }
}
