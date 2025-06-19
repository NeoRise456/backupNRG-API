package com.cloudnrg.api.iam.interfaces.rest.resources;

import java.util.List;
import java.util.UUID;

public record UserResource(
        UUID id,
        String username,
        String email,
        List<String> roles
) {
}
