package com.cloudnrg.api.storage.application.internal.commandservices;

import com.cloudnrg.api.iam.infrastructure.persistance.jpa.repositories.UserRepository;
import com.cloudnrg.api.storage.domain.model.aggregates.CloudFile;
import com.cloudnrg.api.storage.domain.model.commands.CreateFileCommand;
import com.cloudnrg.api.storage.domain.model.commands.DeleteFileByIdCommand;
import com.cloudnrg.api.storage.domain.model.commands.UpdateFileFolderCommand;
import com.cloudnrg.api.storage.domain.model.commands.UpdateFileNameCommand;
import com.cloudnrg.api.storage.domain.model.events.CreateFileEvent;
import com.cloudnrg.api.storage.domain.model.events.DeleteFileEvent;
import com.cloudnrg.api.storage.domain.model.events.UpdateFileNameEvent;
import com.cloudnrg.api.storage.domain.model.events.UpdateFileParentFolderEvent;
import com.cloudnrg.api.storage.domain.services.FileCommandService;
import com.cloudnrg.api.storage.infrastructure.persistence.jpa.repositories.CloudFileRepository;
import com.cloudnrg.api.storage.infrastructure.persistence.jpa.repositories.FolderRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

@Service
public class FileCommandServiceImpl implements FileCommandService {

    private final CloudFileRepository cloudFileRepository;
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    private static final String UPLOAD_DIR = "C:\\Users\\Neo\\Documents\\provisional-storage\\";

    private final ApplicationEventPublisher eventPublisher;

    public FileCommandServiceImpl(CloudFileRepository cloudFileRepository,
                                 FolderRepository folderRepository,
                                 UserRepository userRepository,
                                 ApplicationEventPublisher eventPublisher) {
        this.cloudFileRepository = cloudFileRepository;
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }


    @Override
    public Optional<CloudFile> handle(CreateFileCommand command) {

        //find the first folder for the user
        var folder  = folderRepository.findFolderById(command.folderId());
        var user = userRepository.findUserById(command.userId());

        if (folder.isEmpty()) {
            throw new RuntimeException("Folder not found");
        }

        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        var tempFile = command.file();

        // Calculate MD5 hash
        String md5Hash;
        try {
            md5Hash = DigestUtils.md5Hex(tempFile.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }

        var tempPath = Paths.get(UPLOAD_DIR + tempFile.getOriginalFilename());

        var file = new CloudFile(
                tempFile.getOriginalFilename(),
                folder.get(),
                user.get(),
                tempFile.getSize(),
                tempFile.getContentType(),
                md5Hash,
                tempPath.toString()
        );

        try {

            cloudFileRepository.save(file);

            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(Objects.requireNonNull(tempFile.getOriginalFilename()));
            tempFile.transferTo(filePath.toFile());

            // Publish the event after saving the file
            eventPublisher.publishEvent(new CreateFileEvent(file, file.getId(), user.get().getId()));

        } catch (Exception e) {
            throw new IllegalArgumentException("Error while saving: " + e.getMessage());
        }

        return Optional.of(file);
    }


    @Override
    @Transactional
    public void handle(DeleteFileByIdCommand command) {
        var file = cloudFileRepository.findById(command.fileId());

        if (file.isEmpty()) {
            throw new RuntimeException("File not found");
        }

        try {

            // Publish the delete event
            eventPublisher.publishEvent(new DeleteFileEvent(file, file.get().getId(), file.get().getUser().getId()));

            // Delete the file record from the database
            cloudFileRepository.delete(file.get());


        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage());
        }
    }


    @Override
    public Optional<CloudFile> handle(UpdateFileFolderCommand command) {
        var fileResult = cloudFileRepository.findById(command.fileId());

        if (fileResult.isEmpty()) {
            throw new RuntimeException("File not found");
        }

        var file = fileResult.get();
        var oldFolder = file.getFolder();
        var newFolderResult = folderRepository.findFolderById(command.folderId());

        if (newFolderResult.isEmpty()) {
            throw new RuntimeException("New folder not found");
        }

        var newFolder = newFolderResult.get();

        file.setFolder(newFolder);

        try {
            cloudFileRepository.save(file);

            // Publish an event after updating the file folder
            eventPublisher.publishEvent(new UpdateFileParentFolderEvent(
                    file,
                    file.getId(),
                    file.getUser().getId(),
                    oldFolder.getName(),
                    newFolder.getName()
            ));

            return Optional.of(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update file folder: " + e.getMessage());
        }
    }


    @Override
    public Optional<CloudFile> handle(UpdateFileNameCommand command) {
        var fileResult = cloudFileRepository.findById(command.fileId());



        if (fileResult.isEmpty()) {
            throw new RuntimeException("File not found");
        }

        var file = fileResult.get();
        var oldName = file.getFilename();
        file.setFilename(command.name());

        try {
            cloudFileRepository.save(file);

            // Publish an event after updating the file name
            eventPublisher.publishEvent(new UpdateFileNameEvent(
                    file,
                    file.getId(),
                    file.getUser().getId(),
                    file.getFilename(),
                    oldName
            ));
            return Optional.of(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update file name: " + e.getMessage());
        }
    }
}
