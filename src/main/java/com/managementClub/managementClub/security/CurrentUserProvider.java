package com.managementClub.managementClub.security;

import com.managementClub.managementClub.model.entity.AppUser;
import com.managementClub.managementClub.model.enums.Role;
import com.managementClub.managementClub.repository.AppUserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserProvider {

    private final AppUserRepository appUserRepository;

    public CurrentUserProvider(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUser getCurrentAppUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return appUserRepository.findByUsername(username).orElseThrow();
    }

    public boolean isAdmin() {
        return getCurrentAppUser().getRole() == Role.ROLE_ADMIN;
    }

    public boolean isAdminOrOwner(Long personId) {
        AppUser currentUser = getCurrentAppUser();
        return currentUser.getRole() == Role.ROLE_ADMIN || currentUser.getPerson().getId().equals(personId);
    }
}
