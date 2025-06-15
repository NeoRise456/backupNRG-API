package com.cloudnrg.api.history.domain.model.commands;

import com.cloudnrg.api.history.domain.model.valueobjects.ObjectAction;

import java.util.UUID;

public record CreateObjectHistoryCommand(UUID fileId, UUID userId, ObjectAction action, String message) {
}
