package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.exception.ResourceAlreadyExistsException;
import com.managementClub.managementClub.exception.ResourceNotFoundException;
import com.managementClub.managementClub.mapper.DogMapper;
import com.managementClub.managementClub.model.dto.DogRequestDTO;
import com.managementClub.managementClub.model.dto.DogResponseDTO;
import com.managementClub.managementClub.model.entity.Dog;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.enums.DogSex;
import com.managementClub.managementClub.repository.DogRepository;
import com.managementClub.managementClub.repository.PersonRepository;
import com.managementClub.managementClub.security.CurrentUserProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DogServiceImplTest {

    @Mock private DogRepository dogRepository;
    @Mock private PersonRepository personRepository;
    @Mock private CurrentUserProvider currentUserProvider;
    @Spy private DogMapper dogMapper = new DogMapper();

    @InjectMocks private DogServiceImpl service;

    private static final Long DOG_ID = 10L;
    private static final Long SAVED_DOG_ID = 77L;
    private static final Long OTHER_DOG_ID = 11L;
    private static final Long OWNER_ID = 5L;
    private static final Long OTHER_PERSON_ID = 6L;
    private static final Long MISSING_ID = 999L;
    private static final String MICROCHIP = "123456789012345";
    private static final String OTHER_MICROCHIP = "999999999999999";
    private static final String PEDIGREE = "ES-2020-1234";
    private static final String OTHER_PEDIGREE = "ES-2021-9999";
    private static final LocalDate BIRTH_DATE = LocalDate.of(2020, 5, 15);
    private static final LocalDate NEW_BIRTH_DATE = LocalDate.of(2021, 1, 20);

    private Person owner() {
        Person owner = new Person();
        owner.setId(OWNER_ID);
        owner.setName("Ana");
        owner.setLastName("López");
        return owner;
    }

    // Perro existente: Rex, MALE, Labrador, MICROCHIP, PEDIGREE, de owner
    private Dog existingDog(Person owner) {
        Dog dog = new Dog("Rex", BIRTH_DATE, DogSex.MALE, "Labrador", MICROCHIP, PEDIGREE, owner);
        dog.setId(DOG_ID);
        return dog;
    }

    // Petición de alta/modificación con todos los datos distintos a los de existingDog(), salvo microchip y pedigree (se pasan)
    private DogRequestDTO request(String microchip, String pedigree) {
        DogRequestDTO request = new DogRequestDTO();
        request.setName("Nombre nuevo");
        request.setBirthDate(NEW_BIRTH_DATE);
        request.setSex(DogSex.FEMALE);
        request.setBreed("Raza nueva");
        request.setMicrochip(microchip);
        request.setPedigreeNumber(pedigree);
        request.setOwnerId(OWNER_ID);
        return request;
    }

    //D1 createDog Ni admin ni propietario, con un propietario que ni siquiera existe -> AccessDenied y no se consulta ningún repositorio
    @Test
    void createDog_whenNotAdminNorOwner_throwsAccessDeniedWithoutQueryingRepositories() {
        DogRequestDTO request = request(MICROCHIP, PEDIGREE);
        request.setOwnerId(MISSING_ID);

        when(currentUserProvider.isAdminOrOwner(MISSING_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.createDog(request))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(personRepository, dogRepository);
    }

    //D2 createDog Con permiso, propietario inexistente -> ResourceNotFound y no se toca el repositorio de perros
    @Test
    void createDog_whenOwnerNotFound_throwsResourceNotFound() {
        DogRequestDTO request = request(MICROCHIP, PEDIGREE);
        request.setOwnerId(MISSING_ID);

        when(currentUserProvider.isAdminOrOwner(MISSING_ID)).thenReturn(true);
        when(personRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createDog(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(MISSING_ID.toString());

        verifyNoInteractions(dogRepository);
    }

    //D3 createDog Microchip ya registrado -> ResourceAlreadyExists con mensaje de microchip y no se guarda
    @Test
    void createDog_whenMicrochipAlreadyExists_throwsResourceAlreadyExistsMentioningMicrochip() {
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(personRepository.findById(OWNER_ID)).thenReturn(Optional.of(owner()));
        when(dogRepository.findByMicrochip(MICROCHIP)).thenReturn(Optional.of(new Dog()));

        assertThatThrownBy(() -> service.createDog(request(MICROCHIP, PEDIGREE)))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("microchip");

        verify(dogRepository, never()).save(any(Dog.class));
    }

    //D4 createDog Pedigree informado ya registrado -> ResourceAlreadyExists con mensaje de pedigree (distinto al del microchip) y no se guarda
    @Test
    void createDog_whenPedigreeAlreadyExists_throwsResourceAlreadyExistsMentioningPedigree() {
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(personRepository.findById(OWNER_ID)).thenReturn(Optional.of(owner()));
        when(dogRepository.findByMicrochip(MICROCHIP)).thenReturn(Optional.empty());
        when(dogRepository.findByPedigreeNumber(PEDIGREE)).thenReturn(Optional.of(new Dog()));

        assertThatThrownBy(() -> service.createDog(request(MICROCHIP, PEDIGREE)))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("pedigree");

        verify(dogRepository, never()).save(any(Dog.class));
    }

    //D5 createDog Pedigree null, vacío o en blanco -> no se consulta la unicidad y se guarda sin pedigree (null)
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void createDog_whenPedigreeNullEmptyOrBlank_doesNotCheckPedigreeAndSavesNull(String pedigree) {
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(personRepository.findById(OWNER_ID)).thenReturn(Optional.of(owner()));
        when(dogRepository.findByMicrochip(MICROCHIP)).thenReturn(Optional.empty());
        when(dogRepository.save(any(Dog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Dog> captor = ArgumentCaptor.forClass(Dog.class);

        service.createDog(request(MICROCHIP, pedigree));

        verify(dogRepository, never()).findByPedigreeNumber(any());
        verify(dogRepository).save(captor.capture());
        assertThat(captor.getValue().getPedigreeNumber()).isNull();
    }

    //D6 createDog Alta válida -> se guardan los datos del DTO con el propietario devuelto por findById y la respuesta sale de lo que devuelve save
    @Test
    void createDog_whenValid_savesDogWithRequestDataAndOwnerAndReturnsSavedDog() {
        Person owner = owner();
        // save devuelve una instancia distinta (otro id) de la que crea el mapper
        Dog savedDog = existingDog(owner);
        savedDog.setId(SAVED_DOG_ID);

        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(personRepository.findById(OWNER_ID)).thenReturn(Optional.of(owner));
        when(dogRepository.findByMicrochip(OTHER_MICROCHIP)).thenReturn(Optional.empty());
        when(dogRepository.findByPedigreeNumber(OTHER_PEDIGREE)).thenReturn(Optional.empty());
        when(dogRepository.save(any(Dog.class))).thenReturn(savedDog);

        ArgumentCaptor<Dog> captor = ArgumentCaptor.forClass(Dog.class);

        DogResponseDTO result = service.createDog(request(OTHER_MICROCHIP, OTHER_PEDIGREE));

        verify(dogRepository).save(captor.capture());

        Dog created = captor.getValue();
        assertThat(created.getName()).isEqualTo("Nombre nuevo");
        assertThat(created.getBirthDate()).isEqualTo(NEW_BIRTH_DATE);
        assertThat(created.getSex()).isEqualTo(DogSex.FEMALE);
        assertThat(created.getBreed()).isEqualTo("Raza nueva");
        assertThat(created.getMicrochip()).isEqualTo(OTHER_MICROCHIP);
        assertThat(created.getPedigreeNumber()).isEqualTo(OTHER_PEDIGREE);
        assertThat(created.getOwner()).isSameAs(owner);

        assertThat(result.getId()).isEqualTo(SAVED_DOG_ID);
    }

    //G1 getDogById Perro inexistente y usuario no admin -> AccessDenied (403, no 404)
    @Test
    void getDogById_whenDogNotFoundAndUserNotAdmin_throwsAccessDenied() {
        when(dogRepository.findById(MISSING_ID)).thenReturn(Optional.empty());
        // El perro no existe: el Service no tiene propietario y pregunta por permisos con null
        when(currentUserProvider.isAdminOrOwner(isNull())).thenReturn(false);

        assertThatThrownBy(() -> service.getDogById(MISSING_ID))
                .isInstanceOf(AccessDeniedException.class);
    }

    //G2 getDogById Perro inexistente y admin -> ResourceNotFound
    @Test
    void getDogById_whenDogNotFoundAndUserAdmin_throwsResourceNotFound() {
        when(dogRepository.findById(MISSING_ID)).thenReturn(Optional.empty());
        // El perro no existe: el Service no tiene propietario y pregunta por permisos con null
        when(currentUserProvider.isAdminOrOwner(isNull())).thenReturn(true);

        assertThatThrownBy(() -> service.getDogById(MISSING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(MISSING_ID.toString());
    }

    //G3 getDogById Perro de otro propietario y usuario no admin -> AccessDenied; el permiso se evalúa con el id del propietario, no con el del perro
    @Test
    void getDogById_whenDogNotOwnedByUser_throwsAccessDenied() {
        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(existingDog(owner())));
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.getDogById(DOG_ID))
                .isInstanceOf(AccessDeniedException.class);
    }

    //G4 getDogById Perro propio o admin -> DTO con todos los datos del perro y del propietario
    @Test
    void getDogById_whenDogOwnedByUser_returnsMappedDto() {
        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(existingDog(owner())));
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);

        DogResponseDTO result = service.getDogById(DOG_ID);

        assertThat(result.getId()).isEqualTo(DOG_ID);
        assertThat(result.getName()).isEqualTo("Rex");
        assertThat(result.getBirthDate()).isEqualTo(BIRTH_DATE);
        assertThat(result.getSex()).isEqualTo(DogSex.MALE);
        assertThat(result.getBreed()).isEqualTo("Labrador");
        assertThat(result.getMicrochip()).isEqualTo(MICROCHIP);
        assertThat(result.getPedigreeNumber()).isEqualTo(PEDIGREE);
        assertThat(result.getOwner().getId()).isEqualTo(OWNER_ID);
        assertThat(result.getOwner().getName()).isEqualTo("Ana");
        assertThat(result.getOwner().getLastName()).isEqualTo("López");
    }

    //P1 getDogsByPersonId Ni admin ni la propia persona -> AccessDenied y no se consulta ningún repositorio
    @Test
    void getDogsByPersonId_whenNotAdminNorOwner_throwsAccessDeniedWithoutQueryingRepositories() {
        when(currentUserProvider.isAdminOrOwner(OTHER_PERSON_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.getDogsByPersonId(OTHER_PERSON_ID))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(personRepository, dogRepository);
    }

    //P2 getDogsByPersonId Con permiso, persona inexistente -> ResourceNotFound y no se consultan los perros
    @Test
    void getDogsByPersonId_whenPersonNotFound_throwsResourceNotFound() {
        when(currentUserProvider.isAdminOrOwner(MISSING_ID)).thenReturn(true);
        when(personRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getDogsByPersonId(MISSING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(MISSING_ID.toString());

        verifyNoInteractions(dogRepository);
    }

    //P3 getDogsByPersonId Con permiso y persona con perros -> lista mapeada, mismo tamaño y orden que el repositorio
    @Test
    void getDogsByPersonId_whenPersonHasDogs_returnsMappedDogsInOrder() {
        Person owner = owner();
        Dog first = existingDog(owner);
        Dog second = existingDog(owner);
        second.setId(OTHER_DOG_ID);

        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(personRepository.findById(OWNER_ID)).thenReturn(Optional.of(owner));
        when(dogRepository.findByOwnerId(OWNER_ID)).thenReturn(List.of(first, second));

        List<DogResponseDTO> result = service.getDogsByPersonId(OWNER_ID);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(DOG_ID);
        assertThat(result.get(1).getId()).isEqualTo(OTHER_DOG_ID);
    }

    //M1 getDogByMicrochip Microchip inexistente -> ResourceNotFound con el microchip en el mensaje
    @Test
    void getDogByMicrochip_whenNotFound_throwsResourceNotFound() {
        when(dogRepository.findByMicrochip(MICROCHIP)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getDogByMicrochip(MICROCHIP))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(MICROCHIP);
    }

    //M2 getDogByMicrochip Microchip existente -> DTO del perro
    @Test
    void getDogByMicrochip_whenFound_returnsMappedDto() {
        when(dogRepository.findByMicrochip(MICROCHIP)).thenReturn(Optional.of(existingDog(owner())));

        DogResponseDTO result = service.getDogByMicrochip(MICROCHIP);

        assertThat(result.getId()).isEqualTo(DOG_ID);
        assertThat(result.getMicrochip()).isEqualTo(MICROCHIP);
    }

    //N1 getDogByName CARACTERIZACIÓN (sin regla documentada, ver plan D-D2): el texto llega tal cual al repositorio y la lista se devuelve mapeada en el mismo orden
    @Test
    void getDogByName_whenResults_returnsMappedDogsInOrder() {
        Person owner = owner();
        Dog first = existingDog(owner);
        Dog second = existingDog(owner);
        second.setId(OTHER_DOG_ID);

        when(dogRepository.findByNameContainingIgnoreCase("rE")).thenReturn(List.of(first, second));

        List<DogResponseDTO> result = service.getDogByName("rE");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(DOG_ID);
        assertThat(result.get(1).getId()).isEqualTo(OTHER_DOG_ID);
    }

    //A1 getAllDogs Con varios perros -> todos mapeados, mismo orden que findAll
    @Test
    void getAllDogs_whenSeveralDogs_returnsAllMappedInOrder() {
        Person owner = owner();
        Dog first = existingDog(owner);
        Dog second = existingDog(owner);
        second.setId(OTHER_DOG_ID);

        when(dogRepository.findAll()).thenReturn(List.of(first, second));

        List<DogResponseDTO> result = service.getAllDogs();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(DOG_ID);
        assertThat(result.get(1).getId()).isEqualTo(OTHER_DOG_ID);
    }

    //U1 updateDog Perro inexistente y usuario no admin -> AccessDenied (403 antes que 404) y no se guarda
    @Test
    void updateDog_whenDogNotFoundAndUserNotAdmin_throwsAccessDenied() {
        when(dogRepository.findById(MISSING_ID)).thenReturn(Optional.empty());
        // El perro no existe: el Service no tiene propietario y pregunta por permisos con null
        when(currentUserProvider.isAdminOrOwner(isNull())).thenReturn(false);

        assertThatThrownBy(() -> service.updateDog(MISSING_ID, request(MICROCHIP, PEDIGREE)))
                .isInstanceOf(AccessDeniedException.class);

        verify(dogRepository, never()).save(any(Dog.class));
    }

    //U2 updateDog Perro inexistente y admin -> ResourceNotFound y no se guarda
    @Test
    void updateDog_whenDogNotFoundAndUserAdmin_throwsResourceNotFound() {
        when(dogRepository.findById(MISSING_ID)).thenReturn(Optional.empty());
        // El perro no existe: el Service no tiene propietario y pregunta por permisos con null
        when(currentUserProvider.isAdminOrOwner(isNull())).thenReturn(true);

        assertThatThrownBy(() -> service.updateDog(MISSING_ID, request(MICROCHIP, PEDIGREE)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(MISSING_ID.toString());

        verify(dogRepository, never()).save(any(Dog.class));
    }

    //U3 updateDog Perro de otro propietario y usuario no admin -> AccessDenied; no se consulta la unicidad, no se guarda y el perro no cambia
    @Test
    void updateDog_whenDogNotOwnedByUser_throwsAccessDeniedAndDoesNotChangeDog() {
        Dog existing = existingDog(owner());

        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(existing));
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.updateDog(DOG_ID, request(OTHER_MICROCHIP, OTHER_PEDIGREE)))
                .isInstanceOf(AccessDeniedException.class);

        verify(dogRepository, never()).findByMicrochip(any(String.class));
        verify(dogRepository, never()).findByPedigreeNumber(any(String.class));
        verify(dogRepository, never()).save(any(Dog.class));
        assertThat(existing.getName()).isEqualTo("Rex");
        assertThat(existing.getMicrochip()).isEqualTo(MICROCHIP);
    }

    //U4 updateDog Microchip cambiado a uno ya registrado -> ResourceAlreadyExists con mensaje de microchip; no se guarda y el perro no cambia
    @Test
    void updateDog_whenNewMicrochipAlreadyExists_throwsResourceAlreadyExistsAndDoesNotChangeDog() {
        Dog existing = existingDog(owner());

        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(existing));
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(dogRepository.findByMicrochip(OTHER_MICROCHIP)).thenReturn(Optional.of(new Dog()));

        assertThatThrownBy(() -> service.updateDog(DOG_ID, request(OTHER_MICROCHIP, PEDIGREE)))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("microchip");

        verify(dogRepository, never()).save(any(Dog.class));
        assertThat(existing.getMicrochip()).isEqualTo(MICROCHIP);
        assertThat(existing.getName()).isEqualTo("Rex");
    }

    //U5 updateDog Microchip sin cambios -> no se consulta la unicidad (el perro no choca consigo mismo) y se guarda
    @Test
    void updateDog_whenMicrochipUnchanged_doesNotCheckMicrochipUniquenessAndSaves() {
        Dog existing = existingDog(owner());

        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(existing));
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(dogRepository.save(any(Dog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.updateDog(DOG_ID, request(MICROCHIP, null));

        verify(dogRepository, never()).findByMicrochip(any(String.class));
        verify(dogRepository).save(existing);
    }

    //U6 updateDog Pedigree cambiado a uno ya registrado -> ResourceAlreadyExists con mensaje de pedigree (distinto al del microchip) y no se guarda
    @Test
    void updateDog_whenNewPedigreeAlreadyExists_throwsResourceAlreadyExistsMentioningPedigree() {
        Dog existing = existingDog(owner());

        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(existing));
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(dogRepository.findByPedigreeNumber(OTHER_PEDIGREE)).thenReturn(Optional.of(new Dog()));

        assertThatThrownBy(() -> service.updateDog(DOG_ID, request(MICROCHIP, OTHER_PEDIGREE)))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("pedigree");

        verify(dogRepository, never()).save(any(Dog.class));
        assertThat(existing.getPedigreeNumber()).isEqualTo(PEDIGREE);
    }

    //U7 updateDog Pedigree sin cambios -> no se consulta la unicidad del pedigree y se guarda
    @Test
    void updateDog_whenPedigreeUnchanged_doesNotCheckPedigreeUniquenessAndSaves() {
        Dog existing = existingDog(owner());

        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(existing));
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(dogRepository.save(any(Dog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.updateDog(DOG_ID, request(MICROCHIP, PEDIGREE));

        verify(dogRepository, never()).findByPedigreeNumber(any(String.class));
        verify(dogRepository).save(existing);
    }

    //U8 updateDog Pedigree nuevo vacío o en blanco -> no se consulta la unicidad y el perro queda sin pedigree (null)
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void updateDog_whenNewPedigreeEmptyOrBlank_doesNotCheckPedigreeAndClearsIt(String pedigree) {
        Dog existing = existingDog(owner());

        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(existing));
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(dogRepository.save(any(Dog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.updateDog(DOG_ID, request(MICROCHIP, pedigree));

        verify(dogRepository, never()).findByPedigreeNumber(any(String.class));
        assertThat(existing.getPedigreeNumber()).isNull();
    }

    //U9 updateDog Actualización válida -> la entidad existente recibe los datos del DTO, se guarda esa entidad y la respuesta sale de lo que devuelve save
    @Test
    void updateDog_whenValid_updatesExistingDogAndReturnsSavedDog() {
        Person owner = owner();
        Dog existing = existingDog(owner);
        // save devuelve una instancia distinta (otro id) de la entidad existente
        Dog savedDog = existingDog(owner);
        savedDog.setId(SAVED_DOG_ID);

        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(existing));
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(dogRepository.findByMicrochip(OTHER_MICROCHIP)).thenReturn(Optional.empty());
        when(dogRepository.findByPedigreeNumber(OTHER_PEDIGREE)).thenReturn(Optional.empty());
        when(dogRepository.save(any(Dog.class))).thenReturn(savedDog);

        DogResponseDTO result = service.updateDog(DOG_ID, request(OTHER_MICROCHIP, OTHER_PEDIGREE));

        verify(dogRepository).save(existing);
        assertThat(existing.getName()).isEqualTo("Nombre nuevo");
        assertThat(existing.getBirthDate()).isEqualTo(NEW_BIRTH_DATE);
        assertThat(existing.getSex()).isEqualTo(DogSex.FEMALE);
        assertThat(existing.getBreed()).isEqualTo("Raza nueva");
        assertThat(existing.getMicrochip()).isEqualTo(OTHER_MICROCHIP);
        assertThat(existing.getPedigreeNumber()).isEqualTo(OTHER_PEDIGREE);

        assertThat(result.getId()).isEqualTo(SAVED_DOG_ID);
    }

    //U10 updateDog PUT sin campos opcionales -> sexo, raza y pedigree quedan sin valor aunque el perro los tuviera
    @Test
    void updateDog_whenOptionalFieldsNotSent_clearsThem() {
        Dog existing = existingDog(owner());

        DogRequestDTO request = request(MICROCHIP, null);
        request.setSex(null);
        request.setBreed(null);

        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(existing));
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(dogRepository.save(any(Dog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.updateDog(DOG_ID, request);

        assertThat(existing.getSex()).isNull();
        assertThat(existing.getBreed()).isNull();
        assertThat(existing.getPedigreeNumber()).isNull();
    }

    //U11 updateDog El DTO trae otro ownerId -> se ignora: el propietario no cambia y no se consulta la persona
    @Test
    void updateDog_whenRequestHasOtherOwnerId_ignoresItAndKeepsOwner() {
        Person owner = owner();
        Dog existing = existingDog(owner);

        DogRequestDTO request = request(MICROCHIP, PEDIGREE);
        request.setOwnerId(OTHER_PERSON_ID);

        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(existing));
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(dogRepository.save(any(Dog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DogResponseDTO result = service.updateDog(DOG_ID, request);

        assertThat(existing.getOwner()).isSameAs(owner);
        assertThat(result.getOwner().getId()).isEqualTo(OWNER_ID);
        verifyNoInteractions(personRepository);
    }
}
