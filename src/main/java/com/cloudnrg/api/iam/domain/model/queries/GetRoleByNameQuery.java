package com.cloudnrg.api.iam.domain.model.queries;

import com.cloudnrg.api.iam.domain.model.valueobjects.Roles;

public record GetRoleByNameQuery(Roles name) {
}