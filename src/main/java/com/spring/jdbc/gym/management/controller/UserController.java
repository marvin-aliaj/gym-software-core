package com.spring.jdbc.gym.management.controller;

import com.spring.jdbc.gym.management.annotation.RequiresRole;
import com.spring.jdbc.gym.management.exception.CustomException;
import com.spring.jdbc.gym.management.exception.ForbiddenException;
import com.spring.jdbc.gym.management.model.User;
import com.spring.jdbc.gym.management.model.enums.RESOURCE_TYPE;
import com.spring.jdbc.gym.management.model.filter.UserFilter;
import com.spring.jdbc.gym.management.service.PermissionService;
import com.spring.jdbc.gym.management.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.spring.jdbc.gym.management.model.enums.CONSTANT_VARIABLES.*;

@CrossOrigin(origins = "*")
@RestController
public class UserController {
    private final UserService userService;
    private final PermissionService permissionService;

    public UserController(UserService userService, PermissionService permissionService) {
        this.userService = userService;
        this.permissionService = permissionService;
    }

    @GetMapping("/businesses/{businessId}/users")
    @RequiresRole({"ADMIN", "BUSINESS_OWNER", "BUSINESS_MANAGER", "TRAINER", "STAFF", "CLIENT"})
    public ResponseEntity<Object> getUsers(
            HttpServletRequest request,
            @PathVariable String businessId,
            @RequestParam(value = "userId", defaultValue = NIL_UUID) String userId,
            @RequestParam(value = "searchQuery", defaultValue = "") String searchQuery,
            @RequestParam(value = "userRole", defaultValue = ENTITY_MINUS_ONE_INT + "") int userRole,
            @RequestParam(value = "limit", defaultValue = DEFAULT_LIMIT + "") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset
    ) {
        try {
            // Check permission: Admin can view any business, others can only view their assigned businesses
            if (!permissionService.canView(request, RESOURCE_TYPE.BUSINESS, businessId)) {
                return new ResponseEntity<>("Forbidden: You cannot access users from this business", HttpStatus.FORBIDDEN);
            }
            
            UserFilter filter = new UserFilter(userId, limit, offset);
            filter.setGymId(businessId);
            filter.setRole(userRole);
            filter.setSearchQuery(searchQuery);
            List<User> users = userService.getUsers(filter);

            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("test")
    @RequiresRole({"ADMIN"})
    public ResponseEntity<String> test() {
        return new ResponseEntity<>("test", HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    @RequiresRole({"ADMIN"})
    public ResponseEntity<Object> getUserById(
            HttpServletRequest request,
            @PathVariable String id) {
        try {
            if (!permissionService.canView(request, RESOURCE_TYPE.USER, id)) {
                return new ResponseEntity<>("Forbidden: Cannot view other users", HttpStatus.FORBIDDEN);
            }
            
            UserFilter filter = new UserFilter(id, 1, 0);
            List<User> users = userService.getUsers(filter);
            
            if (users.isEmpty()) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            
            return new ResponseEntity<>(users.get(0), HttpStatus.OK);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>("Invalid user ID", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/users")
    @RequiresRole({"ADMIN", "BUSINESS_OWNER", "BUSINESS_MANAGER", "TRAINER", "STAFF"})
    public ResponseEntity<Object> createUser(
            HttpServletRequest request,
            @RequestBody User user) {
        try {
            if (!permissionService.canCreateUser(request, user)) {
                return new ResponseEntity<>("Forbidden: You cannot create user", HttpStatus.FORBIDDEN);
            }
            userService.createUser(user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/users/{id}")
    @RequiresRole({"ADMIN", "CLIENT"})
    public ResponseEntity<Object> updateUser(
            HttpServletRequest request,
            @PathVariable String id,
            @RequestBody User user) {
        try {
            if (!permissionService.canModify(request, RESOURCE_TYPE.USER, id)) {
                return new ResponseEntity<>("Forbidden: Cannot modify other users", HttpStatus.FORBIDDEN);
            }
            user.setId(id);
            userService.updateUser(user);
            return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/users/{id}")
    @RequiresRole({"ADMIN"})
    public ResponseEntity<Object> deleteUser(
            HttpServletRequest request,
            @PathVariable String id) {
        try {
            // Check permission
            if (!permissionService.canDelete(request, RESOURCE_TYPE.USER, id)) {
                return new ResponseEntity<>("Forbidden: Cannot delete other users", HttpStatus.FORBIDDEN);
            }
            
            userService.deleteUser(id);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>("Invalid user ID", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}