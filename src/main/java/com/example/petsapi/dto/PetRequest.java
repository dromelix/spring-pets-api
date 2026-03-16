package com.example.petsapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Inbound payload for create and update operations.
 * Validation is enforced here so the domain model stays free of web-layer concerns.
 */
public record PetRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Species is required")
        String species,

        @Min(value = 0, message = "Age must be 0 or greater")
        Integer age,

        String ownerName
) {}
