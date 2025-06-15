package com.cloudnrg.api.iam.application.internal.commandservices;

import com.cloudnrg.api.iam.domain.model.aggregates.User;
import com.cloudnrg.api.iam.domain.model.commands.CreateUserCommand;
import com.cloudnrg.api.iam.domain.model.events.UserCreationEvent;
import com.cloudnrg.api.iam.domain.services.UserCommandService;
import com.cloudnrg.api.iam.infrastructure.persistance.jpa.repositories.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserCommandServicesImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserCommandServicesImpl.class);

    public UserCommandServicesImpl(
            UserRepository userRepository,
            ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<User> handle(CreateUserCommand command) {

        var newUser = new User(
                command.username(),
                command.email(),
                command.passwordHash()
        );

        try {

            var savedUser = userRepository.save(newUser);

            //publish event
            eventPublisher.publishEvent(new UserCreationEvent(savedUser, savedUser.getId()));

            return Optional.of(savedUser);

        } catch (Exception e) {
            // Log the original exception with its full stack trace
            logger.error("Failed to create user '{}'. Original exception: ", command.username(), e);
            // Re-throw the exception, chaining the original 'e' to preserve its stack trace
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }




    }
}
