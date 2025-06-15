package com.cloudnrg.api.history.interfaces.rest.transform;

import com.cloudnrg.api.history.domain.model.commands.CreateObjectHistoryCommand;
import com.cloudnrg.api.history.interfaces.rest.resources.CreateObjectHistoryResource;

public class CreateObjectHistoryCommandFromResourceAssembler {
    public static CreateObjectHistoryCommand toCommandFromResource(CreateObjectHistoryResource resource) {
        return new CreateObjectHistoryCommand(
                resource.fileId(),
                resource.userId(),
                resource.action(),
                resource.message()
        );
    }
}
