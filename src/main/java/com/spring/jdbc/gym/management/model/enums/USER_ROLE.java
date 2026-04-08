package com.spring.jdbc.gym.management.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Objects;

@Getter
public enum USER_ROLE {
    ADMIN(1, "Admin"),
    // ALL PERMISSIONS
    BUSINESS_OWNER(2, "Business_Owner"),
    // HAS ALL PERMISSIONS LIMITED ONLY TO HIS BUSINESS
    BUSINESS_MANAGER(3, "Business_Manager"),
    // HAS ALL PERMISSIONS LIMITED ONLY TO HIS BUSINESS
    TRAINER(4, "Trainer"),
    // CAN VIEW CLIENTS, CAN VIEW AND EDIT HIS TRAINING PLANS,
    STAFF(5, "Staff"),
    // CAN VIEW CLIENTS
    CLIENT(6, "Client");

    private final int code;
    private final String description;

    USER_ROLE(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static USER_ROLE fromCode(int code) {
        for (USER_ROLE type : USER_ROLE.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid USER_TYPE code: " + code);
    }


    public static USER_ROLE fromDescription(String description) {
        for (USER_ROLE type : USER_ROLE.values()) {
            if (Objects.equals(type.getDescription(), description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid USER_TYPE description: " + description);
    }

    @JsonCreator
    public static USER_ROLE fromValue(String value) {
        for (USER_ROLE role : USER_ROLE.values()) {
            if (role.description.equalsIgnoreCase(value) ||
                    String.valueOf(role.code).equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }

    @Override
    public String toString() {
        return description;
    }
}
