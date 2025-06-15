package com.cloudnrg.api.auditlog.application.acl;

import com.cloudnrg.api.auditlog.domain.model.commands.CreateAuditLogCommand;
import com.cloudnrg.api.auditlog.domain.model.valueobjects.AuditAction;
import com.cloudnrg.api.auditlog.domain.model.valueobjects.AuditTargetType;
import com.cloudnrg.api.auditlog.domain.services.AuditLogCommandService;
import com.cloudnrg.api.auditlog.domain.services.AuditLogQueryService;
import com.cloudnrg.api.auditlog.interfaces.acl.AuditLogContextFacade;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuditLogContextFacadeImpl implements AuditLogContextFacade {

    private final AuditLogQueryService auditLogQueryService;
    private final AuditLogCommandService auditLogCommandService;

    public AuditLogContextFacadeImpl(
        AuditLogQueryService auditLogQueryService,
        AuditLogCommandService auditLogCommandService
    ) {
        this.auditLogQueryService = auditLogQueryService;
        this.auditLogCommandService = auditLogCommandService;
    }

    @Override
    public UUID createAuditLog(
            UUID userId,
            String action,
            String target,
            String targetId,
            String description
    ) {

        var log = auditLogCommandService.handle(
                new CreateAuditLogCommand(
                        userId,
                        AuditAction.valueOf(action),
                        AuditTargetType.valueOf(target),
                        targetId,
                        description
                )
        );

        if (log.isEmpty()) {
            throw new RuntimeException("Failed to create audit log");
        }

        return log.get().getId();
    }
}
