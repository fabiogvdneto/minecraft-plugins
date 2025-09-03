package com.github.fabiogvdneto.warps.repository.java;

import com.github.fabiogvdneto.common.repository.java.JavaSingleRepository;
import com.github.fabiogvdneto.warps.repository.WarpRepository;
import com.github.fabiogvdneto.warps.repository.data.WarpData;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

public class JavaWarpSingleRepository extends JavaSingleRepository<Collection<WarpData>> implements WarpRepository {
    public JavaWarpSingleRepository(Path path) {
        super(path);
    }

    @Override
    public Collection<WarpData> fetch() throws IOException {
        Collection<WarpData> data = super.fetch();
        return (data == null) ? Collections.emptyList() : data;
    }
}
