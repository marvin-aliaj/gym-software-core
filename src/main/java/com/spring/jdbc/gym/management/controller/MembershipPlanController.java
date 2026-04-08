package com.spring.jdbc.gym.management.controller;

import com.spring.jdbc.gym.management.annotation.RequiresRole;
import com.spring.jdbc.gym.management.exception.CustomException;
import com.spring.jdbc.gym.management.model.MembershipPlan;
import com.spring.jdbc.gym.management.model.filter.MembershipPlanFilter;
import com.spring.jdbc.gym.management.service.MembershipPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/membershipPlans")
public class MembershipPlanController {
    private final MembershipPlanService membershipPlanService;

    public MembershipPlanController(MembershipPlanService membershipPlanService) {
        this.membershipPlanService = membershipPlanService;
    }

    @RequiresRole({"ADMIN"})
    @GetMapping
    public ResponseEntity<Object> getMembershipPlans(
            @RequestParam(required = false) String businessId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            MembershipPlanFilter filter = new MembershipPlanFilter(offset, limit);
            filter.setBusinessId(businessId);
            filter.setUserId(userId);
            filter.setIsActive(isActive);

            List<MembershipPlan> membershipPlans = membershipPlanService.getMembershipPlanList(filter);
            return new ResponseEntity<>(membershipPlans, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequiresRole({"ADMIN", "BUSINESS_OWNER", "BUSINESS_MANAGER", "TRAINER", "STAFF", "CLIENT"})
    @GetMapping("/businesses/{businessId}/membershipPlans")
    public ResponseEntity<Object> getBusinessMembershipPlans(
            @RequestParam(required = false) String businessId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            MembershipPlanFilter filter = new MembershipPlanFilter(offset, limit);
            filter.setBusinessId(businessId);
            filter.setUserId(userId);
            filter.setIsActive(isActive);
            
            List<MembershipPlan> membershipPlans = membershipPlanService.getMembershipPlanList(filter);
            return new ResponseEntity<>(membershipPlans, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequiresRole({"ADMIN"})
    @GetMapping("/businesses/{businessId}/membershipPlans{id}")
    public ResponseEntity<Object> getMembershipPlanById(@PathVariable String id) {
        try {
            MembershipPlan membershipPlan = membershipPlanService.getMembershipPlanById(id);
            return new ResponseEntity<>(membershipPlan, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Membership plan not found", HttpStatus.NOT_FOUND);
        }
    }

    @RequiresRole({"ADMIN", "BUSINESS_OWNER", "BUSINESS_MANAGER"})
    @PostMapping("/businesses/{businessId}/membershipPlans")
    public ResponseEntity<Object> createMembershipPlan(@RequestBody MembershipPlan membershipPlan) {
        try {
            MembershipPlan createdMembershipPlan = membershipPlanService.createMembershipPlan(membershipPlan);
            return new ResponseEntity<>(createdMembershipPlan, HttpStatus.CREATED);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequiresRole({"ADMIN"})
    @PutMapping("/businesses/{businessId}/membershipPlans/{id}")
    public ResponseEntity<Object> updateMembershipPlan(@PathVariable String id, @RequestBody MembershipPlan membershipPlan) {
        try {
            membershipPlan.setId(id);
            membershipPlanService.updateMembershipPlan(membershipPlan);
            return new ResponseEntity<>("Membership plan updated successfully", HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequiresRole({"ADMIN"})
    @DeleteMapping("/businesses/{businessId}/membershipPlans/{id}")
    public ResponseEntity<Object> deleteMembershipPlan(@PathVariable String id) {
        try {
            membershipPlanService.deleteMembershipPlan(id);
            return new ResponseEntity<>("Membership plan deleted successfully", HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
