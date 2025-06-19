package com.cloudnrg.api.storage.interfaces.rest.resources;

import java.util.UUID;

public record CreateFolderResource(
        String folderName,
        UUID parentFolderId
){}
