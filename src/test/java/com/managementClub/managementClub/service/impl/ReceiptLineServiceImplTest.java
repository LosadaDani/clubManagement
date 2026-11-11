package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.exception.InvalidBusinessRuleException;
import com.managementClub.managementClub.exception.ResourceNotFoundException;
import com.managementClub.managementClub.mapper.ReceiptLineMapper;
import com.managementClub.managementClub.model.dto.ReceiptLineRequestDTO;
import com.managementClub.managementClub.model.dto.ReceiptLineResponseDTO;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.entity.ReceiptLine;
import com.managementClub.managementClub.model.enums.ReceiptLineStatus;
import com.managementClub.managementClub.repository.PersonRepository;
import com.managementClub.managementClub.repository.ReceiptLineRepository;
import com.managementClub.managementClub.security.CurrentUserProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptLineServiceImplTest {

    @Mock private ReceiptLineRepository receiptLineRepository;
    @Mock private PersonRepository personRepository;
    @Mock private CurrentUserProvider currentUserProvider;
    @Spy private ReceiptLineMapper receiptLineMapper = new ReceiptLineMapper();

    @InjectMocks private ReceiptLineServiceImpl service;

    private static final Long LINE_ID = 1L;
    private static final Long PERSON_ID = 5L;
    private static final Long OTHER_PERSON_ID = 10L;
    private static final Long MISSING_ID = 999L;

    //1	createReceiptLine	Persona inexistente	Lanza ResourceNotFoundException y no se llama a save
    @Test
    void createReceiptLine_whenPersonNotFound_throwsResourceNotFound(){
        ReceiptLineRequestDTO request = new ReceiptLineRequestDTO();
        request.setPersonId(MISSING_ID);

        when(personRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createReceiptLine(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(receiptLineRepository, never()).save(any());
    }

    //2	createReceiptLine	Petición válida	La línea guardada tiene estado PENDING y el DTO devuelto refleja los datos.
    @Test
    void createReceiptLine_whenValid_savesLinePending () {
        LocalDate today = LocalDate.now();

        ReceiptLineRequestDTO request = new ReceiptLineRequestDTO();
        request.setPersonId(PERSON_ID);
        request.setConcept("Prueba test create");
        request.setDate(today);
        request.setAmount(new BigDecimal("20.00"));

        Person person = new Person();
        person.setId(PERSON_ID);

        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(person));
        when(receiptLineRepository.save(any(ReceiptLine.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<ReceiptLine> captor = ArgumentCaptor.forClass(ReceiptLine.class);

        ReceiptLineResponseDTO result = service.createReceiptLine(request);

        verify(receiptLineRepository).save(captor.capture());

        //Verificacion de lo que devuelve el metodo
        assertThat(result.getPerson().getId()).isEqualTo(PERSON_ID);
        assertThat(result.getStatus()).isEqualTo(ReceiptLineStatus.PENDING);
        assertThat(result.getConcept()).isEqualTo("Prueba test create");
        assertThat(result.getAmount()).isEqualByComparingTo("20.00");
        assertThat(result.getDate()).isEqualTo(today);

        //verificacion del save
        ReceiptLine receiptLine = captor.getValue();
        assertThat(receiptLine.getPerson()).isSameAs(person);
        assertThat(receiptLine.getStatus()).isEqualTo(ReceiptLineStatus.PENDING);
        assertThat(receiptLine.getAmount()).isEqualByComparingTo("20.00");
        assertThat(receiptLine.getConcept()).isEqualTo("Prueba test create");
        assertThat(receiptLine.getDate()).isEqualTo(today);
    }

    //3	findByPerson	Ni admin ni propietario	Lanza AccessDeniedException y no hay interacciones con personRepository
    @Test
    void findByPerson_whenNotAdminNorOwner_throwsAccessDenied () {
        when(currentUserProvider.isAdminOrOwner(PERSON_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.findByPerson(PERSON_ID, null))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(personRepository);
    }

    //4	findByPerson	Admin/propietario, persona inexistente	Lanza ResourceNotFoundException
    @Test
    void findByPerson_whenPersonNotFound_throwsResourceNotFound () {
        when(currentUserProvider.isAdminOrOwner(MISSING_ID)).thenReturn(true);
        when(personRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByPerson(MISSING_ID, null))
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(receiptLineRepository);
    }

    //5	findByPerson	status == null	Usa findByPersonOrderByDateDesc y nunca la variante con estado
    @Test
    void findByPerson_whenStatusNull_usesFindByPersonOrderByDateDesc (){
        Person person = new Person();
        person.setId(PERSON_ID);

        ReceiptLine receiptLine = new ReceiptLine(person,
                LocalDate.of(2026,10,3),
                "Linea test",
                new BigDecimal("10.00"),
                ReceiptLineStatus.PENDING,
                null);

        when(currentUserProvider.isAdminOrOwner(PERSON_ID)).thenReturn(true);
        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(person));
        when(receiptLineRepository.findByPersonOrderByDateDesc(person)).thenReturn(List.of(receiptLine));

        List<ReceiptLineResponseDTO> result = service.findByPerson(PERSON_ID, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getConcept()).isEqualTo("Linea test");

        verify(receiptLineRepository, never()).findByPersonAndStatusOrderByDateDesc(any(), any());
    }

    //6	findByPerson	status informado	Usa findByPersonAndStatusOrderByDateDesc y nunca la otra
    @Test
    void findByPerson_whenStatusGiven_usesFindByPersonAndStatusOrderByDateDesc (){
        Person person = new Person();
        person.setId(PERSON_ID);

        ReceiptLine receiptLine = new ReceiptLine(person,
                LocalDate.of(2026,10,3),
                "Linea test",
                new BigDecimal("10.00"),
                ReceiptLineStatus.PENDING,
                null);

        when(currentUserProvider.isAdminOrOwner(PERSON_ID)).thenReturn(true);
        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(person));
        when(receiptLineRepository.findByPersonAndStatusOrderByDateDesc(person, ReceiptLineStatus.PENDING)).thenReturn(List.of(receiptLine));

        List<ReceiptLineResponseDTO> result = service.findByPerson(PERSON_ID, ReceiptLineStatus.PENDING);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getConcept()).isEqualTo("Linea test");

        verify(receiptLineRepository, never()).findByPersonOrderByDateDesc(any());
    }

    //7	updateReceiptLine	Línea inexistente	Lanza ResourceNotFoundException
    @Test
    void updateReceiptLine_whenNotFound_throwsResourceNotFound() {
        // Given: el repositorio no encuentra nada con el id 999
        when(receiptLineRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

        // When + Then: al llamar al método, debe lanzar esta excepción
        assertThatThrownBy(() -> service.updateReceiptLine(MISSING_ID, new ReceiptLineRequestDTO()))
                .isInstanceOf(ResourceNotFoundException.class);

        // Y además: nunca se llegó a guardar nada
        verify(receiptLineRepository, never()).save(any());
    }

    //8	updateReceiptLine	Línea ISSUED o PAID	Lanza InvalidBusinessRuleException y no se llama a save
    @ParameterizedTest
    @EnumSource(value = ReceiptLineStatus.class, names = "PENDING", mode = EnumSource.Mode.EXCLUDE)
    void updateReceiptLine_whenNotPending_throwsInvalidBusinessRule(ReceiptLineStatus status){
        ReceiptLine receiptLine = new ReceiptLine();
        receiptLine.setStatus(status);

        ReceiptLineRequestDTO receiptLineRequest = new ReceiptLineRequestDTO();

        when (receiptLineRepository.findById(LINE_ID)).thenReturn(Optional.of(receiptLine));

        assertThatThrownBy(() -> service.updateReceiptLine(LINE_ID, receiptLineRequest))
                .isInstanceOf(InvalidBusinessRuleException.class);

        verify(receiptLineRepository, never()).save(any());
    }

    //9	updateReceiptLine	Línea PENDING	Los campos (fecha, concepto, importe) cambian y se guarda
    @Test
    void updateReceiptLine_whenPending_updateFields(){
        Person person = new Person();
        person.setId(PERSON_ID);

        ReceiptLine receiptLinePending = new ReceiptLine(person,
                LocalDate.of(2026,10,3),
                "Linea test",
                new BigDecimal("10.00"),
                ReceiptLineStatus.PENDING,
                null);

        ReceiptLineRequestDTO receiptLineRequest = new ReceiptLineRequestDTO();
        receiptLineRequest.setPersonId(OTHER_PERSON_ID); //No se modifica el id de la persona en el receiptLine
        receiptLineRequest.setDate(LocalDate.of(2026, 11, 1));
        receiptLineRequest.setConcept("Linea test modificada");
        receiptLineRequest.setAmount(new BigDecimal("20.00"));

        when(receiptLineRepository.findById(LINE_ID)).thenReturn(Optional.of(receiptLinePending));
        when(receiptLineRepository.save(any(ReceiptLine.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReceiptLineResponseDTO result = service.updateReceiptLine(LINE_ID, receiptLineRequest);

        assertThat(result.getDate()).isEqualTo(LocalDate.of(2026,11,1));
        assertThat(result.getConcept()).isEqualTo("Linea test modificada");
        assertThat(result.getAmount()).isEqualByComparingTo("20.00");
        assertThat(result.getStatus()).isEqualTo(ReceiptLineStatus.PENDING);
        assertThat(result.getPerson().getId()).isEqualTo(PERSON_ID);

        verify(receiptLineRepository).save(receiptLinePending);
    }

    //10 deleteReceiptLine	Línea inexistente	Lanza ResourceNotFoundException
    @Test
    void deleteReceiptLine_whenNotFound_throwsResourceNotFound(){
        when(receiptLineRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteReceiptLine(MISSING_ID))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(receiptLineRepository, never()).delete(any());
    }

    //11 deleteReceiptLine	Línea ISSUED o PAID	Lanza InvalidBusinessRuleException y no se llama a delete
    @ParameterizedTest
    @EnumSource(value = ReceiptLineStatus.class, names = "PENDING", mode = EnumSource.Mode.EXCLUDE)
    void deleteReceiptLine_whenNotPending_throwsInvalidBusinessRule(ReceiptLineStatus status){
        ReceiptLine receiptLine = new ReceiptLine();
        receiptLine.setStatus(status);

        when(receiptLineRepository.findById(LINE_ID)).thenReturn(Optional.of(receiptLine));

        assertThatThrownBy(() -> service.deleteReceiptLine(LINE_ID))
                .isInstanceOf(InvalidBusinessRuleException.class);

        verify(receiptLineRepository, never()).delete(any());
    }

    //12 deleteReceiptLine	Línea PENDING	Se llama a delete con esa línea
    @Test
    void deleteReceiptLine_whenPending_deletesLine (){
        ReceiptLine receiptLine = new ReceiptLine();
        receiptLine.setStatus(ReceiptLineStatus.PENDING);

        when (receiptLineRepository.findById(LINE_ID)).thenReturn(Optional.of(receiptLine));

        service.deleteReceiptLine(LINE_ID);

        verify(receiptLineRepository).delete(receiptLine);
    }

    //13 createInitiationLines	Alta de iniciación	Se guardan dos líneas PENDING: 150 con “1ª cuota Iniciación” y 100 con “2ª cuota Iniciación”, cada una con su fecha
    @Test
    void createInitiationLines_savesFirstAndSecondInstallment() {
        Person person = new Person();
        ArgumentCaptor<ReceiptLine> captor = ArgumentCaptor.forClass(ReceiptLine.class);

        LocalDate today = LocalDate.now();

        service.createInitiationLines(person);

        verify(receiptLineRepository, times(2)).save(captor.capture());
        List<ReceiptLine> saved = captor.getAllValues();

        ReceiptLine first = saved.get(0);
        ReceiptLine second = saved.get(1);

        // Línea 1: saved.get(0) → persona, estado PENDING, importe 150, concepto "1ª cuota Iniciación", fecha hoy
        assertThat(first.getPerson()).isSameAs(person);
        assertThat(first.getStatus()).isEqualTo(ReceiptLineStatus.PENDING);
        assertThat(first.getAmount()).isEqualByComparingTo("150.00");
        assertThat(first.getConcept()).isEqualTo("1ª cuota Iniciación");
        assertThat(first.getDate()).isEqualTo(today);

        // Línea 2: saved.get(1) → persona, estado PENDING, importe 100, concepto "2ª cuota Iniciación", fecha hoy + 1 mes
        assertThat(second.getPerson()).isSameAs(person);
        assertThat(second.getStatus()).isEqualTo(ReceiptLineStatus.PENDING);
        assertThat(second.getAmount()).isEqualByComparingTo("100.00");
        assertThat(second.getConcept()).isEqualTo("2ª cuota Iniciación");
        assertThat(second.getDate()).isEqualTo(today.plusMonths(1));
    }
}
