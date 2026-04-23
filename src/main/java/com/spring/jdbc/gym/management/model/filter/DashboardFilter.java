package com.spring.jdbc.gym.management.model.filter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class DashboardFilter {
    private static final int DEFAULT_PERIOD_DAYS = 7;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private int period = DEFAULT_PERIOD_DAYS;
    private String businessId;
    private String startDate;
    private String endDate;
    
    // Calculated fields
    private String previousStartDate;
    private String previousEndDate;

    public DashboardFilter(int period, String businessId, String startDate, String endDate) {
        this.period = period > 0 ? period : DEFAULT_PERIOD_DAYS;
        this.businessId = businessId;
        
        // If custom dates provided, use them; otherwise calculate from period
        if (startDate != null && endDate != null) {
            this.startDate = startDate;
            this.endDate = endDate;
            calculatePeriodFromDates();
        } else {
            calculateDatesFromPeriod();
        }
        
        calculatePreviousPeriod();
    }

    public DashboardFilter(int period, String businessId) {
        this(period, businessId, null, null);
    }

    private void calculateDatesFromPeriod() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(period - 1);
        
        this.endDate = end.format(DATE_FORMATTER);
        this.startDate = start.format(DATE_FORMATTER);
    }

    private void calculatePeriodFromDates() {
        LocalDate start = LocalDate.parse(this.startDate, DATE_FORMATTER);
        LocalDate end = LocalDate.parse(this.endDate, DATE_FORMATTER);
        this.period = (int) java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
    }

    private void calculatePreviousPeriod() {
        LocalDate start = LocalDate.parse(this.startDate, DATE_FORMATTER);
        LocalDate previousEnd = start.minusDays(1);
        LocalDate previousStart = previousEnd.minusDays(period - 1);
        
        this.previousEndDate = previousEnd.format(DATE_FORMATTER);
        this.previousStartDate = previousStart.format(DATE_FORMATTER);
    }

    public boolean hasBusinessId() {
        return businessId != null && !businessId.trim().isEmpty();
    }

    public String getStartDate() {
        if (startDate == null) {
            calculateDatesFromPeriod();
        }
        return startDate;
    }

    public String getEndDate() {
        if (endDate == null) {
            calculateDatesFromPeriod();
        }
        return endDate;
    }

    public String getPreviousStartDate() {
        if (previousStartDate == null) {
            calculatePreviousPeriod();
        }
        return previousStartDate;
    }

    public String getPreviousEndDate() {
        if (previousEndDate == null) {
            calculatePreviousPeriod();
        }
        return previousEndDate;
    }
}
