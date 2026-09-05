package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.mapper.ReceiptMapper;
import com.managementClub.managementClub.model.dto.ReceiptLineResponseDTO;
import com.managementClub.managementClub.model.dto.ReceiptProposalResponseDTO;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.enums.MembershipStatus;
import com.managementClub.managementClub.model.enums.ReceiptLineStatus;
import com.managementClub.managementClub.repository.PersonRepository;
import com.managementClub.managementClub.service.ReceiptLineService;
import com.managementClub.managementClub.service.ReceiptService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ReceiptServiceImpl implements ReceiptService {

    private final PersonRepository personRepository;
    private final ReceiptLineService receiptLineService;
    private final ReceiptMapper receiptMapper;

    private final BigDecimal MONTHLY_QUOTA_MEMBER = new BigDecimal("15.00");
    private final BigDecimal ANNUAL_QUOTA_MEMBER = new BigDecimal("15.00");
    private final BigDecimal MONTHLY_QUOTA_PERMANENT_TRAINING = new BigDecimal ("15.00");

    public ReceiptServiceImpl(PersonRepository personRepository, ReceiptLineService receiptLineService, ReceiptMapper receiptMapper) {
        this.personRepository = personRepository;
        this.receiptLineService = receiptLineService;
        this.receiptMapper = receiptMapper;
    }

    @Override
    public List<ReceiptProposalResponseDTO> getReceiptProposal() {
        List<ReceiptProposalResponseDTO> response = new ArrayList<>();
        List<Person> listPerson = personRepository.findByMembershipStatusNot(MembershipStatus.CANCELLED);

        listPerson.forEach(person -> {
            Boolean includePerson = false;
            BigDecimal proposedQuota = null;
            String quotaConcept = null;
            List<ReceiptLineResponseDTO> listPendingLine = receiptLineService.findByPerson(person.getId(), ReceiptLineStatus.PENDING);

            switch (person.getMembershipType()) {
                case INITIATION_TRAINING -> {
                    if (!listPendingLine.isEmpty()) {
                        includePerson = true;
                    }
                }
                case PERMANENT_TRAINING -> {
                    proposedQuota = MONTHLY_QUOTA_PERMANENT_TRAINING;
                    quotaConcept = "Cuota Mensual " + LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
                    includePerson = true;
                }
                case FULL_PARTNER, SUBSCRIBED_MEMBER -> {
                    if (person.getMembershipStatus() == MembershipStatus.ACTIVE) {
                        proposedQuota = MONTHLY_QUOTA_MEMBER;
                        quotaConcept = "Cuota Mensual " + LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
                        includePerson = true;
                    } else if (person.getMembershipStatus() == MembershipStatus.INACTIVE) {
                        if (LocalDate.now().getMonthValue() == 1) {
                            proposedQuota = ANNUAL_QUOTA_MEMBER;
                            quotaConcept = "Cuota anual " + LocalDate.now().getYear();
                            includePerson = true;
                        }

                        if (!listPendingLine.isEmpty()) {
                            includePerson = true;
                        }
                    }
                }
            }

            if (includePerson) {
                response.add(receiptMapper.toProposalDTO(person, listPendingLine, proposedQuota, quotaConcept));
            }
        });

        return response;
    }
}
