package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.exception.InvalidBusinessRuleException;
import com.managementClub.managementClub.exception.ResourceNotFoundException;
import com.managementClub.managementClub.mapper.ReceiptLineMapper;
import com.managementClub.managementClub.mapper.ReceiptMapper;
import com.managementClub.managementClub.model.dto.GenerateReceiptRequestDTO;
import com.managementClub.managementClub.model.dto.ReceiptDetailResponseDTO;
import com.managementClub.managementClub.model.dto.ReceiptLineResponseDTO;
import com.managementClub.managementClub.model.dto.ReceiptProposalResponseDTO;
import com.managementClub.managementClub.model.dto.ReceiptResponseDTO;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.entity.Receipt;
import com.managementClub.managementClub.model.entity.ReceiptLine;
import com.managementClub.managementClub.model.enums.MembershipStatus;
import com.managementClub.managementClub.model.enums.MembershipType;
import com.managementClub.managementClub.model.enums.ReceiptLineStatus;
import com.managementClub.managementClub.model.enums.ReceiptStatus;
import com.managementClub.managementClub.repository.PersonRepository;
import com.managementClub.managementClub.repository.ReceiptLineRepository;
import com.managementClub.managementClub.repository.ReceiptRepository;
import com.managementClub.managementClub.security.CurrentUserProvider;
import com.managementClub.managementClub.service.ReceiptLineService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceImplTest {

    @Mock private PersonRepository personRepository;
    @Mock private ReceiptLineService receiptLineService;
    @Mock private ReceiptLineRepository receiptLineRepository;
    @Mock private ReceiptRepository receiptRepository;
    @Mock private CurrentUserProvider currentUserProvider;
    @Spy private ReceiptMapper receiptMapper = new ReceiptMapper();
    @Spy private ReceiptLineMapper receiptLineMapper = new ReceiptLineMapper();
    @Spy private Clock clock = Clock.fixed(
            Instant.parse("2026-06-15T10:00:00Z"), ZoneId.of("Europe/Madrid"));

    @InjectMocks private ReceiptServiceImpl service;

    private static final Long RECEIPT_ID = 1L;
    private static final Long PERSON_ID = 5L;
    private static final Long OTHER_PERSON_ID = 6L;
    private static final Long RECEIPT_LINE_ID = 1L;
    private static final Long OTHER_RECEIPT_LINE_ID = 2L;
    private static final Long MISSING_ID = 999L;
    private static final Long THIRD_PERSON_ID = 7L;
    private static final Long FOURTH_PERSON_ID = 8L;
    private static final Long FIFTH_PERSON_ID = 9L;

    //A1 markAsPaid Recibo inexistente lanza ResourceNotFoundException y no se guarda nada
    @Test
    void markAsPaid_whenReceiptNotFound_throwsResourceNotFound() {
        when(receiptRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.markAsPaid(MISSING_ID))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(receiptRepository, never()).save(any(Receipt.class));
        verifyNoInteractions(receiptLineRepository);
    }

    //A2 markAsPaid Recibo no issued (Paid o returned) lanza InvalidBusinessException y no se guarda nada
    @ParameterizedTest
    @EnumSource(value = ReceiptStatus.class, names = "ISSUED", mode = EnumSource.Mode.EXCLUDE)
    void markAsPaid_whenReceiptNotIssued_throwsInvalidBusiness(ReceiptStatus status) {
        Receipt receipt = new Receipt();
        receipt.setStatus(status);

        when(receiptRepository.findById(RECEIPT_ID)).thenReturn(Optional.of(receipt));

        assertThatThrownBy(() -> service.markAsPaid(RECEIPT_ID))
                .isInstanceOf(InvalidBusinessRuleException.class);

        verify(receiptRepository, never()).save(any(Receipt.class));
        verifyNoInteractions(receiptLineRepository);
    }

    //A3 markAsPaid Recibo issued con dos lineas, se marca como paid y se guarda correctamente
    @Test
    void markAsPaid_whenReceiptValid_marksAsPaidAndSaves() {
        Person person = new Person();
        person.setId(PERSON_ID);

        Receipt receipt = new Receipt();
        receipt.setId(RECEIPT_ID);
        receipt.setStatus(ReceiptStatus.ISSUED);
        receipt.setPerson(person);
        receipt.setTotal(new BigDecimal("30.00"));

        ReceiptLine receiptLine1 = new ReceiptLine();
        receiptLine1.setReceipt(receipt);
        receiptLine1.setStatus(ReceiptLineStatus.ISSUED);
        receiptLine1.setPerson(person);

        ReceiptLine receiptLine2 = new ReceiptLine();
        receiptLine2.setReceipt(receipt);
        receiptLine2.setStatus(ReceiptLineStatus.ISSUED);
        receiptLine2.setPerson(person);

        when(receiptRepository.findById(RECEIPT_ID)).thenReturn(Optional.of(receipt));
        when(receiptLineRepository.findByReceiptIdOrderByDateDescIdDesc(RECEIPT_ID)).thenReturn(List.of(receiptLine1, receiptLine2));

        when(receiptRepository.save(any(Receipt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(receiptLineRepository.save(any(ReceiptLine.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReceiptDetailResponseDTO result = service.markAsPaid(RECEIPT_ID);

        assertThat(result.getId()).isEqualTo(RECEIPT_ID);
        assertThat(result.getLines()).hasSize(2);
        assertThat(result.getLines()).allMatch(line -> line.getStatus() == ReceiptLineStatus.PAID);


        assertThat(receipt.getStatus()).isEqualTo(ReceiptStatus.PAID);
        assertThat(receiptLine1.getStatus()).isEqualTo(ReceiptLineStatus.PAID);
        assertThat(receiptLine2.getStatus()).isEqualTo(ReceiptLineStatus.PAID);

        verify(receiptRepository).save(receipt);
        verify(receiptLineRepository, times(2)).save(any(ReceiptLine.class));
    }

    //A4 markAsReturned Recibo inexistente lanza ResourceNotFoundException y no se guarda nada
    @Test
    void markAsReturned_whenReceiptNotFound_throwsResourceNotFound() {
        when(receiptRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.markAsReturned(MISSING_ID))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(receiptRepository, never()).save(any(Receipt.class));
        verifyNoInteractions(receiptLineRepository);
    }

    //A5 markAsReturned Recibo no issued (Paid o returned) lanza InvalidBusinessException y no se guarda nada
    @ParameterizedTest
    @EnumSource(value = ReceiptStatus.class, names =  "ISSUED", mode = EnumSource.Mode.EXCLUDE)
    void markAsReturned_whenReceiptNotIssued_throwsInvalidBusiness(ReceiptStatus status) {
        Receipt receipt = new Receipt();
        receipt.setStatus(status);

        when(receiptRepository.findById(RECEIPT_ID)).thenReturn(Optional.of(receipt));

        assertThatThrownBy(() -> service.markAsReturned(RECEIPT_ID))
                .isInstanceOf(InvalidBusinessRuleException.class);

        verify(receiptRepository, never()).save(any(Receipt.class));
        verifyNoInteractions(receiptLineRepository);
    }

    //A6 markAsReturned Recibo issued con total <= 0 (0 y -5), lanza InvalidBusinessException y no se guarda nada.
    @ParameterizedTest
    @CsvSource({"0.00","-5.00"})
    void markAsReturned_whenReceiptTotalNotPositive_throwsInvalidBusiness(BigDecimal total) {
        Receipt receipt = new Receipt();
        receipt.setStatus(ReceiptStatus.ISSUED);
        receipt.setTotal(total);

        when(receiptRepository.findById(RECEIPT_ID)).thenReturn(Optional.of(receipt));

        assertThatThrownBy(() -> service.markAsReturned(RECEIPT_ID))
                .isInstanceOf(InvalidBusinessRuleException.class);

        verify(receiptRepository, never()).save(any(Receipt.class));
        verifyNoInteractions(receiptLineRepository);
    }

    //A7 markAsReturned Recibo issued con total > 0, se marca como returned y se guarda correctamente.
    @Test
    void markAsReturned_whenReceiptValid_marksAsReturnedAndSaves() {
        Person person = new Person();
        person.setId(PERSON_ID);

        Receipt receipt = new Receipt();
        receipt.setId(RECEIPT_ID);
        receipt.setStatus(ReceiptStatus.ISSUED);
        receipt.setPerson(person);
        receipt.setTotal(new BigDecimal("15.00"));

        ReceiptLine original = new ReceiptLine();
        original.setPerson(person);
        original.setStatus(ReceiptLineStatus.ISSUED);

        when(receiptRepository.findById(RECEIPT_ID)).thenReturn(Optional.of(receipt));
        when(receiptLineRepository.findByReceiptIdOrderByDateDescIdDesc(RECEIPT_ID))
                .thenReturn(List.of(original));

        when(receiptRepository.save(any(Receipt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(receiptLineRepository.save(any(ReceiptLine.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReceiptDetailResponseDTO result = service.markAsReturned(RECEIPT_ID);

        ArgumentCaptor<ReceiptLine> captor = ArgumentCaptor.forClass(ReceiptLine.class);
        verify(receiptLineRepository).save(captor.capture());
        verify(receiptRepository).save(receipt);

        assertThat(result.getStatus()).isEqualTo(ReceiptStatus.RETURNED);
        assertThat(receipt.getStatus()).isEqualTo(ReceiptStatus.RETURNED);
        assertThat(result.getLines()).hasSize(1);
        assertThat(result.getLines().get(0).getStatus()).isEqualTo(ReceiptLineStatus.ISSUED);

        ReceiptLine line = captor.getValue();
        assertThat(line.getStatus()).isEqualTo(ReceiptLineStatus.PENDING);
        assertThat(line.getConcept()).isEqualTo("Devolución recibo " + RECEIPT_ID);
        assertThat(line.getAmount()).isEqualByComparingTo(new BigDecimal("17.48"));
        assertThat(line.getPerson()).isSameAs(person);
    }

    //B1 generateReceipt Persona inexistente ResourceNotFoundException y verifyNoInteractions sobre receiptRepository y receiptLineRepository
    @Test
    void generateReceipt_whenPersonNotFound_throwsResourceNotFound(){
        GenerateReceiptRequestDTO request = new GenerateReceiptRequestDTO();
        request.setPersonId(MISSING_ID);

        when(personRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.generateReceipt(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(receiptRepository, receiptLineRepository);
    }

    //B2 generateReceipt Sin líneas (null o lista vacía) y includeQuota=false	InvalidBusinessRuleException y mismos verifyNoInteractions. Parametrizado con @NullAndEmptySource sobre un parámetro List<Long>
    @ParameterizedTest
    @NullAndEmptySource
    void generateReceipt_whenNoLinesAndIncludeQuotaFalse_throwsInvalidBusiness(List<Long> selectedReceiptLineIds) {
        GenerateReceiptRequestDTO request = new GenerateReceiptRequestDTO();
        request.setPersonId(PERSON_ID);
        request.setIncludeQuota(false);
        request.setSelectedReceiptLineIds(selectedReceiptLineIds);

        Person person = new Person();

        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(person));

        assertThatThrownBy(() -> service.generateReceipt(request))
                .isInstanceOf(InvalidBusinessRuleException.class);

        verifyNoInteractions(receiptRepository, receiptLineRepository);
    }

    //B3 generateReceipt Línea inexistente	ResourceNotFoundException
    @Test
    void generateReceipt_whenLineNotFound_throwsResourceNotFound() {
        GenerateReceiptRequestDTO request = new GenerateReceiptRequestDTO();
        request.setPersonId(PERSON_ID);
        request.setIncludeQuota(false);
        request.setSelectedReceiptLineIds(List.of(MISSING_ID));

        Person person = new Person();

        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(person));
        when(receiptLineRepository.findById(MISSING_ID)).thenReturn(Optional.empty());
        when(receiptRepository.save(any(Receipt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThatThrownBy(() -> service.generateReceipt(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(receiptLineRepository, never()).save(any(ReceiptLine.class));
    }

    //B4 generateReceipt Línea de otra persona	ResourceNotFoundException y la línea no se guarda
    @Test
    void generateReceipt_whenLineNotSamePerson_throwsResourceNotFound() {
        GenerateReceiptRequestDTO request = new GenerateReceiptRequestDTO();
        request.setPersonId(PERSON_ID);
        request.setIncludeQuota(false);
        request.setSelectedReceiptLineIds(List.of(RECEIPT_LINE_ID));

        Person person = new Person();

        Person personLine = new Person();
        personLine.setId(MISSING_ID);

        ReceiptLine line = new ReceiptLine();
        line.setPerson(personLine);

        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(person));
        when(receiptLineRepository.findById(RECEIPT_ID)).thenReturn(Optional.of(line));
        when(receiptRepository.save(any(Receipt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThatThrownBy(() -> service.generateReceipt(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(receiptLineRepository, never()).save(any(ReceiptLine.class));
    }

    //B5 generateReceipt Línea no PENDING	InvalidBusinessRuleException. Parametrizado ISSUED/PAID, y la línea no se guarda
    @ParameterizedTest
    @EnumSource(value = ReceiptLineStatus.class, names = "PENDING", mode = EnumSource.Mode.EXCLUDE)
    void generateReceipt_whenLineNotPending_throwsInvalidBusiness(ReceiptLineStatus status) {
        GenerateReceiptRequestDTO request = new GenerateReceiptRequestDTO();
        request.setPersonId(PERSON_ID);
        request.setIncludeQuota(false);
        request.setSelectedReceiptLineIds(List.of(RECEIPT_LINE_ID));

        Person person = new Person();
        person.setId(PERSON_ID);

        ReceiptLine line = new ReceiptLine();
        line.setPerson(person);
        line.setStatus(status);


        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(person));
        when(receiptLineRepository.findById(RECEIPT_LINE_ID)).thenReturn(Optional.of(line));
        when(receiptRepository.save(any(Receipt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThatThrownBy(() -> service.generateReceipt(request))
                .isInstanceOf(InvalidBusinessRuleException.class);

        verify(receiptLineRepository, never()).save(any(ReceiptLine.class));
    }

    //B6 generateReceipt Dos líneas PENDING y includeQuota=false	Total = suma (isEqualByComparingTo), líneas ISSUED y asociadas al recibo, save de línea 2 veces
    @Test
    void generateReceipt_whenTwoPendingLinesAndIncludeQuotaFalse_savesCorrectly() {
        GenerateReceiptRequestDTO request = new GenerateReceiptRequestDTO();
        request.setPersonId(PERSON_ID);
        request.setIncludeQuota(false);
        request.setSelectedReceiptLineIds(List.of(RECEIPT_LINE_ID, OTHER_RECEIPT_LINE_ID));

        Person person = new Person();
        person.setId(PERSON_ID);

        ReceiptLine line1 = new ReceiptLine();
        line1.setPerson(person);
        line1.setStatus(ReceiptLineStatus.PENDING);
        line1.setAmount(new BigDecimal("10.00"));

        ReceiptLine line2 = new ReceiptLine();
        line2.setPerson(person);
        line2.setStatus(ReceiptLineStatus.PENDING);
        line2.setAmount(new BigDecimal("5.50"));

        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(person));
        when(receiptLineRepository.findById(RECEIPT_LINE_ID)).thenReturn(Optional.of(line1));
        when(receiptLineRepository.findById(OTHER_RECEIPT_LINE_ID)).thenReturn(Optional.of(line2));
        when(receiptRepository.save(any(Receipt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Receipt> receiptCaptor = ArgumentCaptor.forClass(Receipt.class);

        ReceiptResponseDTO result = service.generateReceipt(request);

        verify(receiptRepository ,times(2)).save(receiptCaptor.capture());
        verify(receiptLineRepository,times(2)).save(any(ReceiptLine.class));

        Receipt savedReceipt = receiptCaptor.getValue();

        assertThat(savedReceipt.getTotal()).isEqualByComparingTo("15.50");
        assertThat(result.getAmount()).isEqualByComparingTo("15.50");
        assertThat(result.getStatus()).isEqualTo(ReceiptStatus.ISSUED);

        assertThat(line1.getStatus()).isEqualTo(ReceiptLineStatus.ISSUED);
        assertThat(line1.getReceipt()).isSameAs(savedReceipt);

        assertThat(line2.getStatus()).isEqualTo(ReceiptLineStatus.ISSUED);
        assertThat(line2.getReceipt()).isSameAs(savedReceipt);
    }

    //B7 generateReceipt includeQuota=true, FULL_PARTNER ACTIVE, sin líneas	Se crea una línea ISSUED de 15,00 con concepto que empieza por “Cuota Mensual”, y el total es 15,00. Captor sobre receiptLineRepository.save
    @Test
    void generateReceipt_whenIncludeQuotaTrueAndFullPartnerActiveAndNoLines_createsIssuedLineQuota() {
        GenerateReceiptRequestDTO request = new GenerateReceiptRequestDTO();
        request.setPersonId(PERSON_ID);
        request.setIncludeQuota(true);
        request.setSelectedReceiptLineIds(List.of());

        Person person = new Person();
        person.setId(PERSON_ID);
        person.setMembershipStatus(MembershipStatus.ACTIVE);
        person.setMembershipType(MembershipType.FULL_PARTNER);

        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(person));
        when(receiptRepository.save(any(Receipt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Receipt> receiptCaptor = ArgumentCaptor.forClass(Receipt.class);
        ArgumentCaptor<ReceiptLine> receiptLineCaptor = ArgumentCaptor.forClass(ReceiptLine.class);

        ReceiptResponseDTO result = service.generateReceipt(request);

        verify(receiptRepository ,times(2)).save(receiptCaptor.capture());
        verify(receiptLineRepository,times(1)).save(receiptLineCaptor.capture());

        Receipt savedReceipt = receiptCaptor.getValue();
        ReceiptLine savedReceiptLine = receiptLineCaptor.getValue();

        assertThat(savedReceipt.getTotal()).isEqualByComparingTo("15.00");
        assertThat(result.getAmount()).isEqualByComparingTo("15.00");
        assertThat(result.getStatus()).isEqualTo(ReceiptStatus.ISSUED);

        assertThat(savedReceiptLine.getStatus()).isEqualTo(ReceiptLineStatus.ISSUED);
        assertThat(savedReceiptLine.getReceipt()).isSameAs(savedReceipt);
        assertThat(savedReceiptLine.getAmount()).isEqualByComparingTo("15.00");
        assertThat(savedReceiptLine.getConcept()).startsWith("Cuota Mensual junio");
        assertThat(savedReceiptLine.getPerson()).isSameAs(person);
    }

    //B8 generateReceipt includeQuota=true, INITIATION_TRAINING, sin líneas	InvalidBusinessRuleException, porque lineCount queda en 0 y no hay cuota
    @Test
    void generateReceipt_whenIncludeQuotaTrueAndInitiationTrainingNoLines_throwsInvalidBusinessRule () {
        GenerateReceiptRequestDTO request = new GenerateReceiptRequestDTO();
        request.setPersonId(PERSON_ID);
        request.setIncludeQuota(true);
        request.setSelectedReceiptLineIds(List.of());

        Person person = new Person();
        person.setId(PERSON_ID);
        person.setMembershipType(MembershipType.INITIATION_TRAINING);

        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(person));

        assertThatThrownBy(() -> service.generateReceipt(request))
                .isInstanceOf(InvalidBusinessRuleException.class);

        verify(receiptRepository).save(any(Receipt.class));
        verifyNoInteractions(receiptLineRepository);
    }

    //B9 generateReceipt Líneas e includeQuota=true con INITIATION_TRAINING	Solo se guardan las líneas seleccionadas, sin línea de cuota extra
    @Test
    void generateReceipt_whenInitiationTrainingWithQuota_ignoresQuotaAndSavesOnlyLines () {
        GenerateReceiptRequestDTO request = new GenerateReceiptRequestDTO();
        request.setPersonId(PERSON_ID);
        request.setIncludeQuota(true);
        request.setSelectedReceiptLineIds(List.of(RECEIPT_LINE_ID));

        Person person = new Person();
        person.setId(PERSON_ID);
        person.setMembershipType(MembershipType.INITIATION_TRAINING);

        ReceiptLine line1 = new ReceiptLine();
        line1.setPerson(person);
        line1.setStatus(ReceiptLineStatus.PENDING);
        line1.setAmount(new BigDecimal("150.00"));

        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(person));
        when(receiptLineRepository.findById(RECEIPT_LINE_ID)).thenReturn(Optional.of(line1));
        when(receiptRepository.save(any(Receipt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Receipt> receiptCaptor = ArgumentCaptor.forClass(Receipt.class);

        ReceiptResponseDTO result = service.generateReceipt(request);

        verify(receiptRepository,times(2)).save(receiptCaptor.capture());
        verify(receiptLineRepository).save(any(ReceiptLine.class));

        Receipt savedReceipt = receiptCaptor.getValue();

        assertThat(savedReceipt.getTotal()).isEqualByComparingTo("150.00");
        assertThat(result.getAmount()).isEqualByComparingTo("150.00");
        assertThat(result.getStatus()).isEqualTo(ReceiptStatus.ISSUED);

        assertThat(line1.getStatus()).isEqualTo(ReceiptLineStatus.ISSUED);
        assertThat(line1.getReceipt()).isSameAs(savedReceipt);
    }

    //C1 getReceiptsByPersonId Ni admin ni propietario	AccessDeniedException y verifyNoInteractions(personRepository, receiptRepository)
    @Test
    void getReceiptsByPersonId_whenUserNotAdminAndNotOwner_throwsAccessDenied () {
        when(currentUserProvider.isAdminOrOwner(PERSON_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.getReceiptsByPersonId(PERSON_ID))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(personRepository, receiptRepository);
    }

    //C2 getReceiptsByPersonId Con permiso, persona inexistente	ResourceNotFoundException y verifyNoInteractions(receiptRepository). existsById devuelve false
    @Test
    void getReceiptsByPersonId_whenUserHasPermissionsAndPersonDoesNotExists_throwsResourceNotFound () {
        when(currentUserProvider.isAdminOrOwner(MISSING_ID)).thenReturn(true);
        when(personRepository.existsById(MISSING_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.getReceiptsByPersonId(MISSING_ID))
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(receiptRepository);
    }

    //C3 getReceiptsByPersonId Caso válido	Lista de recibos mapeada, con la persona del recibo con id
    @Test
    void getReceiptsByPersonId_whenUserHasPermissionsAndPersonExists_returnsMappedReceipts () {
        Person person = new Person();
        person.setId(PERSON_ID);

        Receipt receiptFirst = new Receipt();
        receiptFirst.setId(1L);
        receiptFirst.setPerson(person);

        Receipt receiptSecond = new Receipt();
        receiptSecond.setId(2L);
        receiptSecond.setPerson(person);

        List<Receipt> receipts = List.of(receiptFirst, receiptSecond);

        when(currentUserProvider.isAdminOrOwner(PERSON_ID)).thenReturn(true);
        when(personRepository.existsById(PERSON_ID)).thenReturn(true);
        when(receiptRepository.findByPersonIdOrderByIssueDateDescIdDesc(PERSON_ID)).thenReturn(receipts);

        List<ReceiptResponseDTO> result = service.getReceiptsByPersonId(PERSON_ID);

        verify(receiptRepository).findByPersonIdOrderByIssueDateDescIdDesc(PERSON_ID);
        verifyNoMoreInteractions(receiptRepository);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
    }

    //C4 getReceiptDetail Recibo inexistente y usuario no admin	AccessDeniedException (no 404): ownerId es null, así que isAdminOrOwner(null) devuelve false para un USER. Es la regla "403 antes que 404"
    @Test
    void getReceiptDetail_whenReceiptDoesNotExistAndUserIsNotAdmin_throwsAccessDeniedException () {
        when(receiptRepository.findById(MISSING_ID)).thenReturn(Optional.empty());
        when(currentUserProvider.isAdminOrOwner(null)).thenReturn(false);

        assertThatThrownBy(() -> service.getReceiptDetail(MISSING_ID))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(receiptLineRepository);
    }

    //C5 getReceiptDetail Recibo inexistente y admin	ResourceNotFoundException. El when sobre isAdminOrOwner(null) devuelve true
    @Test
    void getReceiptDetail_whenReceiptDoesNotExistAndUserAdmin_throwsNotFoundException () {
        when(receiptRepository.findById(MISSING_ID)).thenReturn(Optional.empty());
        when(currentUserProvider.isAdminOrOwner(null)).thenReturn(true);

        assertThatThrownBy(() -> service.getReceiptDetail(MISSING_ID))
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(receiptLineRepository);
    }

    //C6 getReceiptDetail Recibo ajeno	AccessDeniedException y no se consultan las líneas
    @Test
    void getReceiptDetail_whenReceiptIsNotOwnedByUser_throwsAccessDenied () {
        Person otherPerson = new Person();
        otherPerson.setId(OTHER_PERSON_ID);

        Receipt receipt = new Receipt();
        receipt.setId(RECEIPT_ID);
        receipt.setPerson(otherPerson);

        when(receiptRepository.findById(RECEIPT_ID)).thenReturn(Optional.of(receipt));
        when(currentUserProvider.isAdminOrOwner(OTHER_PERSON_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.getReceiptDetail(RECEIPT_ID))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(receiptLineRepository);
    }

    //C7 getReceiptDetail Caso válido	DTO con las líneas del recibo
    @Test
    void getReceiptDetail_whenReceiptValid_returnsReceiptDetailDto () {
        Person person = new Person();
        person.setId(PERSON_ID);

        Receipt receipt = new Receipt();
        receipt.setId(RECEIPT_ID);
        receipt.setPerson(person);

        ReceiptLine lineFirst = new ReceiptLine();
        lineFirst.setConcept("Line first");
        lineFirst.setAmount(new BigDecimal("100.00"));
        lineFirst.setPerson(person);

        ReceiptLine lineSecond = new ReceiptLine();
        lineSecond.setConcept("Line second");
        lineSecond.setAmount(new BigDecimal("50.00"));
        lineSecond.setPerson(person);

        when(receiptRepository.findById(RECEIPT_ID)).thenReturn(Optional.of(receipt));
        when(currentUserProvider.isAdminOrOwner(PERSON_ID)).thenReturn(true);
        when(receiptLineRepository.findByReceiptIdOrderByDateDescIdDesc(RECEIPT_ID))
                .thenReturn(List.of(lineFirst, lineSecond));

        ReceiptDetailResponseDTO result = service.getReceiptDetail(RECEIPT_ID);

        assertThat(result.getId()).isEqualTo(RECEIPT_ID);
        assertThat(result.getPerson().getId()).isEqualTo(PERSON_ID);
        assertThat(result.getLines()).hasSize(2);
        assertThat(result.getLines().get(0).getConcept()).isEqualTo("Line first");
        assertThat(result.getLines().get(1).getConcept()).isEqualTo("Line second");
    }

    //D1 getReceiptProposal Persona INITIATION_TRAINING sin líneas pendientes	No aparece en la propuesta
    @Test
    void getReceiptProposal_whenInitiationTrainingWithoutPendingLines_isNotInProposal () {
        Person person = new Person();
        person.setId(PERSON_ID);
        person.setMembershipType(MembershipType.INITIATION_TRAINING);

        when(personRepository.findByMembershipStatusNot(MembershipStatus.CANCELLED)).thenReturn(List.of(person));
        when(receiptLineService.findByPerson(PERSON_ID, ReceiptLineStatus.PENDING)).thenReturn(List.of());

        List<ReceiptProposalResponseDTO> result = service.getReceiptProposal();

        assertThat(result).isEmpty();
    }

    //D2 getReceiptProposal INITIATION_TRAINING con línea pendiente	Aparece, sin cuota (proposedQuota nulo)
    @Test
    void getReceiptProposal_whenInitiationTrainingWithPendingLine_isInProposalWithoutQuota () {
        Person person = new Person();
        person.setId(PERSON_ID);
        person.setMembershipType(MembershipType.INITIATION_TRAINING);

        ReceiptLineResponseDTO pendingLine = new ReceiptLineResponseDTO();

        when(personRepository.findByMembershipStatusNot(MembershipStatus.CANCELLED)).thenReturn(List.of(person));
        when(receiptLineService.findByPerson(PERSON_ID, ReceiptLineStatus.PENDING)).thenReturn(List.of(pendingLine));

        List<ReceiptProposalResponseDTO> result = service.getReceiptProposal();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPerson().getId()).isEqualTo(PERSON_ID);
        assertThat(result.get(0).getPendingLines()).hasSize(1);
        assertThat(result.get(0).getPendingLines().get(0)).isSameAs(pendingLine);
        assertThat(result.get(0).getProposedQuota()).isNull();
        assertThat(result.get(0).getQuotaConcept()).isNull();
    }

    //D3 getReceiptProposal FULL_PARTNER y SUBSCRIBED_MEMBER ACTIVE sin líneas	Aparece con cuota 15,00 y concepto "Cuota Mensual junio"
    @ParameterizedTest
    @EnumSource(value = MembershipType.class, names = {"FULL_PARTNER", "SUBSCRIBED_MEMBER"})
    void getReceiptProposal_whenMemberActiveWithoutLines_isInProposalWithMonthlyQuota (MembershipType type) {
        Person person = new Person();
        person.setId(PERSON_ID);
        person.setMembershipType(type);
        person.setMembershipStatus(MembershipStatus.ACTIVE);

        when(personRepository.findByMembershipStatusNot(MembershipStatus.CANCELLED)).thenReturn(List.of(person));
        when(receiptLineService.findByPerson(PERSON_ID, ReceiptLineStatus.PENDING)).thenReturn(List.of());

        List<ReceiptProposalResponseDTO> result = service.getReceiptProposal();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPerson().getId()).isEqualTo(PERSON_ID);
        assertThat(result.get(0).getPendingLines()).isEmpty();
        assertThat(result.get(0).getProposedQuota()).isEqualByComparingTo("15.00");
        assertThat(result.get(0).getQuotaConcept()).isEqualTo("Cuota Mensual junio");
    }

    //D4 getReceiptProposal PERMANENT_TRAINING	Aparece con cuota mensual (calculateQuota no lee el estado en esta rama: no se informa)
    @Test
    void getReceiptProposal_whenPermanentTrainingWithoutLines_isInProposalWithMonthlyQuota () {
        Person person = new Person();
        person.setId(PERSON_ID);
        person.setMembershipType(MembershipType.PERMANENT_TRAINING);

        when(personRepository.findByMembershipStatusNot(MembershipStatus.CANCELLED)).thenReturn(List.of(person));
        when(receiptLineService.findByPerson(PERSON_ID, ReceiptLineStatus.PENDING)).thenReturn(List.of());

        List<ReceiptProposalResponseDTO> result = service.getReceiptProposal();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPerson().getId()).isEqualTo(PERSON_ID);
        assertThat(result.get(0).getPendingLines()).isEmpty();
        assertThat(result.get(0).getProposedQuota()).isEqualByComparingTo("15.00");
        assertThat(result.get(0).getQuotaConcept()).isEqualTo("Cuota Mensual junio");
    }

    //D4b getReceiptProposal PERMANENT_TRAINING con línea pendiente	Aparece con la línea y con la cuota mensual (la cuota no depende de las líneas)
    @Test
    void getReceiptProposal_whenPermanentTrainingWithPendingLine_isInProposalWithLineAndQuota () {
        Person person = new Person();
        person.setId(PERSON_ID);
        person.setMembershipType(MembershipType.PERMANENT_TRAINING);

        ReceiptLineResponseDTO pendingLine = new ReceiptLineResponseDTO();

        when(personRepository.findByMembershipStatusNot(MembershipStatus.CANCELLED)).thenReturn(List.of(person));
        when(receiptLineService.findByPerson(PERSON_ID, ReceiptLineStatus.PENDING)).thenReturn(List.of(pendingLine));

        List<ReceiptProposalResponseDTO> result = service.getReceiptProposal();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPendingLines()).hasSize(1);
        assertThat(result.get(0).getProposedQuota()).isEqualByComparingTo("15.00");
        assertThat(result.get(0).getQuotaConcept()).isEqualTo("Cuota Mensual junio");
    }

    //D5 getReceiptProposal INACTIVE fuera de enero sin líneas	No aparece
    @ParameterizedTest
    @EnumSource(value = MembershipType.class, names = {"FULL_PARTNER", "SUBSCRIBED_MEMBER"})
    void getReceiptProposal_whenMemberInactiveOutsideJanuaryWithoutLines_isNotInProposal (MembershipType type) {
        Person person = new Person();
        person.setId(PERSON_ID);
        person.setMembershipType(type);
        person.setMembershipStatus(MembershipStatus.INACTIVE);

        when(personRepository.findByMembershipStatusNot(MembershipStatus.CANCELLED)).thenReturn(List.of(person));
        when(receiptLineService.findByPerson(PERSON_ID, ReceiptLineStatus.PENDING)).thenReturn(List.of());

        List<ReceiptProposalResponseDTO> result = service.getReceiptProposal();

        assertThat(result).isEmpty();
    }

    //D6 getReceiptProposal INACTIVE fuera de enero con línea pendiente	Aparece solo con la línea, sin cuota
    @ParameterizedTest
    @EnumSource(value = MembershipType.class, names = {"FULL_PARTNER", "SUBSCRIBED_MEMBER"})
    void getReceiptProposal_whenMemberInactiveOutsideJanuaryWithPendingLine_isInProposalWithoutQuota (MembershipType type) {
        Person person = new Person();
        person.setId(PERSON_ID);
        person.setMembershipType(type);
        person.setMembershipStatus(MembershipStatus.INACTIVE);

        ReceiptLineResponseDTO pendingLine = new ReceiptLineResponseDTO();

        when(personRepository.findByMembershipStatusNot(MembershipStatus.CANCELLED)).thenReturn(List.of(person));
        when(receiptLineService.findByPerson(PERSON_ID, ReceiptLineStatus.PENDING)).thenReturn(List.of(pendingLine));

        List<ReceiptProposalResponseDTO> result = service.getReceiptProposal();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPerson().getId()).isEqualTo(PERSON_ID);
        assertThat(result.get(0).getPendingLines()).hasSize(1);
        assertThat(result.get(0).getPendingLines().get(0)).isSameAs(pendingLine);
        assertThat(result.get(0).getProposedQuota()).isNull();
        assertThat(result.get(0).getQuotaConcept()).isNull();
    }

    //D7 getReceiptProposal INACTIVE en enero (Clock fijo en enero)	Cuota anual 15,00 con concepto "Cuota Anual " + año
    @ParameterizedTest
    @EnumSource(value = MembershipType.class, names = {"FULL_PARTNER", "SUBSCRIBED_MEMBER"})
    void getReceiptProposal_whenMemberInactiveInJanuary_isInProposalWithAnnualQuota (MembershipType type) {
        // El @InjectMocks usa el reloj de junio: aquí se construye el Service a mano con un reloj fijo en enero (año distinto al de junio)
        Clock januaryClock = Clock.fixed(Instant.parse("2027-01-15T10:00:00Z"), ZoneId.of("Europe/Madrid"));
        ReceiptServiceImpl januaryService = new ReceiptServiceImpl(personRepository, receiptLineService,
                receiptLineRepository, receiptMapper, receiptRepository, receiptLineMapper,
                currentUserProvider, januaryClock);

        Person person = new Person();
        person.setId(PERSON_ID);
        person.setMembershipType(type);
        person.setMembershipStatus(MembershipStatus.INACTIVE);

        when(personRepository.findByMembershipStatusNot(MembershipStatus.CANCELLED)).thenReturn(List.of(person));
        when(receiptLineService.findByPerson(PERSON_ID, ReceiptLineStatus.PENDING)).thenReturn(List.of());

        List<ReceiptProposalResponseDTO> result = januaryService.getReceiptProposal();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPerson().getId()).isEqualTo(PERSON_ID);
        assertThat(result.get(0).getPendingLines()).isEmpty();
        assertThat(result.get(0).getProposedQuota()).isEqualByComparingTo("15.00");
        assertThat(result.get(0).getQuotaConcept()).isEqualTo("Cuota Anual 2027");
    }

    //D8 getReceiptProposal Pide al repositorio las personas con findByMembershipStatusNot(CANCELLED): el filtro lo hace el repositorio, no el Service
    @Test
    void getReceiptProposal_asksRepositoryForNonCancelledPeople () {
        when(personRepository.findByMembershipStatusNot(MembershipStatus.CANCELLED)).thenReturn(List.of());

        List<ReceiptProposalResponseDTO> result = service.getReceiptProposal();

        assertThat(result).isEmpty();

        verify(personRepository).findByMembershipStatusNot(MembershipStatus.CANCELLED);
        verifyNoMoreInteractions(personRepository);
        verifyNoInteractions(receiptLineService);
    }

    //D9 getReceiptProposal Varias personas	El orden de salida respeta el de las personas y solo entran las que cumplen la condición
    @Test
    void getReceiptProposal_whenSeveralPeople_keepsPeopleOrderAndOnlyIncludesEligible () {
        // Orden de entrada deliberadamente no creciente por id, para detectar si el Service reordena
        Person permanent = new Person();
        permanent.setId(THIRD_PERSON_ID);
        permanent.setMembershipType(MembershipType.PERMANENT_TRAINING);

        Person activeMember = new Person();
        activeMember.setId(PERSON_ID);
        activeMember.setMembershipType(MembershipType.FULL_PARTNER);
        activeMember.setMembershipStatus(MembershipStatus.ACTIVE);

        Person initiationWithoutLines = new Person();
        initiationWithoutLines.setId(OTHER_PERSON_ID);
        initiationWithoutLines.setMembershipType(MembershipType.INITIATION_TRAINING);

        Person inactiveWithLine = new Person();
        inactiveWithLine.setId(FOURTH_PERSON_ID);
        inactiveWithLine.setMembershipType(MembershipType.SUBSCRIBED_MEMBER);
        inactiveWithLine.setMembershipStatus(MembershipStatus.INACTIVE);

        Person inactiveWithoutLines = new Person();
        inactiveWithoutLines.setId(FIFTH_PERSON_ID);
        inactiveWithoutLines.setMembershipType(MembershipType.FULL_PARTNER);
        inactiveWithoutLines.setMembershipStatus(MembershipStatus.INACTIVE);

        when(personRepository.findByMembershipStatusNot(MembershipStatus.CANCELLED))
                .thenReturn(List.of(permanent, activeMember, initiationWithoutLines, inactiveWithLine, inactiveWithoutLines));
        when(receiptLineService.findByPerson(THIRD_PERSON_ID, ReceiptLineStatus.PENDING)).thenReturn(List.of());
        when(receiptLineService.findByPerson(PERSON_ID, ReceiptLineStatus.PENDING)).thenReturn(List.of());
        when(receiptLineService.findByPerson(OTHER_PERSON_ID, ReceiptLineStatus.PENDING)).thenReturn(List.of());
        when(receiptLineService.findByPerson(FOURTH_PERSON_ID, ReceiptLineStatus.PENDING)).thenReturn(List.of(new ReceiptLineResponseDTO()));
        when(receiptLineService.findByPerson(FIFTH_PERSON_ID, ReceiptLineStatus.PENDING)).thenReturn(List.of());

        List<ReceiptProposalResponseDTO> result = service.getReceiptProposal();

        assertThat(result).extracting(proposal -> proposal.getPerson().getId())
                .containsExactly(THIRD_PERSON_ID, PERSON_ID, FOURTH_PERSON_ID);

        assertThat(result.get(0).getProposedQuota()).isEqualByComparingTo("15.00");
        assertThat(result.get(1).getProposedQuota()).isEqualByComparingTo("15.00");
        assertThat(result.get(2).getProposedQuota()).isNull();
        assertThat(result.get(2).getPendingLines()).hasSize(1);
    }
}