package com.github.fabiogvdneto.cursedwarps.repository;

import com.github.fabiogvdneto.common.repository.KeyedRepository;
import com.github.fabiogvdneto.cursedwarps.repository.data.UserData;

import java.util.UUID;

public interface UserRepository extends KeyedRepository<UUID, UserData> { }
