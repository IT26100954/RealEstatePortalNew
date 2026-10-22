package com.realestate.moving;

import java.math.BigDecimal;
import java.time.*;

import com.fasterxml.jackson.annotation.JsonFormat;

// Immutable data model. Ownership and validation belong in the service.
public record MovingCompany(
        String id,
        String companyName,
        String email,
        String phone,
        String serviceAreas,
        String description,
        String availability
) {
}
