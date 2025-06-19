package com.cloudnrg.api.iam.domain.services;

import com.cloudnrg.api.iam.domain.model.aggregates.User;
import com.cloudnrg.api.iam.domain.model.queries.GetUserByIdQuery;
import com.cloudnrg.api.iam.domain.model.queries.GetUserByUsernameQuery;

import java.util.Optional;

public interface UserQueryService {
    Optional<User> handle(GetUserByIdQuery query);
    Optional<User> handle(GetUserByUsernameQuery query);
}
