package com.github.fabiogvdneto.common.repository;

public interface SingleRepository<V> extends EmptyRepository {

    /**
     * Fetches data from the repository.
     */
    V fetch() throws Exception;

    /**
     * Stores data to the repository.
     */
    void store(V data) throws Exception;

}
