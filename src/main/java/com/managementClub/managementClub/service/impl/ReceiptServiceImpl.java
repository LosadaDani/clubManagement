package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.exception.InvalidBusinessRuleException;
import com.managementClub.managementClub.exception.ResourceNotFoundException;
import com.managementClub.managementClub.mapper.ReceiptLineMapper;
import com.managementClub.managementClub.mapper.ReceiptMapper;
import com.managementClub.managementClub.model.dto.*;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ReceiptLineMapper receiptLineMapper;

    private static final BigDecimal MONTHLY_QUOTA_MEMBER = new BigDecimal("15.00");
    private static final BigDecimal ANNUAL_QUOTA_MEMBER = new BigDecimal("15.00");
    private static final BigDecimal MONTHLY_QUOTA_PERMANENT_TRAINING = new BigDecimal ("15.00");
    private static final BigDecimal RETURN_PENALTY = new BigDecimal("2.48");

    private record QuotaCalculation(BigDecimal amount, String concept) {}

    public ReceiptServiceImpl(PersonRepository personRepository, ReceiptLineService receiptLineService, ReceiptLineRepository receiptLineRepository, ReceiptMapper receiptMapper, ReceiptRepository receiptRepository, ReceiptLineMapper receiptLineMapper) {
        this.personRepository = personRepository;
        this.receiptLineService = receiptLineService;
        this.receiptLineRepository = receiptLineRepository;
        this.receiptMapper = receiptMapper;
        this.receiptRepository = receiptRepository;
        this.receiptLineMapper = receiptLineMapper;
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

    @Override
    public List<ReceiptResponseDTO> getReceiptsByPersonId(Long personId) {

        if (!personRepository.existsById(personId)) {
            throw new ResourceNotFoundException("La persona indicada con el id " + personId + " no existe");
        }

        List<Receipt> receipts = receiptRepository.findByPersonIdOrderByIssueDateDescIdDesc(personId);

        return receipts.stream().map(receiptMapper::toResponseDTO).toList();
    }

    @Override
    public ReceiptDetailResponseDTO getReceiptDetail(Long receiptId) {

        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("El recibo indicado con el id " + receiptId + " no existe"));

        List<ReceiptLine> lines = receiptLineRepository.findByReceiptIdOrderByDateDescIdDesc(receiptId);

        List<ReceiptLineResponseDTO> receiptLinesDto = lines.stream().map(receiptLineMapper::toResponseDto).toList();

        return receiptMapper.toDetailResponseDto(receipt, receiptLinesDto);
    }

    @Override
    @Transactional
    public ReceiptDetailResponseDTO markAsPaid(Long receiptId) {

        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("El recibo indicado con el id " + receiptId + " no existe"));

        if (receipt.getStatus() != ReceiptStatus.ISSUED) {
            throw new InvalidBusinessRuleException("El recibo no puede ser marcado como pagado si no está en estado enviados");
        }

        receipt.setStatus(ReceiptStatus.PAID);
        Receipt savedReceipt = receiptRepository.save(receipt);

        List<ReceiptLine> lines = receiptLineRepository.findByReceiptIdOrderByDateDescIdDesc(receiptId);

        List<ReceiptLine> savedLines = new ArrayList<>();
        lines.forEach(line -> {
            line.setStatus(ReceiptLineStatus.PAID);
            ReceiptLine savedLine = receiptLineRepository.save(line);
            savedLines.add(savedLine);
        });

        return receiptMapper.toDetailResponseDto(savedReceipt, savedLines.stream().map(receiptLineMapper::toResponseDto).toList());
    }

    @Override
    @Transactional
    public ReceiptDetailResponseDTO markAsReturned(Long receiptId) {

        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("El recibo indicado con el id " + receiptId + " no existe"));

        if (receipt.getStatus() != ReceiptStatus.ISSUED) {
            throw new InvalidBusinessRuleException("El recibo no puede ser marcado como devuelto si no está en estado enviados");
        }

        if (receipt.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidBusinessRuleException("El recibo no puede ser marcado como devuelto si no tiene un importe mayor a 0");
        }

        receipt.setStatus(ReceiptStatus.RETURNED);
        Receipt savedReceipt = receiptRepository.save(receipt);

        ReceiptLine lineReturn = new ReceiptLine();
        lineReturn.setDate(LocalDate.now());
        lineReturn.setConcept("Devolución recibo " + receiptId);
        lineReturn.setStatus(ReceiptLineStatus.PENDING);
        BigDecimal amountPenalty = savedReceipt.getTotal().add(RETURN_PENALTY);
        lineReturn.setAmount(amountPenalty);
        lineReturn.setPerson(savedReceipt.getPerson());
        ReceiptLine savedLineReturn = receiptLineRepository.save(lineReturn);

        List<ReceiptLine> originalLines = receiptLineRepository.findByReceiptIdOrderByDateDescIdDesc(receiptId);
        List<ReceiptLineResponseDTO> originalLineDtos = originalLines.stream().map(receiptLineMapper::toResponseDto).toList();
        return receiptMapper.toDetailResponseDto(savedReceipt, originalLineDtos);
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
