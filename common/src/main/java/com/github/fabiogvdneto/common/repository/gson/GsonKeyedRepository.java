package com.github.fabiogvdneto.common.repository.gson;

import com.github.fabiogvdneto.common.repository.file.FileKeyedRepository;
import com.github.fabiogvdneto.common.repository.file.FileSingleRepository;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.nio.file.Path;

public class GsonKeyedRepository<K, V> extends FileKeyedRepository<K, V> {

    protected final Gson gson;
    protected final Type type;

    public GsonKeyedRepository(Path dir, Gson gson, Type type) {
        super(dir, ".json");
        this.gson = gson;
        this.type = type;
    }

    @Override
    public FileSingleRepository<V> select(Path path) {
        return new GsonSingleRepository<>(path, gson, type);
    }
}
