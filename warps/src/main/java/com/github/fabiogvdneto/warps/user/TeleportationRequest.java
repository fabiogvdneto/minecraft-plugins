package com.github.fabiogvdneto.warps.user;

import com.github.fabiogvdneto.warps.exception.TeleportationRequestClosedException;

import java.time.Duration;
import java.util.UUID;

public class TeleportationRequest {

    public enum State { OPEN, IGNORED, ACCEPTED, DENIED }

    private final UUID sender;
    private final UUID receiver;
    private final Duration duration;

    private long expiryTime;
    private boolean expired;
    private State state;

    protected TeleportationRequest(UUID sender, UUID receiver, Duration duration) {
        this.sender = sender;
        this.receiver = receiver;
        this.duration = duration;
        this.expiryTime = System.currentTimeMillis() + duration.toMillis();
        this.state = State.OPEN;
    }

    public UUID getSender() {
        return sender;
    }

    public UUID getReceiver() {
        return receiver;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public boolean hasExpired() {
        return expired || (expired = expiryTime < System.currentTimeMillis());
    }

    public State getState() {
        return (state == State.OPEN && hasExpired()) ? (state = State.IGNORED) : state;
    }

    public void cancel() throws TeleportationRequestClosedException {
        if (state != State.OPEN || hasExpired())
            throw new TeleportationRequestClosedException(state);

        state = State.IGNORED;
    }

    public void accept() throws TeleportationRequestClosedException {
        if (state != State.OPEN || hasExpired())
            throw new TeleportationRequestClosedException(state);

        state = State.ACCEPTED;
    }

    public void deny() throws TeleportationRequestClosedException {
        if (state != State.OPEN || hasExpired())
            throw new TeleportationRequestClosedException(state);

        state = State.DENIED;
        expiryTime += duration.toMillis();
    }
}
