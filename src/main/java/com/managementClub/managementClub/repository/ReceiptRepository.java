package com.managementClub.managementClub.repository;

import com.managementClub.managementClub.model.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    List<Receipt> findByPersonIdOrderByIssueDateDescIdDesc(Long personId);
}
