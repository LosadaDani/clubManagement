package com.managementClub.managementClub.model.dto;

public class OrganizationSummaryDTO {

    private Long id;
    private String name;
    private String shortName;

    public OrganizationSummaryDTO() {
    }

    public OrganizationSummaryDTO(Long id, String name, String shortName) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}

