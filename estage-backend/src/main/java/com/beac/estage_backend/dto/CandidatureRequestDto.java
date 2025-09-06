package com.beac.estage_backend.dto;

import lombok.Data;

@Data
public class CandidatureRequestDto {
    private String offerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String nationality;
    private String university;
    private String fieldOfStudy;
    private String studyLevel;
}