package com.github.fabiogvdneto.cursedwarps.repository.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

public record UserData(
        UUID uid,
        Collection<HomeData> homes
) implements Serializable { }
