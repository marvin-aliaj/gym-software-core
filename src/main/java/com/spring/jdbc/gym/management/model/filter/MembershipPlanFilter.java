package com.spring.jdbc.gym.management.model.filter;

import lombok.Getter;
import lombok.Setter;

import static com.spring.jdbc.gym.management.model.enums.CONSTANT_VARIABLES.NIL_UUID;

@Getter
@Setter
public class MembershipPlanFilter extends Filter {
    private String businessId;
    private String userId;
    private Boolean isActive;

    public MembershipPlanFilter() {
        super();
        this.businessId = NIL_UUID;
        this.userId = NIL_UUID;
        this.isActive = null;
    }

    public MembershipPlanFilter(int offset, int limit) {
        this.limit = limit;
        this.offset = offset;
        this.businessId = NIL_UUID;
        this.userId = NIL_UUID;
        this.isActive = null;
    }

    public boolean hasBusinessIdSet() {
        return this.businessId != null && !this.businessId.equals(NIL_UUID);
    }

    public boolean hasUserIdSet() {
        return this.userId != null && !this.userId.equals(NIL_UUID);
    }

    public boolean hasIsActiveSet() {
        return this.isActive != null;
    }
}
