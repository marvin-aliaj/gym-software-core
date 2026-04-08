package com.spring.jdbc.gym.management.model.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static com.spring.jdbc.gym.management.model.enums.CONSTANT_VARIABLES.ENTITY_MINUS_ONE_INT;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFilter extends Filter {
    private String username;
    private String password;
    private String email;
    private String searchQuery;
    private List<Long> userIds;
    private String gymId;
    private int role = ENTITY_MINUS_ONE_INT;

    public UserFilter(String entityId, int limit, int offset) {
        super(entityId, limit, offset);
    }

    public UserFilter(String password, String email) {
        this.password = password;
        this.email = email;
    }

    public UserFilter(String gymId) {
        this.gymId = gymId;
    }

    public boolean hasUsernameSet() {
        return username != null && !username.isEmpty();
    }

    public boolean hasPasswordSet() {
        return password != null && !password.isEmpty();
    }

    public boolean hasEmailSet() {
        return email != null && !email.isEmpty();
    }

    public boolean hasSearchQuerySet() {
        return searchQuery != null && !searchQuery.isEmpty();
    }

    public boolean hasGymIdSet() {
        return gymId != null && !gymId.isEmpty();
    }

    public boolean hasUserIdsSet() {
        return userIds != null && !userIds.isEmpty();
    }

    public boolean hasRoleSet() {
        return role != ENTITY_MINUS_ONE_INT;
    }

}
