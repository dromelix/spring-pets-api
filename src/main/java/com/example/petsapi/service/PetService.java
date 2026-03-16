package com.example.petsapi.service;

import com.example.petsapi.dto.PetRequest;
import com.example.petsapi.dto.PetResponse;

import java.util.List;

public interface PetService {

    PetResponse create(PetRequest request);

    PetResponse update(Long id, PetRequest request);

    PetResponse findById(Long id);

    List<PetResponse> findAll();

    void delete(Long id);
}
