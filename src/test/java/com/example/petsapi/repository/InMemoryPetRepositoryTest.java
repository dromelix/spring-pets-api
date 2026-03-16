package com.example.petsapi.repository;

import com.example.petsapi.model.Pet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryPetRepositoryTest {

    private InMemoryPetRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPetRepository();
    }

    @Test
    void save_newPet_assignsIdAndPersists() {
        Pet pet = new Pet(null, "Buddy", "Dog", 3, "Alice");

        Pet saved = repository.save(pet);

        assertThat(saved.id()).isNotNull();
        assertThat(saved.name()).isEqualTo("Buddy");
    }

    @Test
    void save_existingPet_updatesEntry() {
        Pet created = repository.save(new Pet(null, "Buddy", "Dog", 3, "Alice"));
        Pet updated = repository.save(new Pet(created.id(), "Max", "Cat", 5, "Bob"));

        assertThat(updated.name()).isEqualTo("Max");
        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void findById_existingId_returnsPet() {
        Pet saved = repository.save(new Pet(null, "Luna", "Rabbit", 1, null));

        Optional<Pet> found = repository.findById(saved.id());

        assertThat(found).isPresent();
        assertThat(found.get().name()).isEqualTo("Luna");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        assertThat(repository.findById(999L)).isEmpty();
    }

    @Test
    void findAll_returnsAllPets() {
        repository.save(new Pet(null, "Buddy", "Dog", 3, null));
        repository.save(new Pet(null, "Luna", "Cat", 2, null));

        List<Pet> all = repository.findAll();

        assertThat(all).hasSize(2);
    }

    @Test
    void deleteById_removesEntry() {
        Pet saved = repository.save(new Pet(null, "Buddy", "Dog", 3, null));

        repository.deleteById(saved.id());

        assertThat(repository.existsById(saved.id())).isFalse();
    }

    @Test
    void existsById_returnsCorrectly() {
        Pet saved = repository.save(new Pet(null, "Buddy", "Dog", 3, null));

        assertThat(repository.existsById(saved.id())).isTrue();
        assertThat(repository.existsById(999L)).isFalse();
    }

    @Test
    void save_multiplePets_assignsUniqueIds() {
        Pet first = repository.save(new Pet(null, "Buddy", "Dog", 3, null));
        Pet second = repository.save(new Pet(null, "Luna", "Cat", 2, null));

        assertThat(first.id()).isNotEqualTo(second.id());
    }
}
