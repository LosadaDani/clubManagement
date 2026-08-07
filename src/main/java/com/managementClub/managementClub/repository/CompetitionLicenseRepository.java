package com.managementClub.managementClub.repository;

import com.managementClub.managementClub.model.entity.CompetitionLicense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionLicenseRepository extends JpaRepository<CompetitionLicense, Long> {
}
