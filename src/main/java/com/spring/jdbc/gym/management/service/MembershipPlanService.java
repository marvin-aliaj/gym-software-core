package com.spring.jdbc.gym.management.service;

import com.spring.jdbc.gym.management.dao.MembershipPlanDao;
import com.spring.jdbc.gym.management.exception.CustomException;
import com.spring.jdbc.gym.management.model.MembershipPlan;
import com.spring.jdbc.gym.management.model.filter.MembershipPlanFilter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembershipPlanService {
    private final MembershipPlanDao membershipPlanDao;

    public MembershipPlanService(MembershipPlanDao membershipPlanDao) {
        this.membershipPlanDao = membershipPlanDao;
    }

    public List<MembershipPlan> getMembershipPlanList(MembershipPlanFilter filter) throws Exception {
        try {
            return membershipPlanDao.getMembershipPlanList(filter);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public MembershipPlan getMembershipPlanById(String id) throws Exception {
        try {
            return membershipPlanDao.getMembershipPlanById(id);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public MembershipPlan createMembershipPlan(MembershipPlan membershipPlan) throws Exception {
        try {
            return membershipPlanDao.createMembershipPlan(membershipPlan);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public void updateMembershipPlan(MembershipPlan membershipPlan) throws Exception {
        try {
            membershipPlanDao.updateMembershipPlan(membershipPlan);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public void deleteMembershipPlan(String id) throws Exception {
        try {
            membershipPlanDao.deleteMembershipPlan(id);
        } catch (Exception e) {
            throw new CustomException();
        }
    }
}
