package com.example.petsapi.dto;

import com.example.petsapi.model.Pet;

/**
 * Outbound representation of a pet.
 * Decoupling the API contract from the domain model allows either side to evolve independently.
 */
public record PetResponse(
        Long id,
        String name,
        String species,
        Integer age,
        String ownerName
) {
    public static PetResponse from(Pet pet) {
        return new PetResponse(
                pet.id(),
                pet.name(),
                pet.species(),
                pet.age(),
                pet.ownerName()
        );
    }
}
