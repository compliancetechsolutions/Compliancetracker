package com.compliance.common.service;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BaseService<
        CREATE,
        UPDATE,
        RESPONSE,
        ID extends Serializable> {

    RESPONSE create(CREATE request);

    RESPONSE update(ID id, UPDATE request);

    Optional<RESPONSE> getById(ID id);

    Page<RESPONSE> getAll(Pageable pageable);

    void delete(ID id);
}