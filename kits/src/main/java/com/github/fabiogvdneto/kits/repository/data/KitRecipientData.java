package com.github.fabiogvdneto.kits.repository.data;

import java.io.Serializable;
import java.util.UUID;

public record KitRecipientData(
        UUID uid,
        String nextRedeemTime,
        int redeemCount
) implements Serializable { }
