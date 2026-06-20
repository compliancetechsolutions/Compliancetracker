package com.compliance.common.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.NoRepositoryBean;

import jakarta.persistence.LockModeType;

@NoRepositoryBean

public interface BaseRepository<T, ID extends Serializable>

    extends

    JpaRepository<T, ID>,

    JpaSpecificationExecutor<T> {

// =====================================================
// BASIC
// =====================================================

  @Override
  Optional<T> findById(ID id);

  @Override
  boolean existsById(ID id);

// =====================================================
// BATCH
// =====================================================

  @Override
  <S extends T> List<S> saveAll(Iterable<S> entities);

  @Override
  void deleteAllInBatch();

// =====================================================
// LOCKING
// =====================================================

  @Lock(LockModeType.PESSIMISTIC_WRITE)

  Optional<T> findWithLockById(ID id);

// =====================================================
// UTIL
// =====================================================

  default T getRequired(

      ID id

  ) {

    return findById(id)

        .orElseThrow(

            () ->

            new IllegalArgumentException(

                "Record not found : "

                    + id

            )

        );

  }

}
