package com.managementClub.managementClub.mapper;

import com.managementClub.managementClub.model.dto.PersonSummaryDTO;
import com.managementClub.managementClub.model.dto.ReceiptLineRequestDTO;
import com.managementClub.managementClub.model.dto.ReceiptLineResponseDTO;
import com.managementClub.managementClub.model.dto.ReceiptSummaryDTO;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.entity.ReceiptLine;
import org.springframework.stereotype.Component;

@Component
public class ReceiptLineMapper {

    public ReceiptLine toEntity(ReceiptLineRequestDTO dto, Person person){
        ReceiptLine receiptLine = new ReceiptLine();

        receiptLine.setPerson(person);
        receiptLine.setDate(dto.getDate());
        receiptLine.setConcept(dto.getConcept());
        receiptLine.setAmount(dto.getAmount());
        return receiptLine;
    }

    public ReceiptLineResponseDTO toResponseDto(ReceiptLine receiptLine) {
        PersonSummaryDTO personSummary = new PersonSummaryDTO(
                receiptLine.getPerson().getId(),
                receiptLine.getPerson().getName(),
                receiptLine.getPerson().getLastName()
        );

        ReceiptSummaryDTO receiptSummary = null;

        if (receiptLine.getReceipt() != null) {
            receiptSummary = new ReceiptSummaryDTO(
                    receiptLine.getReceipt().getId(),
                    receiptLine.getReceipt().getIssueDate(),
                    receiptLine.getReceipt().getStatus()
            );
        }


        return new ReceiptLineResponseDTO(
                receiptLine.getId(),
                personSummary,
                receiptLine.getDate(),
                receiptLine.getConcept(),
                receiptLine.getAmount(),
                receiptLine.getStatus(),
                receiptSummary);
    }
}
