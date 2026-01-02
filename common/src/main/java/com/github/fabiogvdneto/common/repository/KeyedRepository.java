package com.github.fabiogvdneto.common.repository;

import java.util.Collection;

public interface KeyedRepository<K, V> extends EmptyRepository {

    Collection<V> fetchAll() throws Exception;

    V fetchOne(K key) throws Exception;

    void deleteOne(K key) throws Exception;

    void storeOne(K key, V data) throws Exception;

}
