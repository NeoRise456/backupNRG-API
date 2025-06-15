package com.cloudnrg.api.storage.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class DeleteFileEvent extends ApplicationEvent {
    private final UUID fileId;
    private final UUID userId;

    public DeleteFileEvent(Object source, UUID fileId, UUID userId) {
        super(source);
        this.fileId = fileId;
        this.userId = userId;
    }
}
