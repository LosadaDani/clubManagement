package com.managementClub.managementClub.repository;

import com.managementClub.managementClub.model.entity.ReceiptLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptLineRepository extends JpaRepository<ReceiptLine, Long> {

}
