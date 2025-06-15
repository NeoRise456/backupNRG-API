package com.cloudnrg.api.storage.application.internal.eventhandlers.file;

import com.cloudnrg.api.storage.application.internal.outboundservices.acl.ExternalStorageAuditLogService;
import com.cloudnrg.api.storage.application.internal.outboundservices.acl.ExternalObjectHistoryService;
import com.cloudnrg.api.storage.domain.model.events.UpdateFileParentFolderEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class FileParentFolderUpdate {

    private final ExternalObjectHistoryService externalObjectHistoryService;
    private final ExternalStorageAuditLogService externalStorageAuditLogService;

    public FileParentFolderUpdate(
            ExternalObjectHistoryService externalObjectHistoryService,
            ExternalStorageAuditLogService externalStorageAuditLogService
    ) {
        this.externalObjectHistoryService = externalObjectHistoryService;
        this.externalStorageAuditLogService = externalStorageAuditLogService;
    }

    @EventListener(UpdateFileParentFolderEvent.class)
    public void on(UpdateFileParentFolderEvent event) {
        // Create object history for the file parent folder update event
        externalObjectHistoryService.createObjectHistory(
                event.getFileId(),
                event.getUserId(),
                "UPDATE",
                "File parent folder updated from " + event.getOldParentFolderName() + " to " + event.getNewParentFolderName()
        );

        externalStorageAuditLogService.createAuditLog(
                event.getUserId(),
                "UPDATE",
                "FILE",
                event.getFileId().toString(),
                "File parent folder updated from " + event.getOldParentFolderName() + " to " + event.getNewParentFolderName()
        );
    }

}
