package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.exception.ResourceAlreadyExistsException;
import com.managementClub.managementClub.exception.ResourceNotFoundException;
import com.managementClub.managementClub.mapper.OrganizationMapper;
import com.managementClub.managementClub.model.dto.OrganizationRequestDTO;
import com.managementClub.managementClub.model.dto.OrganizationResponseDTO;
import com.managementClub.managementClub.model.entity.Organization;
import com.managementClub.managementClub.repository.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceImplTest {

    @Mock private OrganizationRepository organizationRepository;
    @Spy private OrganizationMapper organizationMapper = new OrganizationMapper();

    @InjectMocks private OrganizationServiceImpl service;

    private static final Long ORGANIZATION_ID = 3L;
    private static final Long SAVED_ORGANIZATION_ID = 77L;
    private static final Long MISSING_ID = 999L;
    private static final String NAME = "Real Sociedad Canina de España";
    private static final String SHORT_NAME = "RSCE";
    private static final String NEW_NAME = "Federación Catalana";
    private static final String NEW_SHORT_NAME = "FCAG";

    // Organización existente: NAME, SHORT_NAME
    private Organization existingOrganization() {
        Organization organization = new Organization(NAME, SHORT_NAME);
        organization.setId(ORGANIZATION_ID);
        return organization;
    }

    //O1 createOrganization CARACTERIZACIÓN (sin regla documentada, ver plan D-O1): abreviatura ya registrada -> ResourceAlreadyExists y no se guarda
    @Test
    void createOrganization_whenShortNameAlreadyExists_throwsResourceAlreadyExists() {
        when(organizationRepository.findByShortName(SHORT_NAME)).thenReturn(Optional.of(new Organization()));

        assertThatThrownBy(() -> service.createOrganization(new OrganizationRequestDTO(NAME, SHORT_NAME)))
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(organizationRepository, never()).save(any(Organization.class));
    }

    //O2 createOrganization Alta válida -> se guardan nombre y abreviatura del DTO y la respuesta sale de lo que devuelve save
    @Test
    void createOrganization_whenValid_savesOrganizationWithRequestDataAndReturnsSavedOrganization() {
        // save devuelve una instancia distinta (otro id) de la que crea el mapper
        Organization savedOrganization = existingOrganization();
        savedOrganization.setId(SAVED_ORGANIZATION_ID);

        when(organizationRepository.findByShortName(NEW_SHORT_NAME)).thenReturn(Optional.empty());
        when(organizationRepository.save(any(Organization.class))).thenReturn(savedOrganization);

        ArgumentCaptor<Organization> captor = ArgumentCaptor.forClass(Organization.class);

        OrganizationResponseDTO result = service.createOrganization(new OrganizationRequestDTO(NEW_NAME, NEW_SHORT_NAME));

        verify(organizationRepository).save(captor.capture());

        Organization created = captor.getValue();
        assertThat(created.getName()).isEqualTo(NEW_NAME);
        assertThat(created.getShortName()).isEqualTo(NEW_SHORT_NAME);

        assertThat(result.getId()).isEqualTo(SAVED_ORGANIZATION_ID);
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getShortName()).isEqualTo(SHORT_NAME);
    }

    //O3 updateOrganization Organización inexistente -> ResourceNotFound con el id en el mensaje; no se consulta la abreviatura ni se guarda
    @Test
    void updateOrganization_whenOrganizationNotFound_throwsResourceNotFound() {
        when(organizationRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateOrganization(MISSING_ID, new OrganizationRequestDTO(NEW_NAME, NEW_SHORT_NAME)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(MISSING_ID.toString());

        verify(organizationRepository, never()).findByShortName(any(String.class));
        verify(organizationRepository, never()).save(any(Organization.class));
    }

    //O4 updateOrganization CARACTERIZACIÓN (sin regla documentada, ver plan D-O1): abreviatura cambiada a una ya registrada -> ResourceAlreadyExists; no se guarda y la organización no cambia
    @Test
    void updateOrganization_whenNewShortNameAlreadyExists_throwsResourceAlreadyExistsAndDoesNotChangeOrganization() {
        Organization existing = existingOrganization();

        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(existing));
        when(organizationRepository.findByShortName(NEW_SHORT_NAME)).thenReturn(Optional.of(new Organization()));

        assertThatThrownBy(() -> service.updateOrganization(ORGANIZATION_ID, new OrganizationRequestDTO(NEW_NAME, NEW_SHORT_NAME)))
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(organizationRepository, never()).save(any(Organization.class));
        assertThat(existing.getName()).isEqualTo(NAME);
        assertThat(existing.getShortName()).isEqualTo(SHORT_NAME);
    }

    //O5 updateOrganization Abreviatura sin cambios (solo cambia el nombre) -> no se consulta la abreviatura (la organización no choca consigo misma) y se guarda
    @Test
    void updateOrganization_whenShortNameUnchanged_doesNotCheckShortNameAndSaves() {
        Organization existing = existingOrganization();

        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(existing));
        when(organizationRepository.save(any(Organization.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.updateOrganization(ORGANIZATION_ID, new OrganizationRequestDTO(NEW_NAME, SHORT_NAME));

        verify(organizationRepository, never()).findByShortName(any(String.class));
        verify(organizationRepository).save(existing);
        assertThat(existing.getName()).isEqualTo(NEW_NAME);
        assertThat(existing.getShortName()).isEqualTo(SHORT_NAME);
    }

    //O6 updateOrganization Cambio válido de nombre y abreviatura -> la entidad existente recibe ambos valores, se guarda esa entidad y la respuesta sale de lo que devuelve save
    @Test
    void updateOrganization_whenValid_updatesExistingOrganizationAndReturnsSavedOrganization() {
        Organization existing = existingOrganization();
        // save devuelve una instancia distinta (otro id) de la entidad existente
        Organization savedOrganization = existingOrganization();
        savedOrganization.setId(SAVED_ORGANIZATION_ID);

        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(existing));
        when(organizationRepository.findByShortName(NEW_SHORT_NAME)).thenReturn(Optional.empty());
        when(organizationRepository.save(any(Organization.class))).thenReturn(savedOrganization);

        OrganizationResponseDTO result = service.updateOrganization(ORGANIZATION_ID, new OrganizationRequestDTO(NEW_NAME, NEW_SHORT_NAME));

        verify(organizationRepository).save(existing);
        assertThat(existing.getName()).isEqualTo(NEW_NAME);
        assertThat(existing.getShortName()).isEqualTo(NEW_SHORT_NAME);

        assertThat(result.getId()).isEqualTo(SAVED_ORGANIZATION_ID);
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getShortName()).isEqualTo(SHORT_NAME);
    }
}
