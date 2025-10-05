package com.github.fabiogvdneto.common.repository.java;

import com.github.fabiogvdneto.common.repository.SingleRepository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class JavaSingleRepository<V> implements SingleRepository<V> {

    private final Path file;

    public JavaSingleRepository(Path file) {
        this.file = Objects.requireNonNull(file);
    }

    @Override
    public void create() throws IOException {
        Files.createDirectories(file.getParent());
    }

    @Override
    public void delete() throws IOException {
        Files.deleteIfExists(file);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V fetch() throws IOException {
        if (Files.isRegularFile(file)) {
            try (ObjectInputStream stream = new ObjectInputStream(Files.newInputStream(file))) {
                return (V) stream.readObject();
            } catch (ClassNotFoundException e) {
                // This should never happen...
            }
        }
        return null;
    }

    @Override
    public void store(V data) throws IOException {
        try (ObjectOutputStream stream = new ObjectOutputStream(Files.newOutputStream(file))) {
            stream.writeObject(data);
        }
    }

    @Override
    public String toString() {
        return "type:java-object-serialization path:%s".formatted(file);
    }
}
