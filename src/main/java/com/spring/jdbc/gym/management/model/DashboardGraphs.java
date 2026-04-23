package com.spring.jdbc.gym.management.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardGraphs {
    private List<TimeSeriesData> revenueTimeSeries;
    private List<TimeSeriesData> subscriptions;
    private List<TimeSeriesData> rushHours;
    private List<TimeSeriesData> clientGrowth;
}
