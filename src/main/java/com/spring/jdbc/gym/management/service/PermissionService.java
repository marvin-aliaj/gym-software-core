package com.spring.jdbc.gym.management.service;

import com.spring.jdbc.gym.management.dao.UserDao;
import com.spring.jdbc.gym.management.model.User;
import com.spring.jdbc.gym.management.model.enums.RESOURCE_TYPE;
import com.spring.jdbc.gym.management.model.filter.UserFilter;
import com.spring.jdbc.gym.management.service.validator.OwnershipValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PermissionService {
    private final UserDao userDao;
    private final Map<String, OwnershipValidator> validators;

    public PermissionService(UserDao userDao, List<OwnershipValidator> validatorList) {
        this.userDao = userDao;
        // Build a map of validators by resource type for quick lookup
        this.validators = validatorList.stream()
                .collect(Collectors.toMap(
                        OwnershipValidator::getResourceType,
                        Function.identity()
                ));
    }

    public boolean canModify(HttpServletRequest request, RESOURCE_TYPE RESOURCE_TYPE, String resourceId) {
        return checkPermission(request, RESOURCE_TYPE, resourceId);
    }

    public boolean canView(HttpServletRequest request, RESOURCE_TYPE RESOURCE_TYPE, String resourceId) {
        return checkPermission(request, RESOURCE_TYPE, resourceId);
    }

    public boolean canDelete(HttpServletRequest request, RESOURCE_TYPE RESOURCE_TYPE, String resourceId) {
        return checkPermission(request, RESOURCE_TYPE, resourceId);
    }

    private boolean checkPermission(HttpServletRequest request, RESOURCE_TYPE RESOURCE_TYPE, String resourceId) {
        // Admin can access anything
        if (isAdmin(request)) {
            return true;
        }

        // Get the appropriate validator for this resource type
        OwnershipValidator validator = validators.get(RESOURCE_TYPE.getValue());
        if (validator == null) {
            return false;
        }

        // Get authenticated user
        User authenticatedUser = getAuthenticatedUser(request);
        if (authenticatedUser == null) {
            return false;
        }

        // check ownership
        return validator.isOwner(authenticatedUser, resourceId);
    }

    public boolean canCreateUser(HttpServletRequest request, User userToBeCreated) {
        String authenticatedRole = (String) request.getAttribute("authenticatedRole");
        if ("Admin".equalsIgnoreCase (authenticatedRole)) {
            return true;
        } else if ("Business_Owner".equalsIgnoreCase (authenticatedRole)) {
            return "Business_Manager".equalsIgnoreCase (userToBeCreated.getRole().getDescription ()) ||
                    "Trainer".equalsIgnoreCase (userToBeCreated.getRole().getDescription ()) ||
                    "Staff".equalsIgnoreCase (userToBeCreated.getRole().getDescription ()) ||
                    "Client".equalsIgnoreCase (userToBeCreated.getRole().getDescription ());
        } else if ("Business_Manager".equalsIgnoreCase (authenticatedRole)) {
            return "Trainer".equalsIgnoreCase (userToBeCreated.getRole().getDescription ()) ||
                    "Staff".equalsIgnoreCase (userToBeCreated.getRole().getDescription ()) ||
                    "Client".equalsIgnoreCase (userToBeCreated.getRole().getDescription ());
        } else if ("Trainer".equalsIgnoreCase (authenticatedRole)) {
            return "Client".equalsIgnoreCase (userToBeCreated.getRole().getDescription ());
        } else if ("Staff".equalsIgnoreCase (authenticatedRole)) {
            return "Client".equalsIgnoreCase (userToBeCreated.getRole().getDescription ());
        } else {
            return false;
        }
    }

    public User getAuthenticatedUser(HttpServletRequest request) {
        try {
            String authenticatedEmail = (String) request.getAttribute("authenticatedUser");
            if (authenticatedEmail == null) {
                return null;
            }

            UserFilter filter = new UserFilter();
            filter.setEmail(authenticatedEmail);
            List<User> users = userDao.getUserList(filter);

            return users.isEmpty() ? null : users.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isAdmin(HttpServletRequest request) {
        String authenticatedRole = (String) request.getAttribute("authenticatedRole");
        return "ADMIN".equalsIgnoreCase(authenticatedRole);
    }
}
