package com.cloudnrg.api.auditlog.interfaces.acl;

import java.util.UUID;

public interface AuditLogContextFacade {
    UUID createAuditLog(
        UUID userId,
        String action,
        String target,
        String targetId,
        String description
    );
}
