package com.cloudnrg.api.storage.application.internal.eventhandlers.file;

import com.cloudnrg.api.storage.application.internal.outboundservices.acl.ExternalStorageAuditLogService;
import com.cloudnrg.api.storage.application.internal.outboundservices.acl.ExternalObjectHistoryService;
import com.cloudnrg.api.storage.domain.model.events.CreateFileEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class FileCreationEventHandler {

    private final ExternalObjectHistoryService externalObjectHistoryService;
    private final ExternalStorageAuditLogService externalStorageAuditLogService;

    public FileCreationEventHandler(
            ExternalObjectHistoryService externalObjectHistoryService,
            ExternalStorageAuditLogService externalStorageAuditLogService
    ) {
        this.externalObjectHistoryService = externalObjectHistoryService;
        this.externalStorageAuditLogService = externalStorageAuditLogService;
    }

    @EventListener(CreateFileEvent.class)
    public void on(CreateFileEvent event) {
        // Create object history for the file creation event
        externalObjectHistoryService.createObjectHistory(
                event.getFileId(),
                event.getUserId(),
                "CREATE",
                "File created"
        );

        externalStorageAuditLogService.createAuditLog(
                event.getUserId(),
                "CREATE",
                "FILE",
                event.getFileId().toString(),
                "File created with ID: " + event.getFileId()
        );
    }

}
