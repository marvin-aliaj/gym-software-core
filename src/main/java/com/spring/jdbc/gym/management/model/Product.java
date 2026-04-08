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
public class Product {
    private String id;
    private String title;
    private String description;
    private int stock;
    private long price;
    private String categoryId;
    private String businessId;
    @JsonProperty
    private String cDate;
    @JsonProperty
    private String mDate;
}
