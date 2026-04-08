package com.spring.jdbc.gym.management.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Objects;

@Getter
public enum USER_STATUS {
    INACTIVE(0, "Inactive"),
    ACTIVE(1, "Active"),
    DELETED(2, "Deleted");

    private final int code;
    private final String description;

    USER_STATUS(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static USER_STATUS fromCode(int code) {
        for (USER_STATUS type : USER_STATUS.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid USER_TYPE code: " + code);
    }

    public static USER_STATUS fromDescription(String description) {
        for (USER_STATUS type : USER_STATUS.values()) {
            if (Objects.equals(type.getDescription(), description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid USER_STATUS description: " + description);
    }

    @JsonCreator
    public static USER_STATUS fromValue(String value) {
        for (USER_STATUS status : USER_STATUS.values()) {
            if (status.description.equalsIgnoreCase(value) ||
                    String.valueOf(status.code).equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }

    @Override
    public String toString() {
        return description;
    }
}
