package com.github.fabiogvdneto.common.repository.file;

import com.github.fabiogvdneto.common.repository.KeyedRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class FileKeyedRepository<K, V> implements KeyedRepository<K, V> {

    protected final String ext;
    protected final Path dir;

    public FileKeyedRepository(Path dir, String ext) {
        this.dir = Objects.requireNonNull(dir);
        this.ext = Objects.requireNonNull(ext);
    }

    @Override
    public void create() throws IOException {
        Files.createDirectories(dir);
    }

    @Override
    public void delete() throws IOException {
        try (Stream<Path> stream = Files.walk(dir, 1)) {
            stream.forEach(this::delete);
        }
    }

    @Override
    public void purge(int purgeDays) throws IOException {
        final Instant limit = Instant.now().minus(purgeDays, ChronoUnit.DAYS);

        final Stream<Path> stream = Files.find(dir, 1, (path, attr) ->
                attr.isRegularFile() && path.endsWith(ext) && attr.lastAccessTime().toInstant().isBefore(limit)
        );

        stream.forEach(this::delete);
        stream.close();
    }

    @Override
    public Collection<V> fetchAll() throws IOException {
        try (Stream<Path> files = Files.list(dir)) {
            return files.filter(Files::isRegularFile).map(file -> {
                try {
                    return select(file).fetch();
                } catch (Exception e) {
                    return null;
                }
            }).filter(Objects::nonNull).toList();
        }
    }

    @Override
    public V fetchOne(K key) throws IOException {
        return select(key).fetch();
    }

    @Override
    public void deleteOne(K key) throws IOException {
        select(key).delete();
    }

    @Override
    public void storeOne(K key, V data) throws IOException {
        if (data == null) {
            select(key).delete();
        } else {
            select(key).store(data);
        }
    }

    public FileSingleRepository<V> select(K key) {
        return select(dir.resolve(key + ext));
    }

    public void delete(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            // Nothing we can do to help here.
        }
    }

    public abstract FileSingleRepository<V> select(Path path);
}
