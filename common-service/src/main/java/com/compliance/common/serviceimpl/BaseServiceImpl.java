package com.compliance.common.serviceimpl;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.compliance.common.exception.BaseException;
import com.compliance.common.exception.ResourceNotFoundException;
import com.compliance.common.repository.BaseRepository;
import com.compliance.common.service.BaseService;
import com.compliance.entity.BaseEntity;
import com.compliance.enums.ErrorCode;

import jakarta.persistence.OptimisticLockException;

/**
 * Generic base service implementation.
 *
 * Supports:
 * - create
 * - update
 * - getById
 * - getAll
 * - soft delete
 *
 * Child services can override create/update
 * when mapping DTO -> Entity is needed.
 */
public abstract class BaseServiceImpl<
        CREATE,
        UPDATE,
        RESPONSE,
        ENTITY,
        ID extends Serializable>
implements BaseService<
        CREATE,
        UPDATE,
        RESPONSE,
        ID> {

    protected final BaseRepository<ENTITY, ID> repository;

    protected BaseServiceImpl(
            BaseRepository<ENTITY, ID> repository) {
        this.repository = repository;
    }

    @Override
    public abstract RESPONSE create(CREATE request);

    @Override
    public abstract RESPONSE update(
            ID id,
            UPDATE request);

    @Override
    public abstract Optional<RESPONSE> getById(ID id);

    @Override
    public abstract Page<RESPONSE> getAll(
            Pageable pageable);

    @Override
    public void delete(ID id) {

        ENTITY entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ErrorCode.ENTITY_NOT_FOUND,
                                id));

        try {

            if (entity instanceof BaseEntity base) {
                base.setIsDeleted(true);
                repository.save(entity);
            } else {
                repository.deleteById(id);
            }

        } catch (ObjectOptimisticLockingFailureException
                | OptimisticLockException e) {

            throw new BaseException(
                    ErrorCode.ENTITY_CONFLICT,
                    "This record was modified by another request. Please refresh and retry.");
        }
    }
}