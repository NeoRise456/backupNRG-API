package com.cloudnrg.api.history.application.acl;

import com.cloudnrg.api.history.domain.model.aggregates.ObjectHistory;
import com.cloudnrg.api.history.domain.model.commands.CreateObjectHistoryCommand;
import com.cloudnrg.api.history.domain.model.commands.DeleteObjectHistoriesByFileId;
import com.cloudnrg.api.history.domain.model.queries.GetAllObjectsHistoryByUserIdQuery;
import com.cloudnrg.api.history.domain.model.valueobjects.ObjectAction;
import com.cloudnrg.api.history.domain.services.ObjectHistoryCommandService;
import com.cloudnrg.api.history.domain.services.ObjectHistoryQueryService;
import com.cloudnrg.api.history.interfaces.acl.ObjectHistoryContextFacade;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ObjectHistoryContextFacadeImpl implements ObjectHistoryContextFacade {
    private final ObjectHistoryCommandService objectHistoryCommandService;
    private final ObjectHistoryQueryService objectHistoryQueryService;

    public ObjectHistoryContextFacadeImpl(
            ObjectHistoryCommandService objectHistoryCommandService,
            ObjectHistoryQueryService objectHistoryQueryService) {
        this.objectHistoryCommandService = objectHistoryCommandService;
        this.objectHistoryQueryService = objectHistoryQueryService;
    }

    @Override
    public UUID createObjectHistory(UUID fileId, UUID userId, String action, String message) {

        var history =  objectHistoryCommandService.handle(new CreateObjectHistoryCommand(
                fileId,
                userId,
                ObjectAction.valueOf(action.toUpperCase()),
                message
        ));

        return history.get().getId();
    }

    @Override
    public void deleteObjectHistoriesByFileId(UUID fileId) {
        objectHistoryCommandService.handle(new DeleteObjectHistoriesByFileId(fileId));
    }
}
