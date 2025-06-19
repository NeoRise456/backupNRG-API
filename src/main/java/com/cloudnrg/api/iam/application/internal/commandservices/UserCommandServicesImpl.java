package com.cloudnrg.api.iam.application.internal.commandservices;

import com.cloudnrg.api.iam.application.internal.outboundservices.hashing.HashingService;
import com.cloudnrg.api.iam.application.internal.outboundservices.tokens.TokenService;
import com.cloudnrg.api.iam.domain.model.aggregates.User;
import com.cloudnrg.api.iam.domain.model.commands.SignInCommand;
import com.cloudnrg.api.iam.domain.model.commands.SignUpCommand;
import com.cloudnrg.api.iam.domain.model.events.UserCreationEvent;
import com.cloudnrg.api.iam.domain.services.UserCommandService;
import com.cloudnrg.api.iam.infrastructure.persistance.jpa.repositories.RoleRepository;
import com.cloudnrg.api.iam.infrastructure.persistance.jpa.repositories.UserRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserCommandServicesImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;
    private final ApplicationEventPublisher eventPublisher;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserCommandServicesImpl.class);

    public UserCommandServicesImpl(
            UserRepository userRepository, HashingService hashingService, TokenService tokenService, RoleRepository roleRepository,
            ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<ImmutablePair<User, String>> handle(SignInCommand command) {
        var user = userRepository.findByUsername(command.username());
        if (user.isEmpty())
            throw new RuntimeException("User not found");
        if (!hashingService.matches(command.password(), user.get().getPassword()))
            throw new RuntimeException("Invalid password");

        var token = tokenService.generateToken(user.get().getUsername());
        return Optional.of(ImmutablePair.of(user.get(), token));
    }

    @Override
    public Optional<User> handle(SignUpCommand command) {
        if (userRepository.existsByUsername(command.username()))
            throw new RuntimeException("Username already exists");
        var roles = command.roles().stream()
                .map(role ->
                        roleRepository.findByName(role.getName())
                                .orElseThrow(() -> new RuntimeException("Role name not found")))
                .toList();
        var user = new User(command.username(), command.email(), hashingService.encode(command.password()), roles);
        var savedUser = userRepository.save(user);
        eventPublisher.publishEvent(new UserCreationEvent(savedUser, savedUser.getId()));
        return userRepository.findByUsername(command.username());
    }
}
