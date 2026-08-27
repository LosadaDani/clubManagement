package com.managementClub.managementClub.service;

import com.managementClub.managementClub.model.dto.ReceiptLineRequestDTO;
import com.managementClub.managementClub.model.dto.ReceiptLineResponseDTO;

public interface ReceiptLineService {

    ReceiptLineResponseDTO createReceiptLine(ReceiptLineRequestDTO receiptLineRequestDTO);
}
