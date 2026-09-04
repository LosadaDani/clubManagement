package com.managementClub.managementClub.mapper;

import com.managementClub.managementClub.model.dto.PersonSummaryDTO;
import com.managementClub.managementClub.model.dto.ReceiptLineResponseDTO;
import com.managementClub.managementClub.model.dto.ReceiptProposalResponseDTO;
import com.managementClub.managementClub.model.entity.Person;
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
}
