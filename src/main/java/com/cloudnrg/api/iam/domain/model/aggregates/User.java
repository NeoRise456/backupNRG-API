package com.cloudnrg.api.iam.domain.model.aggregates;

import com.cloudnrg.api.iam.domain.model.entities.Role;
import com.cloudnrg.api.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class User extends AuditableAbstractAggregateRoot<User> {

    @NotNull
    @Column(unique = true)
    String username;

    @Email
    @NotNull
    String email;

    @NotNull
    @Size(max = 120)
    String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    public User() {
        this.roles = new HashSet<>();
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = new HashSet<>();
    }

    public User(String username, String email, String password, List<Role> roles) {
        this(username, email, password);
        addRoles(roles);
    }

    /**
     * Add a list of roles to the user
     * @param roles the list of roles to add
     * @return the user with the added roles
     */
    public User addRoles(List<Role> roles) {
        var validatedRoleSet = Role.validateRoleSet(roles);
        this.roles.addAll(validatedRoleSet);
        return this;
    }
}
