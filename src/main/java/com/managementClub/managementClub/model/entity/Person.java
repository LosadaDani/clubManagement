package com.managementClub.managementClub.model.entity;

import com.managementClub.managementClub.model.enums.MembershipStatus;
import com.managementClub.managementClub.model.enums.MembershipType;
import com.managementClub.managementClub.model.enums.PersonState;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100)
    private String lastname;

    @Column(length = 20)
    private String telephone;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private LocalDate memberSince;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MembershipStatus personState;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MembershipType membershipType;

    public Person() {
    }

    public Person(String name, String lastname, String telephone, String email, LocalDate memberSince, MembershipStatus personState, MembershipType membershipType) {
        this.name = name;
        this.lastname = lastname;
        this.telephone = telephone;
        this.email = email;
        this.memberSince = memberSince;
        this.personState = personState;
        this.membershipType = membershipType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(LocalDate memberSince) {
        this.memberSince = memberSince;
    }

    public MembershipStatus getPersonState() {
        return personState;
    }

    public void setPersonState(MembershipStatus personState) {
        this.personState = personState;
    }

    public MembershipType getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(MembershipType membershipType) {
        this.membershipType = membershipType;
    }
}
