package com.managementClub.managementClub.service;

import com.managementClub.managementClub.model.dto.ReceiptProposalResponseDTO;

import java.util.List;

public interface ReceiptService {

    List<ReceiptProposalResponseDTO> getReceiptProposal();
}
