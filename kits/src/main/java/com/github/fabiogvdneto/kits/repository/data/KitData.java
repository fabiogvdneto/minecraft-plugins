package com.github.fabiogvdneto.kits.repository.data;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record KitData(
        String name,
        Duration cooldown,
        long price,
        byte[] contents,
        Map<UUID, Instant> availability
) implements Serializable { }
