package com.github.fabiogvdneto.warps.user;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface UserManager {

    Collection<User> getAll();

    User getIfCached(UUID userId);

    CompletableFuture<User> fetch(UUID userId);

    default void fetch(UUID userId, Consumer<User> callback) {
        fetch(userId).thenAccept(callback);
    }
}
