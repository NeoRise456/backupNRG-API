package com.cloudnrg.api.storage.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class UpdateFileParentFolderEvent extends ApplicationEvent {
    private UUID fileId;
    private UUID userId;
    private String oldParentFolderName;
    private String newParentFolderName;

    public UpdateFileParentFolderEvent(
            Object source,
            UUID fileId,
            UUID userId,
            String oldParentFolderName,
            String newParentFolderName) {
        super(source);
        this.fileId = fileId;
        this.userId = userId;
        this.oldParentFolderName = oldParentFolderName;
        this.newParentFolderName = newParentFolderName;
    }
}
