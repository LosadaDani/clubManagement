package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.mapper.ReceiptLineMapper;
import com.managementClub.managementClub.model.dto.ReceiptLineRequestDTO;
import com.managementClub.managementClub.model.dto.ReceiptLineResponseDTO;
import com.managementClub.managementClub.repository.PersonRepository;
import com.managementClub.managementClub.repository.ReceiptLineRepository;
import com.managementClub.managementClub.service.ReceiptLineService;
import org.springframework.stereotype.Service;

@Service
public class ReceiptLineServiceImpl implements ReceiptLineService {

    private ReceiptLineRepository receiptLineRepository;
    private PersonRepository personRepository;
    private ReceiptLineMapper receiptLineMapper;

    public ReceiptLineServiceImpl(ReceiptLineRepository receiptLineRepository, PersonRepository personRepository, ReceiptLineMapper receiptLineMapper) {
        this.receiptLineRepository = receiptLineRepository;
        this.personRepository = personRepository;
        this.receiptLineMapper = receiptLineMapper;
    }

    @Override
    public ReceiptLineResponseDTO createReceiptLine(ReceiptLineRequestDTO receiptLineRequestDTO) {

        /*
        Crear ReceiptLineService con el método de creación.
        Implementar ReceiptLineServiceImpl.
        Inyectar ReceiptLineRepository, PersonRepository y ReceiptLineMapper.
        Buscar la Person por personId.
        Lanzar ResourceNotFoundException si no existe.
        Mapear DTO → entidad.
        Establecer PENDING.
        Guardar la línea.
        Mapear entidad → ReceiptLineResponseDTO.
         */
        return null;
    }
}
