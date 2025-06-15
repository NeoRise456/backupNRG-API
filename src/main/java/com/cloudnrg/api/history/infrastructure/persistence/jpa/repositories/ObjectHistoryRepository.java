package com.cloudnrg.api.history.infrastructure.persistence.jpa.repositories;

import com.cloudnrg.api.history.domain.model.aggregates.ObjectHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ObjectHistoryRepository extends JpaRepository<ObjectHistory, UUID> {
    Optional<ObjectHistory> findById(UUID id);
    List<ObjectHistory> findAllByUser_Id(UUID userId);
    List<ObjectHistory> findAllByFile_Id(UUID fileId);

    void deleteObjectHistoriesByFile_Id(UUID fileId);
}
