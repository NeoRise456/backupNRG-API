package com.cloudnrg.api.storage.application.internal.outboundservices.acl;

import com.cloudnrg.api.auditlog.interfaces.acl.AuditLogContextFacade;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExternalStorageAuditLogService {

    private final AuditLogContextFacade auditLogContextFacade;

    public ExternalStorageAuditLogService(AuditLogContextFacade auditLogContextFacade) {
        this.auditLogContextFacade = auditLogContextFacade;
    }

    public UUID createAuditLog(
            UUID userId,
            String action,
            String targetType,
            String targetId,
            String description
    ) {
        return auditLogContextFacade.createAuditLog(
                userId, action, targetType, targetId, description
        );
    }

}
