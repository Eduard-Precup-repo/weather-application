package com.example.WheaterApp.cities;

import lombok.Getter;
import lombok.Setter;

//@Getter
//@Setter
public class CityRequest {
    private String name;
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

