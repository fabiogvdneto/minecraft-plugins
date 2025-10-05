package com.github.fabiogvdneto.warps.repository;

import com.github.fabiogvdneto.common.repository.KeyedRepository;
import com.github.fabiogvdneto.warps.repository.data.UserData;

public interface UserRepository extends KeyedRepository<String, UserData> {

    int purge(int days) throws Exception;

}
