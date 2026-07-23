package com.managementClub.managementClub.model.entity;

import com.managementClub.managementClub.model.enums.MembershipStatus;
import com.managementClub.managementClub.model.enums.MembershipType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(length = 20)
    private String telephone;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private LocalDate memberSince;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MembershipStatus membershipStatus;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MembershipType membershipType;

    @OneToMany(mappedBy = "owner")
    private List<Dog> dogs = new ArrayList<>();

    public Person() {
    }

    public Person(String name, String lastName, String telephone, String email, LocalDate memberSince, MembershipStatus membershipStatus, MembershipType membershipType) {
        this.name = name;
        this.lastName = lastName;
        this.telephone = telephone;
        this.email = email;
        this.memberSince = memberSince;
        this.membershipStatus= membershipStatus;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public MembershipStatus getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(MembershipStatus membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public MembershipType getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(MembershipType membershipType) {
        this.membershipType = membershipType;
    }

    public List<Dog> getDogs() {
        return dogs;
    }

    public void setDogs(List<Dog> dogs) {
        this.dogs = dogs;
    }
}
