package com.cloudnrg.api.iam.application.internal.outboundservices.acl;

import com.cloudnrg.api.auditlog.interfaces.acl.AuditLogContextFacade;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExternalAuditLogService {

    private final AuditLogContextFacade auditLogContextFacade;

    public ExternalAuditLogService(AuditLogContextFacade auditLogContextFacade) {
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
