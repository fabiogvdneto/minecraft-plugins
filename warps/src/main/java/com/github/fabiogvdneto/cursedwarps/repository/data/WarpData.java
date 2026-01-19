package com.github.fabiogvdneto.cursedwarps.repository.data;

import java.io.Serializable;

public record WarpData(
        String name,
        LocationData location,
        boolean closed
) implements Serializable { }
