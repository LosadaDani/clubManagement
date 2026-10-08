package com.managementClub.managementClub.repository;

import com.managementClub.managementClub.model.entity.CompetitionLicense;
import com.managementClub.managementClub.model.entity.Dog;
import com.managementClub.managementClub.model.entity.Organization;
import com.managementClub.managementClub.model.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CompetitionLicenseRepository extends JpaRepository<CompetitionLicense, Long> {

    List<CompetitionLicense> findByDog(Dog dog);

    boolean existsByOrganizationAndPersonAndDogAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Organization organization,
            Person person,
            Dog dog,
            LocalDate newEndDate,
            LocalDate newStartDate);

    @Query("""
        SELECT COUNT(c) > 0
        FROM CompetitionLicense c
        WHERE c.dog = :dog
        AND c.organization = :organization
        AND c.person = :person
        AND c.startDate <= :newEndDate
        AND c.endDate >= :newStartDate
        AND c.id <> :idCompetitionLicense
        """)
    boolean existsOverlappingLicenseExcludingId(
            @Param("organization") Organization organization,
            @Param("person") Person person,
            @Param("dog") Dog dog,
            @Param("newEndDate") LocalDate newEndDate,
            @Param("newStartDate") LocalDate newStartDate,
            @Param("idCompetitionLicense")Long idCompetitionLicense);

    @Query("""
          SELECT c 
          FROM CompetitionLicense c 
          WHERE c.dog = :dog 
          AND c.startDate <= CURRENT_DATE 
          AND c.endDate >= CURRENT_DATE
          """)
    List<CompetitionLicense> findLicenseCurrentByDog(@Param("dog") Dog dog);
}
