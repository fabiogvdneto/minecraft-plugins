package com.github.fabiogvdneto.common.repository.java;

import com.github.fabiogvdneto.common.repository.KeyedRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class JavaKeyedRepository<V> implements KeyedRepository<String, V> {

    private final static String EXT = ".ser";

    private final Path dir;

    public JavaKeyedRepository(Path dir) {
        this.dir = Objects.requireNonNull(dir);
    }

    protected abstract String getKey(V data);

    @Override
    public void create() throws IOException {
        Files.createDirectories(dir);
    }

    @Override
    public void delete() throws IOException {
        try (Stream<Path> stream = Files.walk(dir)) {
            stream.forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    // Nothing we can do to help here.
                }
            });
        }
    }

    private boolean filter(Path path, BasicFileAttributes attributes) {
        return attributes.isRegularFile() && path.getFileName().toString().endsWith(EXT);
    }

    @Override
    public Collection<String> fetchKeys() throws IOException {
        try (Stream<Path> files = Files.find(dir, 1, this::filter)) {
            return files.map(path -> {
                        String filename = path.getFileName().toString();
                        return filename.substring(0, filename.length() - EXT.length());
                    }).toList();
        }
    }

    @Override
    public Collection<V> fetchAll() throws IOException {
        try (Stream<Path> files = Files.find(dir, 1, this::filter)) {
            return files.map(file -> {
                try {
                    return new JavaSingleRepository<V>(file).fetch();
                } catch (Exception e) {
                    return null;
                }
            }).filter(Objects::nonNull).toList();
        }
    }

    private JavaSingleRepository<V> select(String key) {
        return new JavaSingleRepository<>(dir.resolve(key + EXT));
    }

    @Override
    public V fetchOne(String key) throws IOException {
        return select(key).fetch();
    }

    @Override
    public void storeOne(V data) throws IOException {
        select(getKey(data)).store(data);
    }

    @Override
    public void deleteOne(String key) throws IOException {
        select(key).delete();
    }
}
