package com.cloudnrg.api.storage.interfaces.rest.resources;

import java.util.List;
import java.util.UUID;


public class HierarchyResource {
    private UUID id;
    private String name;
    private UUID parentFolderId;
    private UUID userId;
    private Long createTime;
    private Long modTime;
    private List<HierarchyResource> children;

    // Constructor with 6 arguments
    public HierarchyResource(UUID id, String name, UUID parentFolderId, UUID userId, Long createTime, Long modTime) {
        this.id = id;
        this.name = name;
        this.parentFolderId = parentFolderId;
        this.userId = userId;
        this.createTime = createTime;
        this.modTime = modTime;
    }

    // Getters for all fields
    public UUID getId() { return id; }
    public String getName() { return name; }
    public UUID getParentFolderId() { return parentFolderId; }
    public UUID getUserId() { return userId; }
    public Long getCreateTime() { return createTime; }
    public Long getModTime() { return modTime; }
    public List<HierarchyResource> getChildren() { return children; }

    // Setter for children
    public void setChildren(List<HierarchyResource> children) {
        this.children = children;
    }
}