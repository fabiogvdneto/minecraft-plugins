package com.github.fabiogvdneto.kits.repository;

import com.github.fabiogvdneto.common.repository.gson.GsonKeyedRepository;
import com.github.fabiogvdneto.kits.repository.data.KitData;
import com.google.gson.GsonBuilder;

import java.nio.file.Path;

public class GsonKitRepository extends GsonKeyedRepository<KitData> implements KitRepository {

    public GsonKitRepository(Path directory) {
        super(directory, new GsonBuilder().setPrettyPrinting().create(), KitData.class);
    }

}
