package com.example.petsapi.service;

import com.example.petsapi.dto.PetRequest;
import com.example.petsapi.dto.PetResponse;
import com.example.petsapi.exception.PetNotFoundException;
import com.example.petsapi.model.Pet;
import com.example.petsapi.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceImplTest {

    @Mock
    private PetRepository repository;

    @InjectMocks
    private PetServiceImpl service;

    private Pet savedPet;
    private PetRequest validRequest;

    @BeforeEach
    void setUp() {
        savedPet = new Pet(1L, "Buddy", "Dog", 3, "Alice");
        validRequest = new PetRequest("Buddy", "Dog", 3, "Alice");
    }

    @Test
    void create_savesAndReturnsPet() {
        when(repository.save(any())).thenReturn(savedPet);

        PetResponse response = service.create(validRequest);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Buddy");
        verify(repository).save(any());
    }

    @Test
    void update_existingPet_returnsUpdatedPet() {
        PetRequest updateRequest = new PetRequest("Max", "Cat", 5, "Bob");
        Pet updatedPet = new Pet(1L, "Max", "Cat", 5, "Bob");

        when(repository.existsById(1L)).thenReturn(true);
        when(repository.save(any())).thenReturn(updatedPet);

        PetResponse response = service.update(1L, updateRequest);

        assertThat(response.name()).isEqualTo("Max");
        assertThat(response.species()).isEqualTo("Cat");
    }

    @Test
    void update_nonExistingPet_throwsPetNotFoundException() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.update(99L, validRequest))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void findById_existingPet_returnsPet() {
        when(repository.findById(1L)).thenReturn(Optional.of(savedPet));

        PetResponse response = service.findById(1L);

        assertThat(response.id()).isEqualTo(1L);
    }

    @Test
    void findById_nonExistingPet_throwsPetNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(PetNotFoundException.class);
    }

    @Test
    void findAll_returnsMappedList() {
        when(repository.findAll()).thenReturn(List.of(savedPet, new Pet(2L, "Luna", "Cat", 1, null)));

        List<PetResponse> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(PetResponse::name).containsExactly("Buddy", "Luna");
    }

    @Test
    void delete_existingPet_deletesSuccessfully() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_nonExistingPet_throwsPetNotFoundException() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(PetNotFoundException.class);

        verify(repository, never()).deleteById(any());
    }
}
