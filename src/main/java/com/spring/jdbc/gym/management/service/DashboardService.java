package com.spring.jdbc.gym.management.service;

import com.spring.jdbc.gym.management.dao.DashboardDao;
import com.spring.jdbc.gym.management.exception.CustomException;
import com.spring.jdbc.gym.management.model.*;
import com.spring.jdbc.gym.management.model.filter.DashboardFilter;
import org.springframework.stereotype.Service;


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
            
            // Check-ins today vs previous period
            MetricComparison checkInsComparison = dashboardDao.getCheckInsForPeriod(filter);
            metrics.setCheckInsToday(checkInsComparison);

            // Revenue comparison
//            MetricComparison revenueComparison = dashboardDao.getRevenueForPeriod(filter);
//            metrics.setRevenue(revenueComparison);
            
            // Total clients comparison
            MetricComparison clientComparison = dashboardDao.getTotalClientsForPeriod(filter);
            metrics.setTotalClients(clientComparison);

            // New clients
//            metrics.setNewClients(dashboardDao.getNewClients(filter));
            
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
