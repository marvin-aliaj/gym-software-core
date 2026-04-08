package com.spring.jdbc.gym.management.service.validator;

import com.spring.jdbc.gym.management.model.User;
import com.spring.jdbc.gym.management.model.enums.RESOURCE_TYPE;
import org.springframework.stereotype.Component;

@Component
public class BusinessOwnershipValidator implements OwnershipValidator {
    
    @Override
    public boolean isOwner(User authenticatedUser, String resourceId) {
        if (authenticatedUser == null || resourceId == null) {
            return false;
        }
        
        // Check if user has a gym assigned
        if (authenticatedUser.getBusinesses () == null || authenticatedUser.getBusinesses ().isEmpty()) {
            return false;
        }
        return authenticatedUser.getBusinesses ().stream ().anyMatch (business -> business.getId ().equals (resourceId));
    }
    
    @Override
    public String getResourceType() {
        return RESOURCE_TYPE.BUSINESS.getValue();
    }
}
