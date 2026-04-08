package com.spring.jdbc.gym.management.model.enums;

import lombok.Getter;

@Getter
public enum RESOURCE_TYPE {
    USER("USER"),
    BUSINESS ("BUSINESS");
    
    private final String value;

    RESOURCE_TYPE(String value) {
        this.value = value;
    }

}
