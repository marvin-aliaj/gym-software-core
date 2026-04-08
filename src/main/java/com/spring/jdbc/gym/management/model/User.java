package com.spring.jdbc.gym.management.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.spring.jdbc.gym.management.model.enums.USER_ROLE;
import com.spring.jdbc.gym.management.model.enums.USER_STATUS;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String gender;
    private String email;
    private String password;
    private String phone;
    private USER_ROLE role;
    private USER_STATUS status;
    private List<Business> businesses;
    private String enrollment_date;
    @JsonProperty
    private String cDate;
    @JsonProperty
    private String mDate;
}
