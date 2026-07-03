package com.managementClub.managementClub.controller;

import com.managementClub.managementClub.model.dto.PersonRequestDTO;
import com.managementClub.managementClub.model.dto.PersonResponseDTO;
import com.managementClub.managementClub.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService personService;


    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    public ResponseEntity<PersonResponseDTO> createPerson (@Valid @RequestBody PersonRequestDTO requestDto) {

        PersonResponseDTO responseDto = personService.createPerson(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

}
