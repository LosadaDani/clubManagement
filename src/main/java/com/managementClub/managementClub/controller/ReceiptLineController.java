package com.managementClub.managementClub.controller;

import com.managementClub.managementClub.controller.documentation.ReceiptLineControllerDocs;
import com.managementClub.managementClub.model.dto.ReceiptLineRequestDTO;
import com.managementClub.managementClub.model.dto.ReceiptLineResponseDTO;
import com.managementClub.managementClub.model.enums.ReceiptLineStatus;
import com.managementClub.managementClub.service.ReceiptLineService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        ReceiptLineResponseDTO response = receiptLineService.createReceiptLine(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/by-person/{id}")
    @Override
    public ResponseEntity<List<ReceiptLineResponseDTO>> findByPerson(@PathVariable Long id, @RequestParam(required = false) ReceiptLineStatus status) {
        List<ReceiptLineResponseDTO> response = receiptLineService.findByPerson(id, status);
        return ResponseEntity.ok(response);
    }
}
