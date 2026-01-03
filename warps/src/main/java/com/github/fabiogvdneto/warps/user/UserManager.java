package com.github.fabiogvdneto.warps.user;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserManager {

    Collection<User> getAll();

    User get(UUID userId);

    CompletableFuture<User> fetch(UUID userId);

}
