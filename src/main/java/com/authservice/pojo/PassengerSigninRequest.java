package com.authservice.pojo;

import lombok.Data;

@Data
public class PassengerSigninRequest {
    private String email;
    private String password;
}