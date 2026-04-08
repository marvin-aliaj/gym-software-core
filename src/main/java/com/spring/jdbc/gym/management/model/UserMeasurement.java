package com.spring.jdbc.gym.management.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserMeasurement {
    private String id;
    private String userId;
    private BigDecimal weight;
    private BigDecimal height;
    private BigDecimal bodyFatPercent;
    private BigDecimal muscleMassKg;
    @JsonProperty
    private String cDate;
    @JsonProperty
    private String mDate;
}
