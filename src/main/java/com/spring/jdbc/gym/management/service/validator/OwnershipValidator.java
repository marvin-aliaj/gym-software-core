package com.spring.jdbc.gym.management.service.validator;

import com.spring.jdbc.gym.management.model.User;

public interface OwnershipValidator {

    boolean isOwner(User authenticatedUser, String resourceId);

    String getResourceType();
}
