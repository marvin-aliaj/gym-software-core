package com.spring.jdbc.gym.management.service.validator;

import com.spring.jdbc.gym.management.model.User;
import com.spring.jdbc.gym.management.model.enums.RESOURCE_TYPE;
import org.springframework.stereotype.Component;

@Component
public class UserOwnershipValidator implements OwnershipValidator {
    
    @Override
    public boolean isOwner(User authenticatedUser, String resourceId) {
        if (authenticatedUser == null || resourceId == null) {
            return false;
        }
        return authenticatedUser.getId().equals(resourceId);
    }
    
    @Override
    public String getResourceType() {
        return RESOURCE_TYPE.USER.getValue();
    }

    public boolean canCreateAdmin(User authenticatedUser) {
        return authenticatedUser != null && "Admin".equalsIgnoreCase(authenticatedUser.getRole().getDescription ());
    }
}
