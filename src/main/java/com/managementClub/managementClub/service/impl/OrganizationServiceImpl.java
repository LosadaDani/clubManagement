package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.exception.ResourceAlreadyExistsException;
import com.managementClub.managementClub.exception.ResourceNotFoundException;
import com.managementClub.managementClub.mapper.OrganizationMapper;
import com.managementClub.managementClub.model.dto.OrganizationRequestDTO;
import com.managementClub.managementClub.model.dto.OrganizationResponseDTO;
import com.managementClub.managementClub.model.entity.Organization;
import com.managementClub.managementClub.repository.OrganizationRepository;
import com.managementClub.managementClub.service.OrganizationService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository, OrganizationMapper organizationMapper) {
        this.organizationRepository = organizationRepository;
        this.organizationMapper = organizationMapper;
    }

    @Override
    public OrganizationResponseDTO createOrganization(OrganizationRequestDTO organizationRequestDTO) {

        if (organizationRepository.findByShortName(organizationRequestDTO.getShortName()).isPresent()){
                throw new ResourceAlreadyExistsException("La abreviatura de la organización ya existe.");
        }

        Organization organization = organizationMapper.toEntity(organizationRequestDTO);
        Organization savedOrganization = organizationRepository.save(organization);
        return organizationMapper.toResponseDto(savedOrganization);
    }

    @Override
    public OrganizationResponseDTO updateOrganization(Long id, OrganizationRequestDTO organizationRequestDTO) {

        Organization existingOrganization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organización con identificador " + id + " no encontrada."));

        if (!Objects.equals(existingOrganization.getShortName(), organizationRequestDTO.getShortName())) {
            organizationRepository.findByShortName(organizationRequestDTO.getShortName())
                    .ifPresent(o -> {
                        throw new ResourceAlreadyExistsException("La abreviatura de la organización ya existe.");
                    });
        }

        organizationMapper.updateEntity(existingOrganization, organizationRequestDTO);

        Organization updatedOrganization = organizationRepository.save(existingOrganization);

        return organizationMapper.toResponseDto(updatedOrganization);
    }
}
