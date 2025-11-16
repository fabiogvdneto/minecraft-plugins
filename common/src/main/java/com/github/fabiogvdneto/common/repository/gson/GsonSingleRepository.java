package com.github.fabiogvdneto.common.repository.gson;

import com.github.fabiogvdneto.common.repository.file.FileSingleRepository;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

public class GsonSingleRepository<V> extends FileSingleRepository<V> {

    protected final Gson gson;
    protected final Type type;

    public GsonSingleRepository(Path file, Gson gson, Type type) {
        super(file);
        this.gson = gson;
        this.type = type;
    }

    @Override
    public V fetch() throws IOException {
        if (!Files.exists(file)) {
            return null;
        }

        try (Reader reader = Files.newBufferedReader(file)) {
            return gson.fromJson(reader, type);
        }
    }

    @Override
    public void store(V data) throws IOException {
        try (Writer writer = Files.newBufferedWriter(file)) {
            gson.toJson(data, type, writer);
        }
    }
}
