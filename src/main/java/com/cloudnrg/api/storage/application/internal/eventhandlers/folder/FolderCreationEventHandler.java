package com.cloudnrg.api.storage.application.internal.eventhandlers.folder;

import com.cloudnrg.api.storage.application.internal.outboundservices.acl.ExternalStorageAuditLogService;
import com.cloudnrg.api.storage.domain.model.events.CreateFolderEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class FolderCreationEventHandler {

    private final ExternalStorageAuditLogService externalStorageAuditLogService;

    public FolderCreationEventHandler(ExternalStorageAuditLogService externalStorageAuditLogService) {
        this.externalStorageAuditLogService = externalStorageAuditLogService;
    }

    @EventListener(CreateFolderEvent.class)
    private void on(CreateFolderEvent event) {
        // Create audit log for the folder creation event
        externalStorageAuditLogService.createAuditLog(
                event.getUserId(),
                "CREATE",
                "FOLDER",
                event.getFolderId().toString(),
                "Folder created with ID: " + event.getFolderId()
        );
    }

}
