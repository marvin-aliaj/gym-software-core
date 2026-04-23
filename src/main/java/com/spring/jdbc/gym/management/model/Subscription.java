package com.spring.jdbc.gym.management.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Subscription {
    private String id;
    private User user;
    private MembershipPlan plan;
    private String businessId;
    private String startDate;
    private String endDate;
    private boolean isActive;
    @JsonProperty
    private String cDate;
    @JsonProperty
    private String mDate;
}
