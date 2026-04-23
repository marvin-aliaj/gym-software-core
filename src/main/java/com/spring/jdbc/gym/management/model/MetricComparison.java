package com.spring.jdbc.gym.management.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetricComparison {
    private Long current;
    private Long previous;
    private Double percentChange;

    public MetricComparison(Long current, Long previous) {
        this.current = current;
        this.previous = previous;
        this.percentChange = calculatePercentChange(current, previous);
    }

    private Double calculatePercentChange(Long current, Long previous) {
        if (previous == null || previous == 0) {
            return current != null && current > 0 ? 100.0 : 0.0;
        }
        if (current == null) {
            return -100.0;
        }
        return ((current - previous) * 100.0) / previous;
    }
}
