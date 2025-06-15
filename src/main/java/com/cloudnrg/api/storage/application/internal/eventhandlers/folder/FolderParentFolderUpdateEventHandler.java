package com.cloudnrg.api.storage.application.internal.eventhandlers.folder;

import com.cloudnrg.api.storage.application.internal.outboundservices.acl.ExternalStorageAuditLogService;
import com.cloudnrg.api.storage.domain.model.events.UpdateFolderParentFolderEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class FolderParentFolderUpdateEventHandler {

    private final ExternalStorageAuditLogService externalStorageAuditLogService;

    public FolderParentFolderUpdateEventHandler(ExternalStorageAuditLogService externalStorageAuditLogService) {
        this.externalStorageAuditLogService = externalStorageAuditLogService;
    }

    @EventListener(UpdateFolderParentFolderEvent.class)
    public void on(UpdateFolderParentFolderEvent event) {
        // Create audit log for the folder parent folder update event
        externalStorageAuditLogService.createAuditLog(
                event.getUserId(),
                "UPDATE",
                "FOLDER",
                event.getFolderId().toString(),
                "Folder parent updated to: " + event.getNewParentFolderId()
        );
    }


}
