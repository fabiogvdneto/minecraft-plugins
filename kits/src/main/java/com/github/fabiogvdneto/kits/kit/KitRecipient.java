package com.github.fabiogvdneto.kits.kit;

import com.github.fabiogvdneto.kits.exception.KitCooldownException;
import com.github.fabiogvdneto.kits.exception.KitLimitException;
import com.github.fabiogvdneto.kits.repository.data.KitRecipientData;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.UUID;

public class KitRecipient {

    private final Kit kit;
    private final UUID uid;

    private int redeemCount = 0;

    private Instant nextRedeemTime = Instant.EPOCH;

    protected KitRecipient(Kit kit, UUID uid) {
        this.kit = Objects.requireNonNull(kit);
        this.uid = Objects.requireNonNull(uid);
    }

    protected KitRecipient(Kit kit, KitRecipientData data) throws DateTimeParseException {
        this.kit = Objects.requireNonNull(kit);
        this.uid = data.uid();
        this.nextRedeemTime = Instant.parse(data.nextRedeemTime());
        this.redeemCount = data.redeemCount();
    }

    public UUID getUID() {
        return uid;
    }

    public Instant getNextRedeemTime() {
        return nextRedeemTime;
    }

    public int getRedeemCount() {
        return redeemCount;
    }

    protected void applyCooldown(Duration cooldown) {
        this.nextRedeemTime = Instant.now().plus(cooldown);
    }

    protected void increaseRedeemCount() {
        this.redeemCount++;
    }

    protected void checkCooldown() throws KitCooldownException {
        if (Instant.now().isBefore(nextRedeemTime))
            throw new KitCooldownException(nextRedeemTime);
    }

    protected void checkLimit(int redeemLimit) throws KitLimitException {
        if (redeemLimit >= 0 && redeemLimit <= redeemCount)
            throw new KitLimitException(redeemLimit);
    }

    protected KitRecipientData memento() {
        return new KitRecipientData(uid, nextRedeemTime.toString(), redeemCount);
    }
}
