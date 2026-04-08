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
public class MembershipPlan {
    private String id;
    private String gymId;
    private String planName;
    private long amountCents;
    private int durationDays;
    private String description;
    private boolean isActive;
    @JsonProperty
    private String cDate;
    @JsonProperty
    private String mDate;
}
