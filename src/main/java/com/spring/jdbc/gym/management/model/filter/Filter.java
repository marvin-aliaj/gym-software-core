package com.spring.jdbc.gym.management.model.filter;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.spring.jdbc.gym.management.model.enums.CONSTANT_VARIABLES.DEFAULT_LIMIT;
import static com.spring.jdbc.gym.management.model.enums.CONSTANT_VARIABLES.NIL_UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
abstract class Filter {
    protected String entityId = NIL_UUID;
    protected int limit = DEFAULT_LIMIT;
    protected int offset = 0;

    public boolean hasEntityId() {
        return entityId != null && !entityId.equals(NIL_UUID);
    }
}
