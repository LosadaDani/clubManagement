package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.exception.InvalidBusinessRuleException;
import com.managementClub.managementClub.exception.ResourceNotFoundException;
import com.managementClub.managementClub.model.dto.GenerateReceiptBatchResponseDTO;
import com.managementClub.managementClub.model.dto.GenerateReceiptRequestDTO;
import com.managementClub.managementClub.model.dto.ReceiptGenerationResultDTO;
import com.managementClub.managementClub.model.dto.ReceiptResponseDTO;
import com.managementClub.managementClub.service.ReceiptBatchService;
import com.managementClub.managementClub.service.ReceiptService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReceiptBatchServiceImpl implements ReceiptBatchService {

    private final ReceiptService receiptService;

    public ReceiptBatchServiceImpl(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @Override
    public GenerateReceiptBatchResponseDTO generateReceiptBatch(List<GenerateReceiptRequestDTO> requests) {
        List<ReceiptGenerationResultDTO> results = new ArrayList<>();

        for (GenerateReceiptRequestDTO request : requests) {
            try {
                ReceiptResponseDTO receipt = receiptService.generateReceipt(request);
                results.add(new ReceiptGenerationResultDTO(
                        request.getPersonId(),
                        true,
                        receipt,
                        null));
            } catch (ResourceNotFoundException | InvalidBusinessRuleException e) {
                results.add(new ReceiptGenerationResultDTO(
                        request.getPersonId(),
                        false,
                        null,
                        e.getMessage()));
            }
        }

        long successCount = results.stream().filter(ReceiptGenerationResultDTO::getSuccess).count();
        long failureCount = results.size() - successCount;

        return new GenerateReceiptBatchResponseDTO(results, (int) successCount, (int) failureCount);
    }
}
