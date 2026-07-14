package com.managementClub.managementClub.repository;

import com.managementClub.managementClub.model.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByEmail(String email);

    @Query("""
            Select p 
            from Person p 
            where lower(concat(p.name, ' ', p.lastName)) 
            like lower(concat('%', :searchText, '%'))
            """)
    List<Person> searchByFullName(@Param("searchText") String searchText);


}
