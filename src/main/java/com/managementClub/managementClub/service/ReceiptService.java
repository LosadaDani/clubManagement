package com.managementClub.managementClub.service;

import com.managementClub.managementClub.model.dto.GenerateReceiptRequestDTO;
import com.managementClub.managementClub.model.dto.ReceiptProposalResponseDTO;
import com.managementClub.managementClub.model.dto.ReceiptResponseDTO;

import java.util.List;

public interface ReceiptService {

    List<ReceiptProposalResponseDTO> getReceiptProposal();

    ReceiptResponseDTO generateReceipt(GenerateReceiptRequestDTO request);

}
