package com.managementClub.managementClub.model.dto;

import java.util.List;

public class GenerateReceiptRequestDTO {

    private Long personId;

    private List<Long> selectedReceiptLineIds;

    private Boolean includeQuota;

    public GenerateReceiptRequestDTO() {
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public List<Long> getSelectedReceiptLineIds() {
        return selectedReceiptLineIds;
    }

    public void setSelectedReceiptLineIds(List<Long> selectedReceiptLineIds) {
        this.selectedReceiptLineIds = selectedReceiptLineIds;
    }

    public Boolean getIncludeQuota() {
        return includeQuota;
    }

    public void setIncludeQuota(Boolean includeQuota) {
        this.includeQuota = includeQuota;
    }
}
