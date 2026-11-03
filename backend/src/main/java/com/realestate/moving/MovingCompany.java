package com.realestate.moving;

// The service validates this immutable stored-record representation.
public record MovingCompany(
        String id,
        String companyName,
        String email,
        String phone,
        String serviceAreas,
        String description,
        String availability
) { }
