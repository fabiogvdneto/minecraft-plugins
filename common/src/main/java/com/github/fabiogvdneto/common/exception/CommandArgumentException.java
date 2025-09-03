package com.github.fabiogvdneto.common.exception;

public class CommandArgumentException extends CommandExecutionException {

    private final int index;

    public CommandArgumentException(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}