package com.managementClub.managementClub.controller;

import com.managementClub.managementClub.controller.documentation.ReceiptControllerDocs;
import com.managementClub.managementClub.model.dto.*;
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

    @Override
    @GetMapping("/by-person/{id}")
    public ResponseEntity<List<ReceiptResponseDTO>> getReceiptsByPersonId(@PathVariable Long id) {
        List<ReceiptResponseDTO> response = receiptService.getReceiptsByPersonId(id);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ReceiptDetailResponseDTO> getReceiptDetail(@PathVariable Long id) {
        ReceiptDetailResponseDTO response = receiptService.getReceiptDetail(id);
        return ResponseEntity.ok(response);
    }

    @Override
    @PatchMapping("/{id}/pay")
    public ResponseEntity<ReceiptDetailResponseDTO> markAsPaid(@PathVariable Long id) {
        ReceiptDetailResponseDTO response = receiptService.markAsPaid(id);
        return ResponseEntity.ok(response);
    }

    @Override
    @PatchMapping("/{id}/return")
    public ResponseEntity<ReceiptDetailResponseDTO> markAsReturned(@PathVariable Long id) {
        ReceiptDetailResponseDTO response = receiptService.markAsReturned(id);
        return ResponseEntity.ok(response);
    }

}
