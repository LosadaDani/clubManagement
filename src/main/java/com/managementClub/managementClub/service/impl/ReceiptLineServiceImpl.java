package com.managementClub.managementClub.service.impl;

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

import java.util.List;

@Service
public class ReceiptLineServiceImpl implements ReceiptLineService {

    private final ReceiptLineRepository receiptLineRepository;
    private final PersonRepository personRepository;
    private final ReceiptLineMapper receiptLineMapper;

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

}
