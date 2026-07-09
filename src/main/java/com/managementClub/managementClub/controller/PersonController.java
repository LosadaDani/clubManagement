package com.managementClub.managementClub.controller;

import com.managementClub.managementClub.model.dto.PersonRequestDTO;
import com.managementClub.managementClub.model.dto.PersonResponseDTO;
import com.managementClub.managementClub.model.dto.PersonStatusDTO;
import com.managementClub.managementClub.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService personService;


    public PersonController (PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    public ResponseEntity<PersonResponseDTO> createPerson (@Valid @RequestBody PersonRequestDTO requestDto) {

        PersonResponseDTO responseDto = personService.createPerson(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonResponseDTO> getPerson (@PathVariable Long id) {

        PersonResponseDTO responseDto = personService.getPersonById(id);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PersonResponseDTO>> searchPersons (@RequestParam String searchText) {

        return ResponseEntity.ok(personService.searchPersons(searchText));
    }

    @GetMapping()
    public ResponseEntity<List<PersonResponseDTO>> getAllPersons() {
        List<PersonResponseDTO> response = personService.getAllPersons();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonResponseDTO> updatePerson(@Valid @RequestBody PersonRequestDTO requestDto, @PathVariable Long id) {

        PersonResponseDTO responseDto = personService.updatePerson(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PersonResponseDTO> changeMembershipStatus(@PathVariable Long id, @Valid @RequestBody PersonStatusDTO dto) {

        PersonResponseDTO response = personService.changeMembershipStatus(id, dto);

        return ResponseEntity.ok(response);
    }
}
