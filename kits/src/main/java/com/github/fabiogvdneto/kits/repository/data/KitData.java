package com.github.fabiogvdneto.kits.repository.data;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

public record KitData(
        String name,
        long cooldownMinutes,
        long price,
        String contents,
        Map<UUID, String> recipients
) implements Serializable { }
