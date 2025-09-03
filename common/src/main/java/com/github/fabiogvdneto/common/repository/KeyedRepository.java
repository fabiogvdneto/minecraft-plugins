package com.github.fabiogvdneto.common.repository;

import java.util.Collection;

public interface KeyedRepository<K, V> extends Repository {

    void storeOne(V data) throws Exception;

    V fetchOne(K key) throws Exception;

    void deleteOne(K key) throws Exception;

    Collection<V> fetchAll() throws Exception;

    Collection<K> fetchKeys() throws Exception;

}
