package com.example.petsapi.controller;

import com.example.petsapi.dto.PetRequest;
import com.example.petsapi.dto.PetResponse;
import com.example.petsapi.exception.PetNotFoundException;
import com.example.petsapi.service.PetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PetController.class)
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PetService petService;

    private static final PetResponse BUDDY = new PetResponse(1L, "Buddy", "Dog", 3, "Alice");

    @Test
    void POST_pets_validRequest_returns201() throws Exception {
        when(petService.create(any())).thenReturn(BUDDY);

        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PetRequest("Buddy", "Dog", 3, "Alice"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Buddy"))
                .andExpect(jsonPath("$.species").value("Dog"));
    }

    @Test
    void POST_pets_missingName_returns400WithValidationError() throws Exception {
        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PetRequest("", "Dog", 2, null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"));
    }

    @Test
    void POST_pets_negativeAge_returns400WithValidationError() throws Exception {
        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PetRequest("Buddy", "Dog", -1, null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.age").exists());
    }

    @Test
    void PUT_pets_existingId_returns200() throws Exception {
        PetResponse updated = new PetResponse(1L, "Max", "Cat", 5, "Bob");
        when(petService.update(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/pets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PetRequest("Max", "Cat", 5, "Bob"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Max"));
    }

    @Test
    void PUT_pets_nonExistingId_returns404() throws Exception {
        when(petService.update(eq(99L), any())).thenThrow(new PetNotFoundException(99L));

        mockMvc.perform(put("/api/pets/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PetRequest("Max", "Cat", 5, "Bob"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Pet Not Found"));
    }

    @Test
    void GET_pets_id_existingPet_returns200() throws Exception {
        when(petService.findById(1L)).thenReturn(BUDDY);

        mockMvc.perform(get("/api/pets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ownerName").value("Alice"));
    }

    @Test
    void GET_pets_id_nonExistingPet_returns404() throws Exception {
        when(petService.findById(99L)).thenThrow(new PetNotFoundException(99L));

        mockMvc.perform(get("/api/pets/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void GET_pets_returnsList() throws Exception {
        when(petService.findAll()).thenReturn(List.of(BUDDY, new PetResponse(2L, "Luna", "Cat", 1, null)));

        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Buddy"))
                .andExpect(jsonPath("$[1].name").value("Luna"));
    }

    @Test
    void DELETE_pets_existingId_returns204() throws Exception {
        doNothing().when(petService).delete(1L);

        mockMvc.perform(delete("/api/pets/1"))
                .andExpect(status().isNoContent());

        verify(petService).delete(1L);
    }

    @Test
    void DELETE_pets_nonExistingId_returns404() throws Exception {
        doThrow(new PetNotFoundException(99L)).when(petService).delete(99L);

        mockMvc.perform(delete("/api/pets/99"))
                .andExpect(status().isNotFound());
    }
}
