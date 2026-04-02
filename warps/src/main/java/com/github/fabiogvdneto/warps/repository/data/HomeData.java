package com.github.fabiogvdneto.warps.repository.data;

import java.io.Serializable;

public record HomeData(
        String name,
        LocationData location,
        boolean closed
) implements Serializable { }
