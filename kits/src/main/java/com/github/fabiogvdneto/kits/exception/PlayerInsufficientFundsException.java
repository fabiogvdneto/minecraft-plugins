package com.github.fabiogvdneto.kits.exception;

public class PlayerInsufficientFundsException extends RuntimeException {

    private final double amountRequired;

    public PlayerInsufficientFundsException(double amountRequired) {
        super("need at least " + amountRequired + " of balance.");
        this.amountRequired = amountRequired;
    }

    public double getAmount() {
        return amountRequired;
    }
}
