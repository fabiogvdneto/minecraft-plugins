package com.github.fabiogvdneto.warps.repository.gson;

import com.github.fabiogvdneto.common.repository.gson.GsonKeyedRepository;
import com.github.fabiogvdneto.warps.repository.UserRepository;
import com.github.fabiogvdneto.warps.repository.data.UserData;
import com.google.gson.GsonBuilder;

import java.nio.file.Path;
import java.util.UUID;

public class GsonUserRepository extends GsonKeyedRepository<UUID, UserData> implements UserRepository {

    public GsonUserRepository(Path dir) {
        super(dir, new GsonBuilder().setPrettyPrinting().create(), UserData.class);
    }

}
