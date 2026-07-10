package com.managementClub.managementClub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(
        description = "Información devuelta cuando se produce un error durante el procesamiento de la solicitud."
)
public class ErrorResponseDTO {

    @Schema(
            description = "Fecha y hora en la que se produjo el error."
    )
    private LocalDateTime timestamp;

    @Schema(
            description = "Código de estado HTTP."
    )
    private int status;

    @Schema(
            description = "Descripción breve del error HTTP."
    )
    private String error;

    @Schema(
            description = "Mensaje detallado del error."
    )
    private String message;

    @Schema(
            description = "Ruta del endpoint que provocó el error."
    )
    private String path;

    public ErrorResponseDTO() {
    }

    public ErrorResponseDTO(LocalDateTime timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
