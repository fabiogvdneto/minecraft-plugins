package com.github.fabiogvdneto.kits.repository.java;

import com.github.fabiogvdneto.common.repository.java.AbstractJavaKeyedRepository;
import com.github.fabiogvdneto.kits.repository.KitRepository;
import com.github.fabiogvdneto.kits.repository.data.KitData;

import java.nio.file.Path;

public class JavaKitRepository extends AbstractJavaKeyedRepository<String, KitData> implements KitRepository {

    public JavaKitRepository(Path directory) {
        super(directory);
    }

    @Override
    protected String getKey(KitData data) {
        return data.name().toLowerCase();
    }

    @Override
    public String getKeyFromString(String id) {
        return id;
    }
}
