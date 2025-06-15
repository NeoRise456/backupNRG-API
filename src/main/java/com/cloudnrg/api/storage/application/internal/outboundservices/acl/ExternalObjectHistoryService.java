package com.cloudnrg.api.storage.application.internal.outboundservices.acl;

import com.cloudnrg.api.history.interfaces.acl.ObjectHistoryContextFacade;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExternalObjectHistoryService {

    private final ObjectHistoryContextFacade objectHistoryContextFacade;

    public ExternalObjectHistoryService(ObjectHistoryContextFacade objectHistoryContextFacade) {
        this.objectHistoryContextFacade = objectHistoryContextFacade;
    }

    public UUID createObjectHistory(
            UUID fileId,
            UUID userId,
            String action,
            String message
    ) {
        return objectHistoryContextFacade.createObjectHistory(fileId, userId, action, message);
    }

    public void deleteObjectHistoriesByFileId(UUID fileId) {
        objectHistoryContextFacade.deleteObjectHistoriesByFileId(fileId);
    }

}
