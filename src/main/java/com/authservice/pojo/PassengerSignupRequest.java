package com.authservice.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PassengerSignupRequest {
    private String email;
    private String password;
    private String phoneNumber;
    private String name;
}
