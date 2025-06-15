package com.cloudnrg.api.history.domain.services;

import com.cloudnrg.api.history.domain.model.aggregates.ObjectHistory;
import com.cloudnrg.api.history.domain.model.commands.CreateObjectHistoryCommand;
import com.cloudnrg.api.history.domain.model.commands.DeleteObjectHistoriesByFileId;

import java.util.Optional;

public interface ObjectHistoryCommandService {
    Optional<ObjectHistory> handle(CreateObjectHistoryCommand command);
    void handle(DeleteObjectHistoriesByFileId command);
}
