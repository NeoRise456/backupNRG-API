package com.cloudnrg.api.auditlog.interfaces.rest;

import com.cloudnrg.api.auditlog.domain.model.queries.GetAuditLogsByFiltersQuery;
import com.cloudnrg.api.auditlog.domain.model.queries.GetAuditLogsByUserQuery;
import com.cloudnrg.api.auditlog.domain.model.valueobjects.AuditAction;
import com.cloudnrg.api.auditlog.domain.model.valueobjects.AuditTargetType;
import com.cloudnrg.api.auditlog.domain.services.AuditLogQueryService;

import com.cloudnrg.api.auditlog.interfaces.rest.resources.AuditLogResource;
import com.cloudnrg.api.auditlog.interfaces.rest.transform.AuditLogResourceAssembler;
import com.cloudnrg.api.shared.application.external.outboundedservices.ExternalIamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/auditlogs", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Audit Log Controller", description = "Audit Log Management Endpoints")
public class AuditLogController {

    private final AuditLogQueryService auditLogQueryService;
    private final ExternalIamService externalIamService;

    public AuditLogController(AuditLogQueryService auditLogQueryService, ExternalIamService externalIamService) {
        this.auditLogQueryService = auditLogQueryService;
        this.externalIamService = externalIamService;
    }

    @Operation(summary = "Get audit logs by user ID", description = "Retrieve audit logs for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logs retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Logs not found")
    })
    @GetMapping("/user")
    public ResponseEntity<List<AuditLogResource>> getLogsByUser(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        var user = externalIamService.getUserByUsername(username);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var userId = user.get().getId();
        var query = new GetAuditLogsByUserQuery(userId);
        var logs = auditLogQueryService.handle(query);
        var resources = logs.stream()
                .map(AuditLogResourceAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }


    @Operation(summary = "Filter audit logs", description = "Retrieve logs by optional filters")
    @GetMapping("/filter")
    public ResponseEntity<List<AuditLogResource>> getLogsByFilters(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) AuditAction action,
            @RequestParam(required = false) AuditTargetType targetType
    ) {
        String username = userDetails.getUsername();

        var user = externalIamService.getUserByUsername(username);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var userId = user.get().getId();

        var query = new GetAuditLogsByFiltersQuery(
                Optional.ofNullable(userId),
                Optional.ofNullable(action),
                Optional.ofNullable(targetType)
        );

        var logs = auditLogQueryService.handle(query);
        var resources = logs.stream()
                .map(AuditLogResourceAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }

}
