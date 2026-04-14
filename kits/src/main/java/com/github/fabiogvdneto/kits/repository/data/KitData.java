package com.github.fabiogvdneto.kits.repository.data;

import java.io.Serializable;
import java.util.List;

public record KitData(
        String id,
        long cooldownMinutes,
        long price,
        String contents,
        List<KitRecipientData> recipients
) implements Serializable { }
