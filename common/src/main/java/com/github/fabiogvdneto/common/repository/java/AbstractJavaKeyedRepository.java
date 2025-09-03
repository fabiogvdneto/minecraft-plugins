package com.github.fabiogvdneto.common.repository.java;

import com.github.fabiogvdneto.common.repository.KeyedRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class AbstractJavaKeyedRepository<K, V> implements KeyedRepository<K, V> {

    private final Path dir;

    public AbstractJavaKeyedRepository(Path dir) {
        this.dir = Objects.requireNonNull(dir);
    }

    protected abstract K getKey(V data);

    protected abstract K getKeyFromString(String id);

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

    @Override
    public void storeOne(V data) throws IOException {
        select(getKey(data)).store(data);
    }

    @Override
    public V fetchOne(K key) throws IOException {
        return select(key).fetch();
    }

    @Override
    public void deleteOne(K key) throws IOException {
        select(key).delete();
    }

    private JavaSingleRepository<V> select(K key) {
        return new JavaSingleRepository<>(dir.resolve(key + ".ser"));
    }

    @Override
    public Collection<K> fetchKeys() throws IOException {
        try (Stream<Path> files = Files.find(dir, 1, this::filter)) {
            return files.map(this::getKeyFromPath).filter(Objects::nonNull).toList();
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

    private boolean filter(Path path, BasicFileAttributes attributes) {
        return attributes.isRegularFile() && path.getFileName().toString().endsWith(".ser");
    }

    private K getKeyFromPath(Path path) {
        String filename;

        filename = path.getFileName().toString();
        filename = filename.substring(0, filename.length() - 4);

        return getKeyFromString(filename);
    }
}
