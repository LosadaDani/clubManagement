package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.exception.ResourceNotFoundException;
import com.managementClub.managementClub.mapper.ReceiptMapper;
import com.managementClub.managementClub.model.dto.GenerateReceiptRequestDTO;
import com.managementClub.managementClub.model.dto.ReceiptLineResponseDTO;
import com.managementClub.managementClub.model.dto.ReceiptProposalResponseDTO;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.entity.Receipt;
import com.managementClub.managementClub.model.entity.ReceiptLine;
import com.managementClub.managementClub.model.enums.MembershipStatus;
import com.managementClub.managementClub.model.enums.ReceiptLineStatus;
import com.managementClub.managementClub.model.enums.ReceiptStatus;
import com.managementClub.managementClub.repository.PersonRepository;
import com.managementClub.managementClub.repository.ReceiptLineRepository;
import com.managementClub.managementClub.repository.ReceiptRepository;
import com.managementClub.managementClub.service.ReceiptLineService;
import com.managementClub.managementClub.service.ReceiptService;
import jakarta.transaction.Transactional;
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

    private final BigDecimal MONTHLY_QUOTA_MEMBER = new BigDecimal("15.00");
    private final BigDecimal ANNUAL_QUOTA_MEMBER = new BigDecimal("15.00");
    private final BigDecimal MONTHLY_QUOTA_PERMANENT_TRAINING = new BigDecimal ("15.00");

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

    @Override
    @Transactional
    public ReceiptProposalResponseDTO generateReceipt(GenerateReceiptRequestDTO request) {
        /*
        Si includeQuota = true → recalcular la cuota internamente (reutilizando la lógica ya extraída de getReceiptProposal()), crear la ReceiptLine de cuota nueva en PENDING.
        Asociar todas las líneas (seleccionadas + cuota si aplica) al Receipt, cambiarlas a ISSUED.
        Mapear a ReceiptResponseDTO (nuevo Mapper o método en ReceiptMapper) y devolver.
         */

        Person person = personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException("La persona indicada con el id " + request.getPersonId() + " no existe"));

        BigDecimal totalAmount = BigDecimal.ZERO;
        Receipt receipt = new Receipt();
        receipt.setPerson(person);
        receipt.setStatus(ReceiptStatus.ISSUED);

        request.getSelectedReceiptLineIds().forEach(lineId -> {
            ReceiptLine receiptLine = receiptLineRepository.findById(lineId)
                    .orElseThrow(() -> new ResourceNotFoundException("La linea de recibo indicada con el id " + lineId + " no existe"));

            if (receiptLine.getPerson().getId().equals(request.getPersonId())) {
                throw new ResourceNotFoundException("La línea " + lineId + " no pertenece a la persona " + request.getPersonId());
            }

            if (receiptLine.getStatus() != ReceiptLineStatus.PENDING) {
                throw new ResourceNotFoundException("La línea " + lineId + " no está en estado PENDING");
            }

            totalAmount.add(receiptLine.getAmount());

            receiptLine.setStatus(ReceiptLineStatus.ISSUED);
            receiptLine.setReceipt(receipt);
            receiptLineRepository.save(receiptLine);

        });

        //includeQuota

        receipt.setAmount(totalAmount);
        receiptRepository.save(receipt);

        //mapper
        return null;
    }

    /*
    Service — orquestador (generateReceiptsBatch), sin @Transactional global:

        Recorre la lista, llama a generateReceipt(...) por cada una en su propia transacción.
        Captura errores por persona, sin que uno tumbe a los demás.
        Devuelve un resumen (éxitos + fallos con motivo).
     */
}
