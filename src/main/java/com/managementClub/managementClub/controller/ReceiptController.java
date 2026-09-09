package com.managementClub.managementClub.controller;

import com.managementClub.managementClub.controller.documentation.ReceiptControllerDocs;
import com.managementClub.managementClub.model.dto.ReceiptProposalResponseDTO;
import com.managementClub.managementClub.service.ReceiptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/receipt")
public class ReceiptController implements ReceiptControllerDocs {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @Override
    @GetMapping("/proposal")
    public ResponseEntity<List<ReceiptProposalResponseDTO>> getReceiptProposal() {
        List<ReceiptProposalResponseDTO> response = receiptService.getReceiptProposal();
        return ResponseEntity.ok(response);
    }

    /*



    POST /api/receipt/generate            → @Valid @RequestBody GenerateReceiptRequestDTO una persona
POST /api/receipt/generate-batch      → @Valid @RequestBody List<@Valid GenerateReceiptRequestDTO> varias personas

@PostMapping("/generate-batch")
public ResponseEntity<GenerateReceiptBatchResponseDTO> generateReceiptsBatch(
        @Valid @RequestBody List<@Valid GenerateReceiptRequestDTO> requests) {
    ...
}
     */
}
