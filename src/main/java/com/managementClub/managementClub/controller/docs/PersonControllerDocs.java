package com.managementClub.managementClub.controller.docs;

import com.managementClub.managementClub.model.dto.ErrorResponseDTO;
import com.managementClub.managementClub.model.dto.PersonRequestDTO;
import com.managementClub.managementClub.model.dto.PersonResponseDTO;
import com.managementClub.managementClub.model.dto.PersonStatusDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(
        name = "Personas",
        description = "Operaciones para la gestión de personas del club."
)
public interface PersonControllerDocs {

    @Operation(
            summary = "Crear una persona",
            description = "Registra una nueva persona en el sistema. Si no se indica la fecha de alta, se asignará la fecha actual y el estado inicial será ACTIVE."
            )
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Persona creada correctamente"
            ),
            @ApiResponse(responseCode = "400",
                    description = "Los datos enviados no son válidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "409",
                    description = "Ya existe una persona con ese correo electrónico",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    ResponseEntity<PersonResponseDTO> createPerson (PersonRequestDTO requestDto);

    @Operation(
            summary = "Obtener una persona por ID",
            description = "Recupera la información de una persona a partir de su identificador único."
            )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Persona encontrada correctamente"),
            @ApiResponse(responseCode = "404",
                    description = "No existe ninguna persona con el identificador indicado",
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    ResponseEntity<PersonResponseDTO> getPerson (@Parameter(description = "Identificador único de la persona") Long id);

    @Operation(
            summary = "Buscar personas",
            description = "Busca personas cuyo nombre y apellidos contengan el texto indicado, sin distinguir entre mayúsculas y minúsculas."
            )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Búsqueda realizada correctamente. Puede devolver una lista vacía si no hay coincidencias.")
    })
    ResponseEntity<List<PersonResponseDTO>> searchPersons (@Parameter(description = "Texto utilizado para buscar coincidencias en nombre y apellidos") String searchText);

    @Operation(
            summary = "Obtener todas las personas",
            description = "Recupera el listado completo de personas registradas en el sistema."
            )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de personas obtenida correctamente. Puede devolver una lista vacía si no existen personas registradas.")
    })
    ResponseEntity<List<PersonResponseDTO>> getAllPersons();

    @Operation(
            summary = "Actualizar una persona",
            description = "Actualiza los datos de una persona existente. Si el correo electrónico se modifica, se comprobará que no esté siendo utilizado por otra persona."
            )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Persona actualizada correctamente"),
            @ApiResponse(responseCode = "400",
                    description = "Los datos enviados no son válidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = "No existe ninguna persona con el identificador indicado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "409",
                    description = "El correo electrónico ya está asignado a otra persona.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    ResponseEntity<PersonResponseDTO> updatePerson(PersonRequestDTO requestDto, @Parameter(description = "Identificador único de la persona") Long id);

    @Operation(
            summary = "Cambiar el estado de una persona",
            description = "Modifica el estado de una persona. Si el estado indicado ya coincide con el estado actual, la operación devolverá la información existente sin realizar modificaciones."
            )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Estado actualizado correctamente"),
            @ApiResponse(responseCode = "400",
                    description = "El estado enviado no es válido",
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = "No existe ninguna persona con el identificador indicado",
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    ResponseEntity<PersonResponseDTO> changeMembershipStatus(@Parameter(description = "Identificador único de la persona") Long id, PersonStatusDTO dto);
}
