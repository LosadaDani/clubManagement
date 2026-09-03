package com.managementClub.managementClub.repository;

import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.entity.ReceiptLine;
import com.managementClub.managementClub.model.enums.ReceiptLineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptLineRepository extends JpaRepository<ReceiptLine, Long> {

    List<ReceiptLine> findByPersonOrderByDateDesc(Person person);

    List<ReceiptLine> findByPersonAndStatusOrderByDateDesc(Person person, ReceiptLineStatus status);

}
