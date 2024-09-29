package com.authservice.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PassengerSigninRequestDto {
    private String email;
    private String password;
}
