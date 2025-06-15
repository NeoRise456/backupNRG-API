package com.cloudnrg.api.history.domain.model.commands;

import java.util.UUID;

public record DeleteObjectHistoriesByFileId(UUID fileId) {
}
