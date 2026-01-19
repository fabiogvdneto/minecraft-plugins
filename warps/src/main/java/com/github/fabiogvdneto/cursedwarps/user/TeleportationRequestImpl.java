package com.github.fabiogvdneto.cursedwarps.user;

import com.github.fabiogvdneto.cursedwarps.exception.TeleportationRequestClosedException;

import java.time.Duration;
import java.util.UUID;

class TeleportationRequestImpl implements TeleportationRequest {

    private final UUID sender;
    private final UUID receiver;
    private final Duration duration;

    private long expiryTime;
    private boolean expired;
    private State state;

    TeleportationRequestImpl(UUID sender, UUID receiver, Duration duration) {
        this.sender = sender;
        this.receiver = receiver;
        this.duration = duration;
        this.expiryTime = System.currentTimeMillis() + duration.toMillis();
        this.state = State.OPEN;
    }

    @Override
    public UUID getSender() {
        return sender;
    }

    @Override
    public UUID getReceiver() {
        return receiver;
    }

    @Override
    public long getExpiryTime() {
        return expiryTime;
    }

    @Override
    public boolean hasExpired() {
        return expired || (expired = expiryTime < System.currentTimeMillis());
    }

    @Override
    public State getState() {
        return (state == State.OPEN && hasExpired()) ? (state = State.IGNORED) : state;
    }

    @Override
    public void cancel() throws TeleportationRequestClosedException {
        if (state != State.OPEN || hasExpired())
            throw new TeleportationRequestClosedException(state);

        state = State.IGNORED;
    }

    @Override
    public void accept() throws TeleportationRequestClosedException {
        if (state != State.OPEN || hasExpired())
            throw new TeleportationRequestClosedException(state);

        state = State.ACCEPTED;
    }

    @Override
    public void deny() throws TeleportationRequestClosedException {
        if (state != State.OPEN || hasExpired())
            throw new TeleportationRequestClosedException(state);

        state = State.DENIED;
        expiryTime += duration.toMillis();
    }
}
