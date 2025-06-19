package com.cloudnrg.api.iam.domain.services;

import com.cloudnrg.api.iam.domain.model.aggregates.User;
import com.cloudnrg.api.iam.domain.model.commands.CreateUserCommand;
import com.cloudnrg.api.iam.domain.model.commands.SignInCommand;
import com.cloudnrg.api.iam.domain.model.commands.SignUpCommand;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Optional;

public interface UserCommandService {
    Optional<ImmutablePair<User, String>> handle(SignInCommand command);
    Optional<User> handle(SignUpCommand command);
    //TO-DO: Eliminar de todas las implementaciones este command y reemplazarlo por SignUpCommand
    Optional<User> handle(CreateUserCommand command);
}
