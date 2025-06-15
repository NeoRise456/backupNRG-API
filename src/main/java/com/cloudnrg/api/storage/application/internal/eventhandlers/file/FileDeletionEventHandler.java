package com.cloudnrg.api.storage.application.internal.eventhandlers.file;

import com.cloudnrg.api.storage.application.internal.outboundservices.acl.ExternalStorageAuditLogService;
import com.cloudnrg.api.storage.application.internal.outboundservices.acl.ExternalObjectHistoryService;
import com.cloudnrg.api.storage.domain.model.events.DeleteFileEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class FileDeletionEventHandler {
    private final ExternalObjectHistoryService externalObjectHistoryService;
    private final ExternalStorageAuditLogService externalStorageAuditLogService;

    public FileDeletionEventHandler(
            ExternalObjectHistoryService externalObjectHistoryService,
            ExternalStorageAuditLogService externalStorageAuditLogService
    ) {
        this.externalObjectHistoryService = externalObjectHistoryService;
        this.externalStorageAuditLogService = externalStorageAuditLogService;
    }

    @EventListener(DeleteFileEvent.class)
    public void on(DeleteFileEvent event) {
        //delete object history for the file deletion event
        externalObjectHistoryService.deleteObjectHistoriesByFileId(event.getFileId());

        externalStorageAuditLogService.createAuditLog(
                event.getUserId(),
                "DELETE",
                "FILE",
                event.getFileId().toString(),
                "File deleted with ID: " + event.getFileId()
        );

    }

}
