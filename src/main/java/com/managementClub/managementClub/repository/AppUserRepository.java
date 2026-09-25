package com.managementClub.managementClub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.managementClub.managementClub.model.entity.AppUser;

@Repository
public interface AppUserRepository extends JpaRepository <AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
}