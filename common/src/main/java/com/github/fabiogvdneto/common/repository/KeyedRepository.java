package com.github.fabiogvdneto.common.repository;

import java.util.Collection;

public interface KeyedRepository<K, V> extends EmptyRepository {

    /**
     * Fetches all data from the repository.
     */
    Collection<V> fetchAll() throws Exception;

    /**
     * Fetches data from the repository.
     */
    V fetchOne(K key) throws Exception;

    /**
     * Deletes data from the repository.
     */
    void deleteOne(K key) throws Exception;

    /**
     * Stores data to the repository.
     */
    void storeOne(K key, V data) throws Exception;

    /**
     * Purges all data older than x days.
     */
    void purge(int purgeDays) throws Exception;

}
