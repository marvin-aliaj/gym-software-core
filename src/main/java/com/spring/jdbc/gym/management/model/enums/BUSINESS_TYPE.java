package com.spring.jdbc.gym.management.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Objects;

@Getter
public enum BUSINESS_TYPE {
    GYM(1, "Gym"),
    SHOP(2, "Shop");

    private final int code;
    private final String description;

    BUSINESS_TYPE(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static BUSINESS_TYPE fromCode(int code) {
        for (BUSINESS_TYPE type : BUSINESS_TYPE.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid BUSINESS_TYPE code: " + code);
    }

    public static BUSINESS_TYPE fromDescription(String description) {
        for (BUSINESS_TYPE type : BUSINESS_TYPE.values()) {
            if (Objects.equals(type.getDescription(), description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid BUSINESS_TYPE description: " + description);
    }

    @JsonCreator
    public static BUSINESS_TYPE fromValue(String value) {
        for (BUSINESS_TYPE type : BUSINESS_TYPE.values()) {
            if (type.description.equalsIgnoreCase(value) ||
                    String.valueOf(type.code).equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown business type: " + value);
    }

    @Override
    public String toString() {
        return description;
    }
}
