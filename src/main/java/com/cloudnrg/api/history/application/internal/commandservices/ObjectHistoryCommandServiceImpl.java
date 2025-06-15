package com.cloudnrg.api.history.application.internal.commandservices;

import com.cloudnrg.api.history.domain.model.aggregates.ObjectHistory;
import com.cloudnrg.api.history.domain.model.commands.CreateObjectHistoryCommand;
import com.cloudnrg.api.history.domain.model.commands.DeleteObjectHistoriesByFileId;
import com.cloudnrg.api.history.domain.services.ObjectHistoryCommandService;
import com.cloudnrg.api.history.infrastructure.persistence.jpa.repositories.ObjectHistoryRepository;
import com.cloudnrg.api.iam.infrastructure.persistance.jpa.repositories.UserRepository;
import com.cloudnrg.api.storage.infrastructure.persistence.jpa.repositories.CloudFileRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ObjectHistoryCommandServiceImpl implements ObjectHistoryCommandService {
    private final ObjectHistoryRepository objectHistoryRepository;

    //TODO: refactor to external user service
    private final UserRepository userRepository;

    //TODO: refactor to external file service
    private final CloudFileRepository cloudFileRepository;

    public ObjectHistoryCommandServiceImpl(
            ObjectHistoryRepository objectHistoryRepository,
            UserRepository userRepository,
            CloudFileRepository cloudFileRepository) {
        this.objectHistoryRepository = objectHistoryRepository;
        this.userRepository = userRepository;
        this.cloudFileRepository = cloudFileRepository;
    }

    @Override
    public Optional<ObjectHistory> handle(CreateObjectHistoryCommand command) {
        var file = cloudFileRepository.findById(command.fileId());
        var user = userRepository.findUserById(command.userId());

        if (file.isEmpty()) {
            throw new RuntimeException("File not found");
        }
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        var objectHistory = new ObjectHistory(
                file.get(),
                user.get(),
                command.action(),
                command.message()
        );

        try {
            var savedObjectHistory = objectHistoryRepository.save(objectHistory);
            return Optional.of(savedObjectHistory);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create object history: " + e.getMessage());
        }
    }

    @Override
    public void handle(DeleteObjectHistoriesByFileId command) {


        objectHistoryRepository.deleteObjectHistoriesByFile_Id(command.fileId());

    }
}
