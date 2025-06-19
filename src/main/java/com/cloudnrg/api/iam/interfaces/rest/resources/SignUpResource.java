package com.cloudnrg.api.iam.interfaces.rest.resources;

import java.util.List;

public record SignUpResource(String username, String email, String password, List<String> roles) {
}

