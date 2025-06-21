package com.cloudnrg.api.storage.interfaces.rest;


import com.cloudnrg.api.shared.application.external.outboundedservices.ExternalIamService;
import com.cloudnrg.api.storage.domain.model.aggregates.Folder;
import com.cloudnrg.api.storage.domain.model.commands.CreateFolderCommand;
import com.cloudnrg.api.storage.domain.model.commands.DeleteFolderByIdCommand;
import com.cloudnrg.api.storage.domain.model.commands.UpdateFolderNameCommand;
import com.cloudnrg.api.storage.domain.model.commands.UpdateFolderParentCommand;
import com.cloudnrg.api.storage.domain.model.queries.GetFolderByIdQuery;
import com.cloudnrg.api.storage.domain.model.queries.GetFolderHierarchyByIdQuery;
import com.cloudnrg.api.storage.domain.model.queries.GetRootFolderByUserIdQuery;
import com.cloudnrg.api.storage.domain.services.FolderCommandService;
import com.cloudnrg.api.storage.domain.services.FolderQueryService;
import com.cloudnrg.api.storage.interfaces.rest.resources.CreateFolderResource;
import com.cloudnrg.api.storage.interfaces.rest.resources.FolderResource;
import com.cloudnrg.api.storage.interfaces.rest.resources.HierarchyResource;
import com.cloudnrg.api.storage.interfaces.rest.transform.CreateFolderCommandFromResourceAssembler;
import com.cloudnrg.api.storage.interfaces.rest.transform.FolderResourceFromEntityAssembler;
import com.cloudnrg.api.storage.interfaces.rest.transform.HierarchyResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final ExternalIamService externalIamService;

    public FolderController(FolderCommandService folderCommandService, FolderQueryService folderQueryService, ExternalIamService externalIamService) {
        this.folderCommandService = folderCommandService;
        this.folderQueryService = folderQueryService;
        this.externalIamService = externalIamService;
    }

    @Operation(summary = "Get root folder by user authenticated", description = "Get root folder by user authenticated")
    @GetMapping(value = "/root")
    @ApiResponses(value = {
            @ApiResponse( responseCode = "200", description = "Root folder retrieved successfully"),
            @ApiResponse( responseCode = "400", description = "Invalid input data"),
            @ApiResponse( responseCode = "401", description = "Unauthorized"),
    })
    public ResponseEntity<FolderResource> getRootFolderByUserId(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        var user = externalIamService.getUserByUsername(username);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var userId = user.get().getId();

        var getRootFolderByUserIdQuery = new GetRootFolderByUserIdQuery(userId);

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
    //TO-DO: Reemplazar
    @PostMapping("/new")
    public ResponseEntity<FolderResource> createFolder(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CreateFolderResource resource) {
        String username = userDetails.getUsername();
        var user = externalIamService.getUserByUsername(username);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var userId = user.get().getId();
        var createFolderCommand = CreateFolderCommandFromResourceAssembler.toCommandFromResource(userId, resource);

        try {
            var folder = folderCommandService.handle(createFolderCommand);
            var folderResource = FolderResourceFromEntityAssembler.toResourceFromEntity(folder.get());
            return ResponseEntity.status(201).body(folderResource);
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

    @Operation(summary = "Get folder hierarchy by user authenticated", description = "Returns the full folder hierarchy for a user authenticated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hierarchy retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Root folder not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/hierarchy")
    public ResponseEntity<HierarchyResource> getFolderHierarchyByUserId(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        var user = externalIamService.getUserByUsername(username);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var userId = user.get().getId();
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


    @Operation(summary = "Get Folder By Id", description = "Returns a folder by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Folder retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Folder not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping()
    public ResponseEntity<?> getFolderById(
            @RequestParam(required = false) UUID folderId,
            @AuthenticationPrincipal UserDetails userDetails
            ) {

        String username = userDetails.getUsername();
        var user = externalIamService.getUserByUsername(username);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var userId = user.get().getId();



        if (folderId == null) {
            var rootFolderOpt = folderQueryService.handle(new GetRootFolderByUserIdQuery(userId));
            if (rootFolderOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            var folderResource = FolderResourceFromEntityAssembler.toResourceFromEntity(rootFolderOpt.get());
            return ResponseEntity.ok(folderResource);
        }

        var query = new GetFolderByIdQuery(folderId);
        var folderOpt = folderQueryService.handle(query);
        if (folderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var folderResource = FolderResourceFromEntityAssembler.toResourceFromEntity(folderOpt.get());
        return ResponseEntity.ok(folderResource);
    }

    @Operation(summary = "Get Folders by Parent Id ", description = "Returns a list of folders by their parent folder ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Folders retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Parent folder not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/parent/{parentFolderId}")
    public ResponseEntity<List<FolderResource>> getFoldersByParentFolderId(@PathVariable UUID parentFolderId) {
        var query = new com.cloudnrg.api.storage.domain.model.queries.GetFoldersByParentFolderIdQuery(parentFolderId);
        var folders = folderQueryService.handle(query);
        if (folders.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var folderResources = folders.stream()
                .map(FolderResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(folderResources);
    }


}