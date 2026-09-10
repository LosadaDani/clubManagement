package com.managementClub.managementClub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Representa un recibo antes de crearse")
public class GenerateReceiptRequestDTO {
    @Schema(description = "Identificador de la persona a la que se le creara el recibo",
            example = "1")
    @NotNull(message = "El id de la persona es obligatorio")
    private Long personId;

    @Schema(description = "Identificadores de las lineas de recibo seleccionadas",
            example = "[1, 2]")
    private List<Long> selectedReceiptLineIds;

    @Schema(description = "Indica si se incluye la cuota propuesta",
            example = "true")
    @NotNull(message = "Debe especificarse si se incluye la cuota propuesta")
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
