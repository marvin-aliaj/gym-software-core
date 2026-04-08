package com.spring.jdbc.gym.management.model.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessFilter extends Filter {
    private String searchQuery;
    private String userId;

    public BusinessFilter (String entityId, int limit, int offset) {
        super(entityId, limit, offset);
    }

    public boolean hasSearchQuerySet() {
        return searchQuery != null && !searchQuery.isEmpty();
    }

    public boolean hasUserIdSet() {
        return userId != null && !userId.isEmpty();
    }

}
