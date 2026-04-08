package com.spring.jdbc.gym.management.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.spring.jdbc.gym.management.model.enums.BUSINESS_TYPE;
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
public class Business {
    private String id;
    private String name;
    private BUSINESS_TYPE type;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private boolean isActive;
    @JsonProperty
    private String cDate;
    @JsonProperty
    private String mDate;
}
