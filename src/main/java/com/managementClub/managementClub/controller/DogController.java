package com.managementClub.managementClub.controller;

import com.managementClub.managementClub.controller.documentation.DogControllerDocs;
import com.managementClub.managementClub.model.dto.DogRequestDTO;
import com.managementClub.managementClub.model.dto.DogResponseDTO;
import com.managementClub.managementClub.service.DogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dogs")
public class DogController implements DogControllerDocs {

    private final DogService dogService;

    public DogController(DogService dogService) {
        this.dogService = dogService;
    }

    @PostMapping
    @Override
    public ResponseEntity<DogResponseDTO> createDog(@Valid @RequestBody DogRequestDTO requestDto) {

        DogResponseDTO dog = dogService.createDog(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(dog);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<DogResponseDTO> getDog(@PathVariable Long id) {

        DogResponseDTO dog = dogService.getDogById(id);

        return ResponseEntity.ok(dog);
    }

    @GetMapping
    @Override
    public ResponseEntity<List<DogResponseDTO>> getAllDogs() {

        List<DogResponseDTO> response = dogService.getAllDogs();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/person/{personId}")
    @Override
    public ResponseEntity<List<DogResponseDTO>> getDogsByPersonId(@PathVariable Long personId) {
        List<DogResponseDTO> response = dogService.getDogsByPersonId(personId);

        return ResponseEntity.ok(response);
    }
}
