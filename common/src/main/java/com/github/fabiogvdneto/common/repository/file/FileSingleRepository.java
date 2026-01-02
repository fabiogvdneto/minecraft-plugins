package com.github.fabiogvdneto.common.repository.file;

import com.github.fabiogvdneto.common.repository.SingleRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public abstract class FileSingleRepository<V> implements SingleRepository<V> {

    protected final Path file;

    public FileSingleRepository(Path file) {
        this.file = Objects.requireNonNull(file);
    }

    @Override
    public void create() throws Exception {
        Files.createDirectories(file.getParent());
    }

    @Override
    public void delete() throws Exception {
        Files.deleteIfExists(file);
    }
}
