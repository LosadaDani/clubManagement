package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.exception.ResourceAlreadyExistsException;
import com.managementClub.managementClub.mapper.OrganizationMapper;
import com.managementClub.managementClub.model.dto.OrganizationRequestDTO;
import com.managementClub.managementClub.model.dto.OrganizationResponseDTO;
import com.managementClub.managementClub.model.entity.Organization;
import com.managementClub.managementClub.repository.OrganizationRepository;
import com.managementClub.managementClub.service.OrganizationService;
import org.springframework.stereotype.Service;

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
}
