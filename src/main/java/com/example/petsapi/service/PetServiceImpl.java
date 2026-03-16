package com.example.petsapi.service;

import com.example.petsapi.dto.PetRequest;
import com.example.petsapi.dto.PetResponse;
import com.example.petsapi.exception.PetNotFoundException;
import com.example.petsapi.model.Pet;
import com.example.petsapi.repository.PetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetServiceImpl implements PetService {

    private final PetRepository repository;

    public PetServiceImpl(PetRepository repository) {
        this.repository = repository;
    }

    @Override
    public PetResponse create(PetRequest request) {
        Pet pet = new Pet(null, request.name(), request.species(), request.age(), request.ownerName());
        return PetResponse.from(repository.save(pet));
    }

    @Override
    public PetResponse update(Long id, PetRequest request) {
        if (!repository.existsById(id)) {
            throw new PetNotFoundException(id);
        }
        Pet updated = new Pet(id, request.name(), request.species(), request.age(), request.ownerName());
        return PetResponse.from(repository.save(updated));
    }

    @Override
    public PetResponse findById(Long id) {
        return repository.findById(id)
                .map(PetResponse::from)
                .orElseThrow(() -> new PetNotFoundException(id));
    }

    @Override
    public List<PetResponse> findAll() {
        return repository.findAll().stream()
                .map(PetResponse::from)
                .toList();
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new PetNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
