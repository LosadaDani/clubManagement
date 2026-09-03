package com.managementClub.managementClub.service;

import com.managementClub.managementClub.model.dto.ReceiptLineRequestDTO;
import com.managementClub.managementClub.model.dto.ReceiptLineResponseDTO;
import com.managementClub.managementClub.model.enums.ReceiptLineStatus;

import java.util.List;

public interface ReceiptLineService {

    ReceiptLineResponseDTO createReceiptLine(ReceiptLineRequestDTO receiptLineRequestDTO);

    List<ReceiptLineResponseDTO> findByPerson(Long personId, ReceiptLineStatus status);

}
