package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.mapper.ReceiptMapper;
import com.managementClub.managementClub.model.dto.ReceiptLineResponseDTO;
import com.managementClub.managementClub.model.dto.ReceiptProposalResponseDTO;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.enums.MembershipStatus;
import com.managementClub.managementClub.model.enums.MembershipType;
import com.managementClub.managementClub.model.enums.ReceiptLineStatus;
import com.managementClub.managementClub.repository.PersonRepository;
import com.managementClub.managementClub.service.ReceiptLineService;
import com.managementClub.managementClub.service.ReceiptService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

            if (person.getMembershipType() == MembershipType.INITIATION_TRAINING) {
                if (!listPendingLine.isEmpty()) {
                    includePerson = true;
                }
            } else {
                if ((person.getMembershipType() == MembershipType.FULL_PARTNER || person.getMembershipType() == MembershipType.SUBSCRIBED_MEMBER)
                        && person.getMembershipStatus() == MembershipStatus.ACTIVE) {
                    proposedQuota = MONTHLY_QUOTA_MEMBER;
                    quotaConcept = "Quota Mensual " + LocalDate.now().getMonth();
                    includePerson = true;
                } else if (person.getMembershipType() == MembershipType.PERMANENT_TRAINING) {
                    proposedQuota = MONTHLY_QUOTA_PERMANENT_TRAINING;
                    quotaConcept = "Quota Mensual " + LocalDate.now().getMonth();
                    includePerson = true;
                } else if (person.getMembershipStatus() == MembershipStatus.INACTIVE) {

                    if (LocalDate.now().getMonthValue() == 1) {
                        proposedQuota = ANNUAL_QUOTA_MEMBER;
                        quotaConcept = "Quota anual " + LocalDate.now().getYear();
                        includePerson = true;
                    }

                    if (!listPendingLine.isEmpty()) {
                        includePerson = true;
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
