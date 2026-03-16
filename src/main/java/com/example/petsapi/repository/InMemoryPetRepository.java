package com.example.petsapi.repository;

import com.example.petsapi.model.Pet;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory mock of a relational repository.
 *
 * This implementation simulates what a JPA/JDBC repository would do.
 * Replacing it with a real database requires only providing a new bean
 * that implements {@link PetRepository} — no other code changes are needed.
 */
@Repository
public class InMemoryPetRepository implements PetRepository {

    private final Map<Long, Pet> store = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    @Override
    public Pet save(Pet pet) {
        if (pet.id() == null) {
            // Create: assign a new generated ID
            Pet persisted = new Pet(idSequence.getAndIncrement(), pet.name(), pet.species(), pet.age(), pet.ownerName());
            store.put(persisted.id(), persisted);
            return persisted;
        }
        // Update: overwrite the existing entry
        store.put(pet.id(), pet);
        return pet;
    }

    @Override
    public Optional<Pet> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Pet> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return store.containsKey(id);
    }
}
