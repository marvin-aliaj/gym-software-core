package com.spring.jdbc.gym.management.service;

import com.spring.jdbc.gym.management.dao.BusinessDao;
import com.spring.jdbc.gym.management.exception.CustomException;
import com.spring.jdbc.gym.management.model.Business;
import com.spring.jdbc.gym.management.model.filter.BusinessFilter;
import com.spring.jdbc.gym.management.utils.AESUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BusinessService {
    private final BusinessDao businessDao;
    private final AESUtil aesUtil;

    public BusinessService (BusinessDao businessDao, AESUtil aesUtil) {
        this.businessDao = businessDao;
        this.aesUtil = aesUtil;
    }

    public List<Business> getBusinessList(BusinessFilter filter) throws Exception {
        try {
            return businessDao.getBusinessList (filter);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Business createBusiness(Business business) throws Exception {
        try {
            return businessDao.createGym(business);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBusiness(Business business) throws Exception {
        try {
            businessDao.updateGym(business);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public void deleteBusiness(String businessId) throws Exception {
        try {
            businessDao.deleteGym(businessId);
        } catch (Exception e) {
            throw new CustomException();
        }
    }
}