package com.cloudnrg.api.storage.application.internal.eventhandlers.file;

import com.cloudnrg.api.storage.application.internal.outboundservices.acl.ExternalStorageAuditLogService;
import com.cloudnrg.api.storage.application.internal.outboundservices.acl.ExternalObjectHistoryService;
import com.cloudnrg.api.storage.domain.model.events.UpdateFileNameEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class FileNameUpdateEventHandler {

    private final ExternalObjectHistoryService externalObjectHistoryService;
    private final ExternalStorageAuditLogService externalStorageAuditLogService;

    public FileNameUpdateEventHandler(
            ExternalObjectHistoryService externalObjectHistoryService,
            ExternalStorageAuditLogService externalStorageAuditLogService
    ) {
        this.externalObjectHistoryService = externalObjectHistoryService;
        this.externalStorageAuditLogService = externalStorageAuditLogService;
    }

    @EventListener(UpdateFileNameEvent.class)
    public void on(UpdateFileNameEvent event) {
        // Create object history for the file name update event
        externalObjectHistoryService.createObjectHistory(
                event.getFileId(),
                event.getUserId(),
                "UPDATE",
                "File name updated from " + event.getOldFileName() + " to " + event.getNewFileName()
        );

        externalStorageAuditLogService.createAuditLog(
                event.getUserId(),
                "UPDATE",
                "FILE",
                event.getFileId().toString(),
                "File name updated from " + event.getOldFileName() + " to " + event.getNewFileName()
        );
    }

}
