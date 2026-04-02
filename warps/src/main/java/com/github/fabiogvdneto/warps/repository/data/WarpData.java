package com.github.fabiogvdneto.warps.repository.data;

import java.io.Serializable;

public record WarpData(
        String name,
        LocationData location,
        boolean closed
) implements Serializable { }
