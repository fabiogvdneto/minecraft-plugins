package com.github.fabiogvdneto.kits.repository.data;

import java.io.Serializable;
import java.util.List;

public record KitData(
        String name,
        double price,
        long cooldownMinutes,
        int redeemLimit,
        String contents,
        List<KitRecipientData> recipients
) implements Serializable { }
