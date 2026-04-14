package com.github.fabiogvdneto.kits.kit;

import com.github.fabiogvdneto.kits.repository.data.KitRecipientData;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.UUID;

public class KitRecipientImpl implements KitRecipient {

    private final KitImpl kit;
    private final UUID uid;

    private Instant nextRedeemTime = Instant.EPOCH;

    KitRecipientImpl(KitImpl kit, UUID uid) {
        this.kit = Objects.requireNonNull(kit);
        this.uid = Objects.requireNonNull(uid);
    }

    KitRecipientImpl(KitImpl kit, KitRecipientData data) throws DateTimeParseException {
        this.kit = Objects.requireNonNull(kit);
        this.uid = data.uid();
        this.nextRedeemTime = Instant.parse(data.nextRedeemTime());
    }

    @Override
    public UUID getUID() {
        return uid;
    }

    @Override
    public Instant getNextRedeemTime() {
        return nextRedeemTime;
    }

    @Override
    public void setNextRedeemTime(Instant nextRedeemTime) {
        this.nextRedeemTime = nextRedeemTime;
    }

    public KitRecipientData memento() {
        return new KitRecipientData(uid, nextRedeemTime.toString());
    }
}
