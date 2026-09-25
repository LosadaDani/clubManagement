package com.managementClub.managementClub.service.impl;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.managementClub.managementClub.exception.InvalidBusinessRuleException;
import com.managementClub.managementClub.exception.ResourceNotFoundException;
import com.managementClub.managementClub.mapper.AppUserMapper;
import com.managementClub.managementClub.model.dto.AppUserCreatedResponseDTO;
import com.managementClub.managementClub.model.dto.AppUserRequestDTO;
import com.managementClub.managementClub.model.entity.AppUser;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.enums.Role;
import com.managementClub.managementClub.repository.AppUserRepository;
import com.managementClub.managementClub.repository.PersonRepository;
import com.managementClub.managementClub.service.AppUserService;

@Service
public class AppUserServiceImpl implements AppUserService{

    private final PersonRepository personRepository;
    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;

    public AppUserServiceImpl(AppUserRepository appUserRepository, PersonRepository personRepository, AppUserMapper appUserMapper, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.personRepository = personRepository;
        this.appUserMapper = appUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AppUserCreatedResponseDTO registerUser(AppUserRequestDTO appUserDto) {

        Person person = personRepository.findById(appUserDto.getPersonId())
            .orElseThrow(() -> new ResourceNotFoundException("La persona indicada no existe"));
        
        if (appUserRepository.existsByPerson(person)){
            throw new InvalidBusinessRuleException("La persona indicada ya tiene un usuario asignado");
        }

        String plainPassword = UUID.randomUUID().toString().substring(0, 12);

        String hashPassword = passwordEncoder.encode(plainPassword);
	    AppUser savedAppUser = appUserRepository.save(appUserMapper.toEntity(appUserDto, person, hashPassword, Role.ROLE_USER));
        
        return appUserMapper.toCreatedResponseDto(savedAppUser, plainPassword);
    }

    

}
