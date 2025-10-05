package com.github.fabiogvdneto.common.repository;

import java.util.Collection;

public interface KeyedRepository<K, V> extends Repository {

    Collection<K> fetchKeys() throws Exception;

    Collection<V> fetchAll() throws Exception;

    V fetchOne(K key) throws Exception;

    void storeOne(V data) throws Exception;

    void deleteOne(K key) throws Exception;

}
