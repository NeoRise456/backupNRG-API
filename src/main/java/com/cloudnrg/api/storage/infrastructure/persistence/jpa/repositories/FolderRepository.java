package com.cloudnrg.api.storage.infrastructure.persistence.jpa.repositories;

import com.cloudnrg.api.storage.domain.model.aggregates.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FolderRepository extends JpaRepository<Folder, UUID> {

    Optional<Folder> findFolderById(UUID id);

    Optional<Folder> findFolderByUser_IdAndName(UUID userId, String name);

    Boolean existsFolderByNameAndUser_Id(String name, UUID userId);
}
