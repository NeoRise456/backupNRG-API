package com.cloudnrg.api.storage.domain.services;

import com.cloudnrg.api.storage.domain.model.aggregates.Folder;
import com.cloudnrg.api.storage.domain.model.queries.GetFolderHierarchyByIdQuery;
import com.cloudnrg.api.storage.domain.model.queries.GetRootFolderByUserIdQuery;

import java.util.List;
import java.util.Optional;

public interface FolderQueryService {
    Optional<Folder> handle(GetRootFolderByUserIdQuery query);
    List<Folder> handle(GetFolderHierarchyByIdQuery query);
}
