package com.cloudnrg.api.storage.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class UpdateFileNameEvent extends ApplicationEvent {
    private UUID fileId;
    private String newFileName;
    private String oldFileName;
    private UUID userId;


    public UpdateFileNameEvent(
            Object source,
            UUID fileId,
            UUID userId,
            String newFileName,
            String oldFileName
            ) {
        super(source);
        this.fileId = fileId;
        this.newFileName = newFileName;
        this.oldFileName = oldFileName;
        this.userId = userId;


    }
}
