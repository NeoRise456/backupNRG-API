package com.cloudnrg.api.storage.interfaces.rest.transform;

import com.cloudnrg.api.storage.domain.model.commands.CreateFolderCommand;
import com.cloudnrg.api.storage.interfaces.rest.resources.CreateFolderResource;

import java.util.UUID;

public class CreateFolderCommandFromResourceAssembler {
    public static CreateFolderCommand toCommandFromResource(UUID userId, CreateFolderResource resource) {
        return new CreateFolderCommand(
                userId,
                resource.folderName(),
                resource.parentFolderId()
        );
    }
}
