package com.managementClub.managementClub.model.entity;

import com.managementClub.managementClub.model.enums.DogSex;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "dog")
public class Dog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private DogSex sex;

    @Column(length = 50)
    private String breed;

    @Column(nullable = false, unique = true, length = 15)
    private String microchip;

    @Column(unique = true)
    private String pedigreeNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person owner;


    public Dog() {
    }

    public Dog(String name, LocalDate birthDate, DogSex sex, String breed, String microchip, String pedigreeNumber, Person owner) {
        this.name = name;
        this.birthDate = birthDate;
        this.sex = sex;
        this.breed = breed;
        this.microchip = microchip;
        this.pedigreeNumber = pedigreeNumber;
        this.owner = owner;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public DogSex getSex() {
        return sex;
    }

    public void setSex(DogSex sex) {
        this.sex = sex;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getMicrochip() {
        return microchip;
    }

    public void setMicrochip(String microchip) {
        this.microchip = microchip;
    }

    public String getPedigreeNumber() {
        return pedigreeNumber;
    }

    public void setPedigreeNumber(String pedigreeNumber) {
        this.pedigreeNumber = pedigreeNumber;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }
}
