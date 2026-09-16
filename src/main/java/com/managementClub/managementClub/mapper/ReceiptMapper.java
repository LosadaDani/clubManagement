package com.managementClub.managementClub.mapper;

import com.managementClub.managementClub.model.dto.*;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.entity.Receipt;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ReceiptMapper {

    public ReceiptProposalResponseDTO toProposalDTO(
            Person person,
            List<ReceiptLineResponseDTO> pendingLine,
            BigDecimal proposedQuota,
            String quotaConcept) {

        PersonSummaryDTO personSummary = new PersonSummaryDTO(person.getId(), person.getName(), person.getLastName());
        return new ReceiptProposalResponseDTO(personSummary, pendingLine, proposedQuota, quotaConcept);
    }

    public ReceiptResponseDTO toResponseDTO(Receipt receipt) {
        PersonSummaryDTO personSummary = new PersonSummaryDTO(
                receipt.getPerson().getId(),
                receipt.getPerson().getName(),
                receipt.getPerson().getLastName());

        return new ReceiptResponseDTO(
                receipt.getId(),
                personSummary,
                receipt.getIssueDate(),
                receipt.getTotal(),
                receipt.getStatus());
    }

    public ReceiptDetailResponseDTO toDetailResponseDto (Receipt receipt, List<ReceiptLineResponseDTO> lines) {
        PersonSummaryDTO personSummary = new PersonSummaryDTO(
                receipt.getPerson().getId(),
                receipt.getPerson().getName(),
                receipt.getPerson().getLastName()
        );

        return new ReceiptDetailResponseDTO(receipt.getId(), personSummary, receipt.getIssueDate(), receipt.getTotal(), receipt.getStatus(), lines);

    }
}
