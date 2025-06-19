package com.cloudnrg.api.iam.interfaces.rest.transform;

import com.cloudnrg.api.iam.domain.model.commands.SignUpCommand;
import com.cloudnrg.api.iam.domain.model.entities.Role;
import com.cloudnrg.api.iam.interfaces.rest.resources.SignUpResource;

import java.util.ArrayList;

public class SignUpCommandFromResourceAssembler {

  public static SignUpCommand toCommandFromResource(SignUpResource resource) {
    var roles = resource.roles() != null
        ? resource.roles().stream().map(name -> Role.toRoleFromName(name)).toList()
        : new ArrayList<Role>();
    return new SignUpCommand(resource.username(), resource.email(), resource.password(), roles);
  }
}
