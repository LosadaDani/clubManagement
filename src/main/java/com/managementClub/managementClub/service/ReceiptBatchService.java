package com.managementClub.managementClub.service;

import com.managementClub.managementClub.model.dto.GenerateReceiptBatchResponseDTO;
import com.managementClub.managementClub.model.dto.GenerateReceiptRequestDTO;

import java.util.List;

public interface ReceiptBatchService {
    GenerateReceiptBatchResponseDTO generateReceiptBatch(List<GenerateReceiptRequestDTO> requests);
}
