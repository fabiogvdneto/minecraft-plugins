package com.github.fabiogvdneto.common.repository;

public interface EmptyRepository {

    /**
     * Creates an empty repository.
     */
    void create() throws Exception;

    /**
     * Deletes the repository.
     */
    void delete() throws Exception;

}
