package com.github.fabiogvdneto.kits.kit;

import java.time.Instant;
import java.util.UUID;

public interface KitRecipient {

    UUID getUID();

    Instant getNextRedeemTime();

    void setNextRedeemTime(Instant when);

}
