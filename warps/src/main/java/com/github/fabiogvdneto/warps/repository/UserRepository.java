package com.github.fabiogvdneto.warps.repository;

import com.github.fabiogvdneto.common.repository.KeyedRepository;
import com.github.fabiogvdneto.warps.repository.data.UserData;

import java.util.UUID;

public interface UserRepository extends KeyedRepository<UUID, UserData> {

    int purge(int days) throws Exception;

}
