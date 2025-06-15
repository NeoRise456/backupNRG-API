package com.cloudnrg.api.auditlog.application.internal.commandservices;

import com.cloudnrg.api.auditlog.domain.model.aggregates.AuditLog;
import com.cloudnrg.api.auditlog.domain.model.commands.CreateAuditLogCommand;

import com.cloudnrg.api.auditlog.domain.services.AuditLogCommandService;
import com.cloudnrg.api.auditlog.infrastructure.persistence.jpa.repositories.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuditLogCommandServiceImpl implements AuditLogCommandService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogCommandServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public Optional<AuditLog> handle(CreateAuditLogCommand command) {
        var log = new AuditLog(
                command.userId(),
                command.action(),
                command.target(),
                command.targetId(),
                command.description()
        );

        try{
            auditLogRepository.save(log);
            return Optional.of(log);

        } catch (Exception e) {
            // Log the exception or handle it as needed
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
