package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.exception.InvalidBusinessRuleException;
import com.managementClub.managementClub.exception.ResourceNotFoundException;
import com.managementClub.managementClub.model.dto.GenerateReceiptBatchResponseDTO;
import com.managementClub.managementClub.model.dto.GenerateReceiptRequestDTO;
import com.managementClub.managementClub.model.dto.ReceiptGenerationResultDTO;
import com.managementClub.managementClub.model.dto.ReceiptResponseDTO;
import com.managementClub.managementClub.service.ReceiptService;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptBatchServiceImplTest {

    @Mock private ReceiptService receiptService;

    @InjectMocks private ReceiptBatchServiceImpl service;

    private static final Long PERSON_ID = 5L;
    private static final Long OTHER_PERSON_ID = 6L;
    private static final Long THIRD_PERSON_ID = 7L;

    // Dos fallos controlados distintos, con mensajes distintos entre sí: el resultado debe llevar el mensaje de la excepción que salta
    static Stream<Arguments> controlledFailures() {
        return Stream.of(
                Arguments.of(Named.of("ResourceNotFoundException", new ResourceNotFoundException("Persona no encontrada."))),
                Arguments.of(Named.of("InvalidBusinessRuleException", new InvalidBusinessRuleException("Sin líneas seleccionadas ni cuota incluida."))));
    }

    private GenerateReceiptRequestDTO request(Long personId) {
        GenerateReceiptRequestDTO request = new GenerateReceiptRequestDTO();
        request.setPersonId(personId);
        return request;
    }

    private ReceiptResponseDTO receiptWithId(Long id) {
        ReceiptResponseDTO receipt = new ReceiptResponseDTO();
        receipt.setId(id);
        return receipt;
    }

    //B1 generateReceiptBatch Lista vacía -> sin resultados, contadores 0 y 0, y no se llama a generateReceipt
    @Test
    void generateReceiptBatch_whenEmptyList_returnsEmptyResultsAndZeroCounters() {
        GenerateReceiptBatchResponseDTO result = service.generateReceiptBatch(List.of());

        assertThat(result.getResults()).isEmpty();
        assertThat(result.getSuccessCount()).isZero();
        assertThat(result.getFailureCount()).isZero();
        verifyNoInteractions(receiptService);
    }

    //B2 generateReceiptBatch Dos peticiones válidas -> dos resultados en orden, success true, el recibo es el devuelto por generateReceipt (misma instancia), sin mensaje de error; contadores 2 y 0
    @Test
    void generateReceiptBatch_whenTwoValidRequests_returnsTwoSuccessfulResults() {
        GenerateReceiptRequestDTO first = request(PERSON_ID);
        GenerateReceiptRequestDTO second = request(OTHER_PERSON_ID);
        ReceiptResponseDTO firstReceipt = receiptWithId(1L);
        ReceiptResponseDTO secondReceipt = receiptWithId(2L);

        when(receiptService.generateReceipt(first)).thenReturn(firstReceipt);
        when(receiptService.generateReceipt(second)).thenReturn(secondReceipt);

        GenerateReceiptBatchResponseDTO result = service.generateReceiptBatch(List.of(first, second));

        assertThat(result.getResults()).hasSize(2);

        ReceiptGenerationResultDTO firstResult = result.getResults().get(0);
        assertThat(firstResult.getPersonId()).isEqualTo(PERSON_ID);
        assertThat(firstResult.getSuccess()).isTrue();
        assertThat(firstResult.getReceipt()).isSameAs(firstReceipt);
        assertThat(firstResult.getErrorMessage()).isNull();

        ReceiptGenerationResultDTO secondResult = result.getResults().get(1);
        assertThat(secondResult.getPersonId()).isEqualTo(OTHER_PERSON_ID);
        assertThat(secondResult.getSuccess()).isTrue();
        assertThat(secondResult.getReceipt()).isSameAs(secondReceipt);
        assertThat(secondResult.getErrorMessage()).isNull();

        assertThat(result.getSuccessCount()).isEqualTo(2);
        assertThat(result.getFailureCount()).isZero();
    }

    //B3 generateReceiptBatch Una petición falla con ResourceNotFoundException o con InvalidBusinessRuleException -> success false, sin recibo, errorMessage igual al de la excepción; contadores 0 y 1; la excepción no se propaga
    @ParameterizedTest(name = "{0}")
    @MethodSource("controlledFailures")
    void generateReceiptBatch_whenRequestFailsWithControlledException_returnsFailedResultWithExceptionMessage(RuntimeException failure) {
        GenerateReceiptRequestDTO request = request(PERSON_ID);

        when(receiptService.generateReceipt(request)).thenThrow(failure);

        AtomicReference<GenerateReceiptBatchResponseDTO> response = new AtomicReference<>();
        // El fallo controlado no se propaga: se convierte en un resultado fallido
        assertThatCode(() -> response.set(service.generateReceiptBatch(List.of(request))))
                .doesNotThrowAnyException();
        GenerateReceiptBatchResponseDTO result = response.get();

        assertThat(result.getResults()).hasSize(1);

        ReceiptGenerationResultDTO failed = result.getResults().get(0);
        assertThat(failed.getPersonId()).isEqualTo(PERSON_ID);
        assertThat(failed.getSuccess()).isFalse();
        assertThat(failed.getReceipt()).isNull();
        assertThat(failed.getErrorMessage()).isEqualTo(failure.getMessage());

        assertThat(result.getSuccessCount()).isZero();
        assertThat(result.getFailureCount()).isEqualTo(1);
    }

    //B4 generateReceiptBatch Mezcla éxito-fallo-éxito -> el fallo no corta el lote: orden conservado, contadores 2 y 1 y generateReceipt llamado 3 veces (una por petición)
    @Test
    void generateReceiptBatch_whenSuccessFailureSuccess_keepsOrderAndProcessesAllRequests() {
        GenerateReceiptRequestDTO first = request(PERSON_ID);
        GenerateReceiptRequestDTO second = request(OTHER_PERSON_ID);
        GenerateReceiptRequestDTO third = request(THIRD_PERSON_ID);
        ReceiptResponseDTO firstReceipt = receiptWithId(1L);
        ReceiptResponseDTO thirdReceipt = receiptWithId(3L);

        when(receiptService.generateReceipt(first)).thenReturn(firstReceipt);
        when(receiptService.generateReceipt(second)).thenThrow(new InvalidBusinessRuleException("Sin líneas seleccionadas ni cuota incluida."));
        when(receiptService.generateReceipt(third)).thenReturn(thirdReceipt);

        AtomicReference<GenerateReceiptBatchResponseDTO> response = new AtomicReference<>();
        // El fallo controlado de la segunda petición no se propaga ni corta el lote
        assertThatCode(() -> response.set(service.generateReceiptBatch(List.of(first, second, third))))
                .doesNotThrowAnyException();
        GenerateReceiptBatchResponseDTO result = response.get();

        verify(receiptService, times(3)).generateReceipt(any(GenerateReceiptRequestDTO.class));

        assertThat(result.getResults()).extracting(ReceiptGenerationResultDTO::getPersonId)
                .containsExactly(PERSON_ID, OTHER_PERSON_ID, THIRD_PERSON_ID);
        assertThat(result.getResults()).extracting(ReceiptGenerationResultDTO::getSuccess)
                .containsExactly(true, false, true);
        assertThat(result.getResults().get(0).getReceipt()).isSameAs(firstReceipt);
        assertThat(result.getResults().get(1).getReceipt()).isNull();
        assertThat(result.getResults().get(1).getErrorMessage()).isEqualTo("Sin líneas seleccionadas ni cuota incluida.");
        assertThat(result.getResults().get(2).getReceipt()).isSameAs(thirdReceipt);

        assertThat(result.getSuccessCount()).isEqualTo(2);
        assertThat(result.getFailureCount()).isEqualTo(1);
    }
}
