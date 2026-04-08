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
public class Order {
    private String id;
    private String businessId;
    private String userId;
    private String shoppingCartId;
    private long totalAmount;
    private String address;
    @JsonProperty
    private String cDate;
    @JsonProperty
    private String mDate;
}
