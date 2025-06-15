package com.cloudnrg.api.history.domain.model.aggregates;

import com.cloudnrg.api.history.domain.model.valueobjects.ObjectAction;
import com.cloudnrg.api.iam.domain.model.aggregates.User;
import com.cloudnrg.api.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.cloudnrg.api.storage.domain.model.aggregates.CloudFile;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ObjectHistory extends AuditableAbstractAggregateRoot<ObjectHistory> {
    @ManyToOne
    @JoinColumn(name = "file_id")
    private CloudFile file;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ObjectAction action;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    //defined as text
    @NotNull
    @Column(columnDefinition = "TEXT")
    private String message;

    public ObjectHistory(CloudFile file, User user, ObjectAction action, String message) {
        this.file = file;
        this.action = action;
        this.message = message;
        this.user = user;
    }

    public UUID getFileId() {
        return this.file.getId();
    }

    public UUID getUserId() {
        return this.user.getId();
    }
}
