package com.cloudnrg.api.iam.domain.services;

import com.cloudnrg.api.iam.domain.model.commands.SeedRolesCommand;

public interface RoleCommandService {
  void handle(SeedRolesCommand command);
}
