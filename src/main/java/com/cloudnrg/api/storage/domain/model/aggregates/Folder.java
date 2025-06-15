package com.cloudnrg.api.storage.domain.model.aggregates;

import com.cloudnrg.api.iam.domain.model.aggregates.User;
import com.cloudnrg.api.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Folder extends AuditableAbstractAggregateRoot<Folder> {

    @NotNull
    String name;

    @ManyToOne
    @JoinColumn(name = "parent_folder_id")
    Folder parentFolder;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    public Folder(String name, Folder parentFolder, User user) {
        this.name = name;
        this.parentFolder = parentFolder;
        this.user = user;
    }

    public void setParentFolderWithCycleVerification(Folder newParent) {
        if (newParent != null && wouldCreateCycle(this, newParent)) {
            throw new IllegalStateException("¡Asignar este padre crearía un ciclo!");
        }
        this.parentFolder = newParent;
    }

    private boolean wouldCreateCycle(Folder current, Folder newParent) {
        // Caso base: el nuevo padre ES la carpeta actual (ciclo directo A -> A)
        if (current.equals(newParent)) {
            return true;
        }
        // Si el nuevo padre no tiene más ancestros, no hay ciclo.
        if (newParent.getParentFolder() == null) {
            return false;
        }
        // Recorre recursivamente los ancestros del nuevo padre.
        return wouldCreateCycle(current, newParent.getParentFolder());
    }

}
