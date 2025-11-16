package com.github.fabiogvdneto.warps.repository.gson;

import com.github.fabiogvdneto.common.repository.gson.GsonSingleRepository;
import com.github.fabiogvdneto.warps.repository.WarpRepository;
import com.github.fabiogvdneto.warps.repository.data.WarpData;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

public class GsonWarpRepository extends GsonSingleRepository<Collection<WarpData>> implements WarpRepository {

    public GsonWarpRepository(Path path) {
        super(path, new GsonBuilder().setPrettyPrinting().create(), new TypeToken<Collection<WarpData>>() { }.getType());
    }

    @Override
    public Collection<WarpData> fetch() throws IOException {
        Collection<WarpData> data = super.fetch();
        return (data == null) ? Collections.emptyList() : data;
    }
}
