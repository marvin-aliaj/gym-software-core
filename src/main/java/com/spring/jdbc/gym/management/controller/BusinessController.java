package com.spring.jdbc.gym.management.controller;

import com.spring.jdbc.gym.management.annotation.RequiresRole;
import com.spring.jdbc.gym.management.exception.CustomException;
import com.spring.jdbc.gym.management.model.Business;
import com.spring.jdbc.gym.management.model.enums.RESOURCE_TYPE;
import com.spring.jdbc.gym.management.model.filter.BusinessFilter;
import com.spring.jdbc.gym.management.service.BusinessService;
import com.spring.jdbc.gym.management.service.PermissionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class BusinessController {
    private final BusinessService businessService;
    private final PermissionService permissionService;

    public BusinessController (BusinessService businessService, PermissionService permissionService) {
        this.businessService = businessService;
        this.permissionService = permissionService;
    }

    @GetMapping("/businesses")
    @RequiresRole({"ADMIN"})
    public ResponseEntity<Object> getBusinesses(
            HttpServletRequest request) {
        try {
            BusinessFilter filter = new BusinessFilter ();
            List<Business> businesses = businessService.getBusinessList (filter);
            
            return new ResponseEntity<>(businesses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/businesses/{id}")
    @RequiresRole({"ADMIN", "BUSINESS_OWNER", "BUSINESS_MANAGER", "TRAINER", "STAFF", "CLIENT"})
    public ResponseEntity<Object> getGymById(
            HttpServletRequest request,
            @PathVariable String id) {
        try {
            if (!permissionService.canView(request, RESOURCE_TYPE.BUSINESS, id)) {
                return new ResponseEntity<>("Forbidden: You cannot view this business", HttpStatus.FORBIDDEN);
            }

            BusinessFilter filter = new BusinessFilter (id, 1, 0);
            List<Business> businesses = businessService.getBusinessList (filter);

            if (businesses.isEmpty()) {
                return new ResponseEntity<>("Business not found", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(businesses.get(0), HttpStatus.OK);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>("Invalid user ID", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/businesses")
    @RequiresRole("ADMIN")
    public ResponseEntity<Object> createBusiness(@RequestBody Business business) {
        try {
            Business createdBusiness = businessService.createBusiness(business);
            return new ResponseEntity<>(createdBusiness, HttpStatus.CREATED);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/businesses/{id}")
    @RequiresRole({"ADMIN", "BUSINESS_OWNER"})
    public ResponseEntity<Object> updateBusiness(
            HttpServletRequest request,
            @PathVariable String id,
            @RequestBody Business business) {
        try {
            if (!permissionService.canModify(request, RESOURCE_TYPE.BUSINESS, id)) {
                return new ResponseEntity<>("Forbidden: Cannot modify this business", HttpStatus.FORBIDDEN);
            }
            
            business.setId(id);
            businessService.updateBusiness(business);
            return new ResponseEntity<>("Business updated successfully", HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/businesses/{id}")
    @RequiresRole({"ADMIN"})
    public ResponseEntity<Object> deleteBusiness(
            HttpServletRequest request,
            @PathVariable String id) {
        try {
            if (!permissionService.canDelete(request, RESOURCE_TYPE.BUSINESS, id)) {
                return new ResponseEntity<>("Forbidden: Cannot delete this business", HttpStatus.FORBIDDEN);
            }

            businessService.deleteBusiness(id);
            return new ResponseEntity<>("Business deleted successfully", HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}