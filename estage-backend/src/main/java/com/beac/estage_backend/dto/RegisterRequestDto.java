package com.beac.estage_backend.dto;

import lombok.Data;

@Data
public class RegisterRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}