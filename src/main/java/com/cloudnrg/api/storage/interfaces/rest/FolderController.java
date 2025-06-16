package com.cloudnrg.api.storage.interfaces.rest;


import com.cloudnrg.api.storage.domain.model.aggregates.Folder;
import com.cloudnrg.api.storage.domain.model.commands.CreateFolderCommand;
import com.cloudnrg.api.storage.domain.model.commands.DeleteFolderByIdCommand;
import com.cloudnrg.api.storage.domain.model.commands.UpdateFolderNameCommand;
import com.cloudnrg.api.storage.domain.model.commands.UpdateFolderParentCommand;
import com.cloudnrg.api.storage.domain.model.queries.GetFolderHierarchyByIdQuery;
import com.cloudnrg.api.storage.domain.model.queries.GetRootFolderByUserIdQuery;
import com.cloudnrg.api.storage.domain.services.FolderCommandService;
import com.cloudnrg.api.storage.domain.services.FolderQueryService;
import com.cloudnrg.api.storage.interfaces.rest.resources.FolderResource;
import com.cloudnrg.api.storage.interfaces.rest.resources.HierarchyResource;
import com.cloudnrg.api.storage.interfaces.rest.transform.FolderResourceFromEntityAssembler;
import com.cloudnrg.api.storage.interfaces.rest.transform.HierarchyResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", methods = { RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE })
@RestController
@RequestMapping(value = "/api/v1/folders", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Folder Controller", description = "Folder Management Endpoints")
public class FolderController {
    private final FolderCommandService folderCommandService;
    private final FolderQueryService folderQueryService;

    public FolderController(FolderCommandService folderCommandService, FolderQueryService folderQueryService) {
        this.folderCommandService = folderCommandService;
        this.folderQueryService = folderQueryService;
    }

    @Operation(summary = "Get root folder by user id", description = "Get root folder by user id")
    @GetMapping(
            value = "/root",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse( responseCode = "200", description = "Root folder retrieved successfully"),
            @ApiResponse( responseCode = "400", description = "Invalid input data"),
            @ApiResponse( responseCode = "401", description = "Unauthorized"),
    })
    public ResponseEntity<FolderResource> getRootFolderByUserId(
            @RequestParam UUID userId
    ) {

        var getRootFolderByUserIdQuery = new GetRootFolderByUserIdQuery(
                userId
        );

        var folder = folderQueryService.handle(getRootFolderByUserIdQuery);

        if (folder.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        var folderResource = FolderResourceFromEntityAssembler.toResourceFromEntity(folder.get());

        return ResponseEntity.ok(folderResource);
    }

    @Operation(summary = "Create folder", description = "Creates a new folder")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Folder created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Related resource not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<FolderResource> createFolder(@RequestBody CreateFolderCommand command) {
        try {
            var folder = folderCommandService.handle(command);
            var resource = FolderResourceFromEntityAssembler.toResourceFromEntity(folder.get());
            return ResponseEntity.status(201).body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Update folder name", description = "Updates the name of a folder by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Folder name updated successfully"),
            @ApiResponse(responseCode = "404", description = "Folder not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
    })
    @PutMapping("/{folderId}/name")
    public ResponseEntity<FolderResource> updateFolderName(
            @PathVariable UUID folderId,
            @RequestParam String name) {
        try {
            var command = new UpdateFolderNameCommand(folderId, name);
            var folder = folderCommandService.handle(command);
            if (folder.isEmpty()) return ResponseEntity.notFound().build();
            var resource = FolderResourceFromEntityAssembler.toResourceFromEntity(folder.get());
            return ResponseEntity.ok(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Batch update parent folder", description = "Updates the parent folder for multiple folders by their IDs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parent folders updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Some folders or parent not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/parent")
    public ResponseEntity<List<FolderResource>> updateParentFolders(
            @RequestBody List<UUID> folderIds,
            @RequestParam UUID parentId) {
        try {
            var updatedFolders = folderIds.stream()
                    .map(folderId -> {
                        var command = new UpdateFolderParentCommand(folderId, parentId);
                        var folder = folderCommandService.handle(command);
                        return folder.map(FolderResourceFromEntityAssembler::toResourceFromEntity).orElse(null);
                    })
                    .filter(java.util.Objects::nonNull)
                    .toList();

            if (updatedFolders.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedFolders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @Operation(summary = "Delete folder", description = "Deletes a folder by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Folder deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Folder not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{folderId}")
    public ResponseEntity<Void> deleteFolder(@PathVariable UUID folderId) {
        try {
            folderCommandService.handle(new DeleteFolderByIdCommand(folderId));
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get folder hierarchy by user id", description = "Returns the full folder hierarchy for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hierarchy retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Root folder not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/hierarchy")
    public ResponseEntity<HierarchyResource> getFolderHierarchyByUserId(@RequestParam UUID userId) {
        var rootFolderOpt = folderQueryService.handle(new GetRootFolderByUserIdQuery(userId));
        if (rootFolderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var rootFolder = rootFolderOpt.get();
        // The buildHierarchy method now correctly uses the assembler
        HierarchyResource rootResource = buildHierarchy(rootFolder);
        return ResponseEntity.ok(rootResource);
    }

    private HierarchyResource buildHierarchy(Folder folder) {
        // Delegate the recursive hierarchy building to HierarchyResourceFromEntityAssembler.
        // Provide a lambda function to fetch children for a given parent folder ID.
        // GetFolderHierarchyByIdQuery is assumed to fetch direct children of a folder.
        return HierarchyResourceFromEntityAssembler.toResourceFromEntity(
                folder,
                parentId -> folderQueryService.handle(new GetFolderHierarchyByIdQuery(parentId))
        );
    }

}