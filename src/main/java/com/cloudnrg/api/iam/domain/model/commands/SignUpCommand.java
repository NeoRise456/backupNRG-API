package com.cloudnrg.api.iam.domain.model.commands;


import com.cloudnrg.api.iam.domain.model.entities.Role;

import java.util.List;

public record SignUpCommand(String username, String email, String password, List<Role> roles) {
}
