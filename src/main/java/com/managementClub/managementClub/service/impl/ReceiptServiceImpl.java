package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.exception.InvalidBusinessRuleException;
import com.managementClub.managementClub.exception.ResourceNotFoundException;
import com.managementClub.managementClub.mapper.ReceiptMapper;
import com.managementClub.managementClub.model.dto.GenerateReceiptRequestDTO;
import com.managementClub.managementClub.model.dto.ReceiptLineResponseDTO;
import com.managementClub.managementClub.model.dto.ReceiptProposalResponseDTO;
import com.managementClub.managementClub.model.dto.ReceiptResponseDTO;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.entity.Receipt;
import com.managementClub.managementClub.model.entity.ReceiptLine;
import com.managementClub.managementClub.model.enums.MembershipStatus;
import com.managementClub.managementClub.model.enums.MembershipType;
import com.managementClub.managementClub.model.enums.ReceiptLineStatus;
import com.managementClub.managementClub.model.enums.ReceiptStatus;
import com.managementClub.managementClub.repository.PersonRepository;
import com.managementClub.managementClub.repository.ReceiptLineRepository;
import com.managementClub.managementClub.repository.ReceiptRepository;
import com.managementClub.managementClub.service.ReceiptLineService;
import com.managementClub.managementClub.service.ReceiptService;
import org.springframework.transaction.annotation.Transactional;
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
    private final ReceiptLineRepository receiptLineRepository;
    private final ReceiptMapper receiptMapper;
    private final ReceiptRepository receiptRepository;

    private static final BigDecimal MONTHLY_QUOTA_MEMBER = new BigDecimal("15.00");
    private static final BigDecimal ANNUAL_QUOTA_MEMBER = new BigDecimal("15.00");
    private static final BigDecimal MONTHLY_QUOTA_PERMANENT_TRAINING = new BigDecimal ("15.00");

    private record QuotaCalculation(BigDecimal amount, String concept) {}

    public ReceiptServiceImpl(PersonRepository personRepository, ReceiptLineService receiptLineService, ReceiptLineRepository receiptLineRepository, ReceiptMapper receiptMapper, ReceiptRepository receiptRepository) {
        this.personRepository = personRepository;
        this.receiptLineService = receiptLineService;
        this.receiptLineRepository = receiptLineRepository;
        this.receiptMapper = receiptMapper;
        this.receiptRepository = receiptRepository;
    }

    @Override
    public List<ReceiptProposalResponseDTO> getReceiptProposal() {
        List<ReceiptProposalResponseDTO> response = new ArrayList<>();
        List<Person> listPerson = personRepository.findByMembershipStatusNot(MembershipStatus.CANCELLED);

        listPerson.forEach(person -> {
            Boolean includePerson = false;
            List<ReceiptLineResponseDTO> listPendingLine = receiptLineService.findByPerson(person.getId(), ReceiptLineStatus.PENDING);

            QuotaCalculation quotaCalculation = calculateQuota(person);

            if (person.getMembershipType() == MembershipType.INITIATION_TRAINING) {
                includePerson = !listPendingLine.isEmpty();
            } else {
                includePerson = quotaCalculation != null || !listPendingLine.isEmpty();
            }

            if (includePerson){
                response.add(receiptMapper.toProposalDTO(
                        person,
                        listPendingLine,
                        quotaCalculation != null ? quotaCalculation.amount : null,
                        quotaCalculation != null ? quotaCalculation.concept : null));
            }
        });

        return response;
    }

    @Override
    @Transactional
    public ReceiptResponseDTO generateReceipt(GenerateReceiptRequestDTO request) {

        Person person = personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException("La persona indicada con el id " + request.getPersonId() + " no existe"));

        if ((request.getSelectedReceiptLineIds() == null || request.getSelectedReceiptLineIds().isEmpty())
                && !request.getIncludeQuota()) {
            throw new InvalidBusinessRuleException("No se puede generar un recibo sin líneas seleccionadas ni cuota incluida");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        int lineCount = 0;

        Receipt receipt = new Receipt();
        receipt.setPerson(person);
        receipt.setStatus(ReceiptStatus.ISSUED);
        receipt.setIssueDate(LocalDate.now());
        receipt.setTotal(totalAmount);
        Receipt savedReceipt = receiptRepository.save(receipt);

        if (request.getSelectedReceiptLineIds() != null && !request.getSelectedReceiptLineIds().isEmpty()) {
            for (Long lineId : request.getSelectedReceiptLineIds()) {
                ReceiptLine receiptLine = receiptLineRepository.findById(lineId)
                        .orElseThrow(() -> new ResourceNotFoundException("La linea de recibo indicada con el id " + lineId + " no existe"));

                if (!receiptLine.getPerson().getId().equals(request.getPersonId())) {
                    throw new ResourceNotFoundException("La línea " + lineId + " no pertenece a la persona " + request.getPersonId());
                }

                if (receiptLine.getStatus() != ReceiptLineStatus.PENDING) {
                    throw new InvalidBusinessRuleException("La línea " + lineId + " no está en estado PENDING");
                }

                totalAmount =totalAmount.add(receiptLine.getAmount());

                receiptLine.setStatus(ReceiptLineStatus.ISSUED);
                receiptLine.setReceipt(savedReceipt);
                receiptLineRepository.save(receiptLine);
                lineCount++;
            }
        }


        if (request.getIncludeQuota()) {
            QuotaCalculation quotaCalculation = calculateQuota(person);
            if (quotaCalculation != null) {
                ReceiptLine receiptLine = new ReceiptLine();
                receiptLine.setPerson(person);
                receiptLine.setAmount(quotaCalculation.amount);
                receiptLine.setConcept(quotaCalculation.concept);
                receiptLine.setStatus(ReceiptLineStatus.ISSUED);
                receiptLine.setDate(LocalDate.now());
                receiptLine.setReceipt(savedReceipt);
                receiptLineRepository.save(receiptLine);
                totalAmount = totalAmount.add(receiptLine.getAmount());
                lineCount++;
            }
        }

        if (lineCount == 0) {
            throw new InvalidBusinessRuleException("No se puede generar un recibo sin líneas seleccionadas ni cuota incluida");
        }
        savedReceipt.setTotal(totalAmount);
        receiptRepository.save(savedReceipt);

        return receiptMapper.toResponseDTO(savedReceipt);
    }

    private QuotaCalculation calculateQuota(Person person) {

        return switch (person.getMembershipType()) {
            case PERMANENT_TRAINING -> new QuotaCalculation(
                    MONTHLY_QUOTA_PERMANENT_TRAINING,
                    "Cuota Mensual " + LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES")));

            case FULL_PARTNER, SUBSCRIBED_MEMBER -> {
                if (person.getMembershipStatus() == MembershipStatus.ACTIVE) {
                    yield new QuotaCalculation(
                            MONTHLY_QUOTA_MEMBER,
                            "Cuota Mensual " + LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES")));
                } else if (person.getMembershipStatus() == MembershipStatus.INACTIVE && LocalDate.now().getMonthValue() == 1) {
                    yield new QuotaCalculation(
                            ANNUAL_QUOTA_MEMBER,
                            "Cuota Anual " + LocalDate.now().getYear());
                }
                yield null;
            }

            default -> null; //INITIATION_TRAINING no genera cuota
        };

    }
}
