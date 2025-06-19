package com.cloudnrg.api.history.interfaces.rest;

import com.cloudnrg.api.history.domain.model.queries.GetAllObjectsHistoryByFileIdQuery;
import com.cloudnrg.api.history.domain.model.queries.GetAllObjectsHistoryByUserIdQuery;
import com.cloudnrg.api.history.domain.model.queries.GetObjectHistoryByIdQuery;
import com.cloudnrg.api.history.domain.services.ObjectHistoryCommandService;
import com.cloudnrg.api.history.domain.services.ObjectHistoryQueryService;
import com.cloudnrg.api.history.interfaces.rest.resources.CreateObjectHistoryResource;
import com.cloudnrg.api.history.interfaces.rest.resources.ObjectHistoryResource;
import com.cloudnrg.api.history.interfaces.rest.transform.CreateObjectHistoryCommandFromResourceAssembler;
import com.cloudnrg.api.history.interfaces.rest.transform.ObjectHistoryResourceFromEntityAssembler;
import com.cloudnrg.api.shared.application.external.outboundedservices.ExternalIamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/history", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Objects History", description = "Objects History Management Endpoint")
public class ObjectsHistoryController {
    private final ObjectHistoryQueryService queryService;
    private final ExternalIamService externalIamService;

    public ObjectsHistoryController(ObjectHistoryQueryService queryService, ExternalIamService externalIamService) {
        this.externalIamService = externalIamService;
        this.queryService = queryService;
    }

    @Operation(summary = "Get Object History by ID", description = "Retrieve an Object History record by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Object History found"),
            @ApiResponse(responseCode = "404", description = "Object History not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ObjectHistoryResource> getObjectHistoryById(@PathVariable UUID id) {
        var getObjectHistoryByIdQuery = new GetObjectHistoryByIdQuery(id);
        var objectHistory = queryService.handle(getObjectHistoryByIdQuery);
        if (objectHistory.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var objectHistoryResource = ObjectHistoryResourceFromEntityAssembler.toResourceFromEntity(objectHistory.get());
        return ResponseEntity.ok(objectHistoryResource);
    }

    @Operation(summary = "Get all Object Histories by User ID", description = "Retrieve all Object History records associated with a specific User ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Object Histories found"),
            @ApiResponse(responseCode = "404", description = "No Object Histories found for the User ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @GetMapping("/user")
    public ResponseEntity<List<ObjectHistoryResource>> getAllObjectsHistoryByUserId(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        var user = externalIamService.getUserByUsername(username);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var userId = user.get().getId();
        var getAllObjectsHistoryQuery = new GetAllObjectsHistoryByUserIdQuery(userId);
        var objectsHistory = queryService.handle(getAllObjectsHistoryQuery);
        if (objectsHistory.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var objectsHistoryResources = objectsHistory.stream()
                .map(ObjectHistoryResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(objectsHistoryResources);
    }

    @Operation(summary = "Get all Object Histories by File ID", description = "Retrieve all Object History records associated with a specific File ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Object Histories found"),
            @ApiResponse(responseCode = "404", description = "No Object Histories found for the File ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/file/{fileId}")
    public ResponseEntity<List<ObjectHistoryResource>> getAllObjectsHistoryByFileId(@PathVariable UUID fileId) {
        var getAllObjectsHistoryQuery = new GetAllObjectsHistoryByFileIdQuery(fileId);
        var objectsHistory = queryService.handle(getAllObjectsHistoryQuery);

        //TODO: hay posibilidad de que el archivo sea reciente y no tenga historial, en ese caso retornar un objeto vacio
        if (objectsHistory.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var objectsHistoryResources = objectsHistory.stream()
                .map(ObjectHistoryResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(objectsHistoryResources);
    }
}
