package com.github.fabiogvdneto.kits.kit;

import com.github.fabiogvdneto.kits.exception.KitCooldownException;
import com.github.fabiogvdneto.kits.exception.KitLimitException;
import com.github.fabiogvdneto.kits.repository.data.KitRecipientData;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.UUID;

public class KitRecipientImpl implements KitRecipient {

    private final KitImpl kit;
    private final UUID uid;

    private int redeemCount = 0;

    private Instant nextRedeemTime = Instant.EPOCH;

    KitRecipientImpl(KitImpl kit, UUID uid) {
        this.kit = Objects.requireNonNull(kit);
        this.uid = Objects.requireNonNull(uid);
    }

    KitRecipientImpl(KitImpl kit, KitRecipientData data) throws DateTimeParseException {
        this.kit = Objects.requireNonNull(kit);
        this.uid = data.uid();
        this.nextRedeemTime = Instant.parse(data.nextRedeemTime());
        this.redeemCount = data.redeemCount();
    }

    @Override
    public UUID getUID() {
        return uid;
    }

    @Override
    public Instant getNextRedeemTime() {
        return nextRedeemTime;
    }

    public void setNextRedeemTime(Instant nextRedeemTime) {
        this.nextRedeemTime = nextRedeemTime;
    }

    public void applyCooldown(Duration cooldown) {
        setNextRedeemTime(Instant.now().plus(cooldown));
    }

    public void checkCooldown() throws KitCooldownException {
        if (Instant.now().isBefore(nextRedeemTime))
            throw new KitCooldownException(nextRedeemTime);
    }

    @Override
    public int getRedeemCount() {
        return redeemCount;
    }

    public void increaseRedeemCount() {
        redeemCount++;
    }

    public void checkLimit(int redeemLimit) throws KitLimitException {
        if (redeemLimit >= 0 && redeemLimit <= redeemCount)
            throw new KitLimitException(redeemLimit);
    }

    public KitRecipientData memento() {
        return new KitRecipientData(uid, nextRedeemTime.toString(), redeemCount);
    }
}
