package com.github.fabiogvdneto.cursedwarps.user;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserService {

    Collection<User> getAll();

    User get(UUID userId);

    CompletableFuture<User> fetch(UUID userId);

}
