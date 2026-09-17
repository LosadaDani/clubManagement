package com.managementClub.managementClub.service;

import com.managementClub.managementClub.model.dto.GenerateReceiptRequestDTO;
import com.managementClub.managementClub.model.dto.ReceiptDetailResponseDTO;
import com.managementClub.managementClub.model.dto.ReceiptProposalResponseDTO;
import com.managementClub.managementClub.model.dto.ReceiptResponseDTO;

import java.util.List;

public interface ReceiptService {

    List<ReceiptProposalResponseDTO> getReceiptProposal();

    ReceiptResponseDTO generateReceipt(GenerateReceiptRequestDTO request);

    List<ReceiptResponseDTO> getReceiptsByPersonId(Long personId);

    ReceiptDetailResponseDTO getReceiptDetail(Long receiptId);

    ReceiptDetailResponseDTO markAsPaid(Long receiptId);

    ReceiptDetailResponseDTO markAsReturned(Long receiptId);


}
