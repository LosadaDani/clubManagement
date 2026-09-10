package com.managementClub.managementClub.controller;

import com.managementClub.managementClub.controller.documentation.ReceiptControllerDocs;
import com.managementClub.managementClub.model.dto.GenerateReceiptBatchResponseDTO;
import com.managementClub.managementClub.model.dto.GenerateReceiptRequestDTO;
import com.managementClub.managementClub.model.dto.ReceiptProposalResponseDTO;
import com.managementClub.managementClub.model.dto.ReceiptResponseDTO;
import com.managementClub.managementClub.service.ReceiptBatchService;
import com.managementClub.managementClub.service.ReceiptService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/receipt")
public class ReceiptController implements ReceiptControllerDocs {

    private final ReceiptService receiptService;
    private final ReceiptBatchService receiptBatchService;

    public ReceiptController(ReceiptService receiptService, ReceiptBatchService receiptBatchService) {
        this.receiptService = receiptService;
        this.receiptBatchService = receiptBatchService;
    }

    @Override
    @GetMapping("/proposal")
    public ResponseEntity<List<ReceiptProposalResponseDTO>> getReceiptProposal() {
        List<ReceiptProposalResponseDTO> response = receiptService.getReceiptProposal();
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/generate")
    public ResponseEntity<ReceiptResponseDTO> generateReceipt(@Valid @RequestBody GenerateReceiptRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(receiptService.generateReceipt(request));
    }

    @Override
    @PostMapping("/generate-batch")
    public ResponseEntity<GenerateReceiptBatchResponseDTO> generateReceiptBatch(@Valid @RequestBody List<@Valid GenerateReceiptRequestDTO> requests) {
        GenerateReceiptBatchResponseDTO response = receiptBatchService.generateReceiptBatch(requests);
        return ResponseEntity.ok(response);
    }

}
