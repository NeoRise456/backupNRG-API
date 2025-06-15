package com.cloudnrg.api.history.interfaces.rest.resources;

import com.cloudnrg.api.history.domain.model.valueobjects.ObjectAction;

import java.util.UUID;

public record ObjectHistoryResource(UUID id, UUID fileId, UUID userId, ObjectAction action) {
}
