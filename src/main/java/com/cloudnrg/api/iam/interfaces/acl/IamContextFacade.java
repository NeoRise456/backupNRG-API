package com.cloudnrg.api.iam.interfaces.acl;

import com.cloudnrg.api.iam.domain.model.aggregates.User;

import java.util.Optional;
import java.util.UUID;

public interface IamContextFacade {
    Optional<User> fetchUserById(UUID userId);
    UUID fetchUserIdByUsername(String username);
}
