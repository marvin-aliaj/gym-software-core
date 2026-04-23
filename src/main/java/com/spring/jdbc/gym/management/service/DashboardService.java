package com.spring.jdbc.gym.management.service;

import com.spring.jdbc.gym.management.dao.DashboardDao;
import com.spring.jdbc.gym.management.exception.CustomException;
import com.spring.jdbc.gym.management.model.*;
import com.spring.jdbc.gym.management.model.filter.DashboardFilter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {
    private final DashboardDao dashboardDao;

    public DashboardService(DashboardDao dashboardDao) {
        this.dashboardDao = dashboardDao;
    }

    public DashboardResponse getDashboardMetrics(DashboardFilter filter) throws Exception {
        try {
            DashboardResponse response = new DashboardResponse();
            
            // Set period info
            PeriodInfo periodInfo = new PeriodInfo();
            periodInfo.setDays(filter.getPeriod());
            periodInfo.setStartDate(filter.getStartDate());
            periodInfo.setEndDate(filter.getEndDate());
            periodInfo.setPreviousStartDate(filter.getPreviousStartDate());
            periodInfo.setPreviousEndDate(filter.getPreviousEndDate());
            response.setPeriodInfo(periodInfo);

            // Build metrics
            DashboardMetrics metrics = new DashboardMetrics();
            
            // Active members now
            metrics.setActiveMembersNow(dashboardDao.getActiveMembersNow(filter));
            
            // Check-ins today vs previous period average
            Long checkInsToday = dashboardDao.getCheckInsForPeriod(filter, 
                java.time.LocalDate.now().toString(), 
                java.time.LocalDate.now().toString());
            Long checkInsPrevious = dashboardDao.getCheckInsForPeriod(filter, 
                filter.getPreviousStartDate(), 
                filter.getPreviousEndDate());
            // Calculate daily average for previous period
            Long checkInsPreviousAvg = checkInsPrevious / filter.getPeriod();
            metrics.setCheckInsToday(new MetricComparison(checkInsToday, checkInsPreviousAvg));
            
            // Revenue comparison
            Long revenueCurrent = dashboardDao.getRevenueForPeriod(filter, 
                filter.getStartDate(), 
                filter.getEndDate());
            Long revenuePrevious = dashboardDao.getRevenueForPeriod(filter, 
                filter.getPreviousStartDate(), 
                filter.getPreviousEndDate());
            metrics.setRevenue(new MetricComparison(revenueCurrent, revenuePrevious));
            
            // Total clients comparison
            Long clientsCurrent = dashboardDao.getTotalClientsForPeriod(filter, filter.getEndDate());
            Long clientsPrevious = dashboardDao.getTotalClientsForPeriod(filter, filter.getPreviousEndDate());
            metrics.setTotalClients(new MetricComparison(clientsCurrent, clientsPrevious));
            
            // New clients
            metrics.setNewClients(dashboardDao.getNewClients(filter));
            
            response.setMetrics(metrics);

            // Build graphs
            DashboardGraphs graphs = new DashboardGraphs();
            graphs.setRevenueTimeSeries(dashboardDao.getRevenueTimeSeries(filter));
            graphs.setSubscriptions(dashboardDao.getSubscriptionTimeSeries(filter));
            graphs.setRushHours(dashboardDao.getRushHours(filter));
            graphs.setClientGrowth(dashboardDao.getClientGrowthTimeSeries(filter));
            response.setGraphs(graphs);

            return response;
        } catch (Exception e) {
            throw new CustomException("Error building dashboard: " + e.getMessage());
        }
    }
}
