package com.example.WheaterApp.registration;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

//@Getter
@EqualsAndHashCode
//@AllArgsConstructor
@ToString
public class RegistrationRequest {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;

    public RegistrationRequest(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
