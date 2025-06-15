package com.cloudnrg.api.iam.application.internal.eventhandlers;

import com.cloudnrg.api.iam.application.internal.outboundservices.acl.ExternalAuditLogService;
import com.cloudnrg.api.iam.application.internal.outboundservices.acl.ExternalFolderService;
import com.cloudnrg.api.iam.domain.model.events.UserCreationEvent;
import com.cloudnrg.api.iam.domain.model.queries.GetUserByIdQuery;
import com.cloudnrg.api.iam.domain.services.UserQueryService;
import com.cloudnrg.api.storage.domain.model.aggregates.Folder;
import com.cloudnrg.api.storage.infrastructure.persistence.jpa.repositories.FolderRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class UserCreationEventHandler {

    private final UserQueryService userQueryService;
    private final ExternalFolderService externalFolderService;
    private final ExternalAuditLogService externalAuditLogService;

    public UserCreationEventHandler(UserQueryService userQueryService,
                                    ExternalFolderService externalFolderService,
                                    ExternalAuditLogService externalAuditLogService) {
        this.userQueryService = userQueryService;
        this.externalFolderService = externalFolderService;
        this.externalAuditLogService = externalAuditLogService;
    }

    //on user creation create a root folder for the user
    @EventListener(UserCreationEvent.class)
    public void on(UserCreationEvent event) {

        var user = userQueryService.handle(new GetUserByIdQuery(event.getUserId()));

        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        //user creation log

        var logUser = externalAuditLogService.createAuditLog(
                user.get().getId(),
                "CREATE",
                "USER",
                user.get().getId().toString(),
                "user " + user.get().getUsername() + " created"
        );

        //create root folder for user
        var rootFolderId = externalFolderService.createRootFolderForUser(user.get().getId());



    }

}
