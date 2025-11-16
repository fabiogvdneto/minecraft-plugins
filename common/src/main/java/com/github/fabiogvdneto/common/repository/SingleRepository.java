package com.github.fabiogvdneto.common.repository;

public interface SingleRepository<V> extends EmptyRepository {

    V fetch() throws Exception;

    void store(V data) throws Exception;

}
