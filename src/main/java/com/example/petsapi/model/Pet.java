package com.example.petsapi.model;

/**
 * Core domain entity representing a pet.
 *
 * Intentionally kept as a plain Java record rather than a JPA entity so that
 * switching the persistence layer (relational → NoSQL) requires changes only
 * in the repository package, leaving the domain model untouched.
 */
public record Pet(
        Long id,
        String name,
        String species,
        Integer age,
        String ownerName
) {}
