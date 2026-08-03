package com.managementClub.managementClub.controller;

import com.managementClub.managementClub.controller.documentation.DogControllerDocs;
import com.managementClub.managementClub.model.dto.DogRequestDTO;
import com.managementClub.managementClub.model.dto.DogResponseDTO;
import com.managementClub.managementClub.service.DogService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dogs")
@Validated
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

    @GetMapping("/person/{personId}")
    @Override
    public ResponseEntity<List<DogResponseDTO>> getDogsByPersonId(@PathVariable Long personId) {
        List<DogResponseDTO> response = dogService.getDogsByPersonId(personId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/microchip/{microchip}")
    @Override
    public ResponseEntity<DogResponseDTO> getDogByMicrochip(@PathVariable String microchip) {

        DogResponseDTO dog = dogService.getDogByMicrochip(microchip);

        return ResponseEntity.ok(dog);
    }

    @GetMapping("/name/{name}")
    @Override
    public ResponseEntity<List<DogResponseDTO>> getDogByName(@PathVariable @NotBlank String name) {
        List<DogResponseDTO> response = dogService.getDogByName(name);

        return ResponseEntity.ok(response);
    }


    @GetMapping
    @Override
    public ResponseEntity<List<DogResponseDTO>> getAllDogs() {

        List<DogResponseDTO> response = dogService.getAllDogs();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @Override
    public ResponseEntity<DogResponseDTO> updateDog(@PathVariable Long id, @Valid @RequestBody DogRequestDTO requestDto) {
        DogResponseDTO responseDto = dogService.updateDog(id, requestDto);

        return ResponseEntity.ok(responseDto);
    }
}
