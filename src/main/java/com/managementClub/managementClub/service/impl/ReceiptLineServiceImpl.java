package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.exception.InvalidBusinessRuleException;
import com.managementClub.managementClub.exception.ResourceNotFoundException;
import com.managementClub.managementClub.mapper.ReceiptLineMapper;
import com.managementClub.managementClub.model.dto.ReceiptLineRequestDTO;
import com.managementClub.managementClub.model.dto.ReceiptLineResponseDTO;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.entity.ReceiptLine;
import com.managementClub.managementClub.model.enums.ReceiptLineStatus;
import com.managementClub.managementClub.repository.PersonRepository;
import com.managementClub.managementClub.repository.ReceiptLineRepository;
import com.managementClub.managementClub.service.ReceiptLineService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReceiptLineServiceImpl implements ReceiptLineService {

    private final ReceiptLineRepository receiptLineRepository;
    private final PersonRepository personRepository;
    private final ReceiptLineMapper receiptLineMapper;

    private final static BigDecimal FIRST_INITIATION_PRICE = new BigDecimal("150");
    private final static BigDecimal SECOND_INITIATION_PRICE = new BigDecimal("100");

    public ReceiptLineServiceImpl(ReceiptLineRepository receiptLineRepository, PersonRepository personRepository, ReceiptLineMapper receiptLineMapper) {
        this.receiptLineRepository = receiptLineRepository;
        this.personRepository = personRepository;
        this.receiptLineMapper = receiptLineMapper;
    }

    @Override
    public ReceiptLineResponseDTO createReceiptLine(ReceiptLineRequestDTO receiptLineRequestDTO) {

        Person person = personRepository.findById(receiptLineRequestDTO.getPersonId()).
                orElseThrow(() -> new ResourceNotFoundException("La persona indicada con el id " + receiptLineRequestDTO.getPersonId() + " no existe"));

        ReceiptLine receiptLine = receiptLineMapper.toEntity(receiptLineRequestDTO, person);
        receiptLine.setStatus(ReceiptLineStatus.PENDING);
        ReceiptLine savedReceiptLine = receiptLineRepository.save(receiptLine);

        return receiptLineMapper.toResponseDto(savedReceiptLine);
    }

    @Override
    public List<ReceiptLineResponseDTO> findByPerson(Long personId, ReceiptLineStatus status) {

        Person person = personRepository.findById(personId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("La persona indicada con el id " + personId + " no existe"));

        if (status != null) {
            return receiptLineRepository.findByPersonAndStatusOrderByDateDesc(person, status)
                    .stream()
                    .map( receiptLineMapper::toResponseDto)
                    .toList();
        } else {
            return receiptLineRepository.findByPersonOrderByDateDesc(person)
                    .stream()
                    .map( receiptLineMapper::toResponseDto)
                    .toList();
        }
    }

    @Override
    public ReceiptLineResponseDTO updateReceiptLine(Long id, ReceiptLineRequestDTO receiptLineRequestDTO) {

        ReceiptLine receiptLine = receiptLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La linea de recibo indicada con el id " + id + " no existe"));

        if (receiptLine.getStatus() != ReceiptLineStatus.PENDING) {
            throw new InvalidBusinessRuleException("La linea de recibo indicada con el id " + id + " no se encuentra en estado PENDIENTE");
        }

        receiptLine = receiptLineMapper.updateEntityFromDTO(receiptLineRequestDTO, receiptLine);

        ReceiptLine updatedReceiptLine = receiptLineRepository.save(receiptLine);
        return receiptLineMapper.toResponseDto(updatedReceiptLine);

    }

    @Override
    public void deleteReceiptLine(Long id) {

        ReceiptLine receiptLine = receiptLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La linea de recibo indicada con el id " + id + " no existe"));

        if (receiptLine.getStatus() != ReceiptLineStatus.PENDING) {
            throw new InvalidBusinessRuleException("La linea de recibo indicada con el id " + id + " no se encuentra en estado PENDIENTE");
        }

        receiptLineRepository.delete(receiptLine);
    }

    @Override
    public void createInitiationLines(Person person) {
        ReceiptLine receiptLineFirst = new ReceiptLine();
        receiptLineFirst.setPerson(person);
        receiptLineFirst.setAmount(FIRST_INITIATION_PRICE);
        receiptLineFirst.setStatus(ReceiptLineStatus.PENDING);
        receiptLineFirst.setConcept("1ª cuota Iniciación");
        receiptLineFirst.setDate(LocalDate.now());
        receiptLineRepository.save(receiptLineFirst);

        ReceiptLine receiptLineSecond = new ReceiptLine();
        receiptLineSecond.setPerson(person);
        receiptLineSecond.setAmount(SECOND_INITIATION_PRICE);
        receiptLineSecond.setStatus(ReceiptLineStatus.PENDING);
        receiptLineSecond.setConcept("2ª cuota Iniciación");
        receiptLineSecond.setDate(LocalDate.now().plusMonths(1));
        receiptLineRepository.save(receiptLineSecond);
    }

}
