package com.cloudnrg.api.storage.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class DeleteFolderEvent extends ApplicationEvent {
    private UUID folderId;
    private UUID userId;

    public DeleteFolderEvent(Object source, UUID folderId, UUID userId) {
        super(source);
        this.folderId = folderId;
        this.userId = userId;
    }
}
