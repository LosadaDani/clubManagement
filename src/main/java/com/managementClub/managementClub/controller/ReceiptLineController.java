package com.managementClub.managementClub.controller;

import com.managementClub.managementClub.controller.documentation.ReceiptLineControllerDocs;
import com.managementClub.managementClub.model.dto.ReceiptLineRequestDTO;
import com.managementClub.managementClub.model.dto.ReceiptLineResponseDTO;
import com.managementClub.managementClub.service.ReceiptLineService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/receipt-lines")
public class ReceiptLineController implements ReceiptLineControllerDocs {

    private final ReceiptLineService receiptLineService;

    public ReceiptLineController(ReceiptLineService receiptLineService) {
        this.receiptLineService = receiptLineService;
    }

    @PostMapping
    @Override
    public ResponseEntity<ReceiptLineResponseDTO> createReceiptLine(@Valid @RequestBody ReceiptLineRequestDTO requestDTO) {
        ReceiptLineResponseDTO responseDTO = receiptLineService.createReceiptLine(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
}
