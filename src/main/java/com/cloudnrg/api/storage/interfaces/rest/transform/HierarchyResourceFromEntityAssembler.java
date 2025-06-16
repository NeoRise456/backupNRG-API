package com.cloudnrg.api.storage.interfaces.rest.transform;

import com.cloudnrg.api.storage.domain.model.aggregates.Folder;
import com.cloudnrg.api.storage.interfaces.rest.resources.HierarchyResource;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HierarchyResourceFromEntityAssembler {
    // Pass a function that fetches children by parentId
    public static HierarchyResource toResourceFromEntity(
            Folder entity,
            Function<UUID, List<Folder>> childrenFetcher
    ) {
        List<Folder> childrenFolders = childrenFetcher.apply(entity.getId());
        List<HierarchyResource> childrenResources = childrenFolders.stream()
                .map(child -> toResourceFromEntity(child, childrenFetcher)) // Recursive call
                .collect(Collectors.toList()); // Use .collect(Collectors.toList()) for broader Java version compatibility

        // Instantiate HierarchyResource with 6 arguments
        // Convert Instant to Long (epoch seconds) for timestamps
        HierarchyResource resource = new HierarchyResource(
                entity.getId(),
                entity.getName(),
                entity.getParentFolder() != null ? entity.getParentFolder().getId() : null,
                entity.getUser().getId(),
                entity.getCreatedAt().getEpochSecond(),
                entity.getUpdatedAt().getEpochSecond()
        );

        // Set the children using a setter method
        // This assumes HierarchyResource has a method like: public void setChildren(List<HierarchyResource> children)
        resource.setChildren(childrenResources);

        return resource;
    }
}
