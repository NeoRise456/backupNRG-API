package com.cloudnrg.api.auditlog.domain.services;


import com.cloudnrg.api.auditlog.domain.model.aggregates.AuditLog;
import com.cloudnrg.api.auditlog.domain.model.commands.CreateAuditLogCommand;

import java.util.Optional;


public interface AuditLogCommandService {
    Optional<AuditLog> handle(CreateAuditLogCommand command);
}
