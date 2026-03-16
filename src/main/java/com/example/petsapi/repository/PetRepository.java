package com.example.petsapi.repository;

import com.example.petsapi.model.Pet;

import java.util.List;
import java.util.Optional;

/**
 * Storage abstraction for pets.
 *
 * Defining persistence as an interface is the main preparation for the planned
 * migration to a non-relational database: a new implementation (e.g. MongoDBPetRepository)
 * can be dropped in without touching any service or controller code.
 */
public interface PetRepository {

    Pet save(Pet pet);

    Optional<Pet> findById(Long id);

    List<Pet> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);
}
