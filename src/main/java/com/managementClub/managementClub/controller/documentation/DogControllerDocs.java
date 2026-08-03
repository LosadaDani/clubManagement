package com.managementClub.managementClub.controller.documentation;

import com.managementClub.managementClub.model.dto.DogRequestDTO;
import com.managementClub.managementClub.model.dto.DogResponseDTO;
import com.managementClub.managementClub.model.dto.ErrorResponseDTO;
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
        name = "Perros",
        description = "Operaciones para la gestión de perros asociados a los miembros del club"
)
public interface DogControllerDocs {

    @Operation(
            summary = "Crear un perro",
            description = "Registra un nuevo perro asociado a una persona existente en el club"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Perro registrado correctamente"
            ),
            @ApiResponse(responseCode = "400",
                    description = "Los datos enviados no son válidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = "No existe la persona propietaria indicada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "409",
                    description = "Ya existe un perro con el mismo microchip y número de pedigree",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    ResponseEntity<DogResponseDTO> createDog(DogRequestDTO requestDto);

    @Operation(
            summary = "Obtener un perro por su ID",
            description = "Recupera un perro registrado en el sistema por su identificador único"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                description = "Perro encontrado correctamente"),
            @ApiResponse(responseCode = "404",
                description = "No existe ningun perro con el identificador indicado",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                )
            )
    })
    ResponseEntity<DogResponseDTO> getDog (@Parameter(description = "Identificador del perro") Long id);

    @Operation(
            summary = "Obtener todos los perros de un propietario",
            description = "Recupera el listado de perros registrados en el sistema por el identificador del propietario"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Listado de perros obtenido correctamente. Puede devolver una lista vacía si no existen perros registrados para el propietario indicado."),
            @ApiResponse(responseCode = "404",
                    description = "No existe ningun propietario con el identificador indicado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    ResponseEntity<List<DogResponseDTO>> getDogsByPersonId(@Parameter(description = "Identificador del propietario") Long personId);

    @Operation(
            summary = "Obtener un perro por su microchip",
            description = "Recupera un perro registrado en el sistema por su microchip único"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Perro encontrado correctamente"),
            @ApiResponse(responseCode = "404",
                    description = "No existe ningun perro con el microchip indicado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    ResponseEntity<DogResponseDTO> getDogByMicrochip(@Parameter(description = "Microchip del perro") String microchip);

    @Operation(
            summary = "Obtener todos los perros con un nombre",
            description = "Recupera el listado de perros registrados en el sistema por el nombre indicado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Listado de perros obtenido correctamente. Puede devolver una lista vacía si no existen perros registrados por el nombre indicado.")
    })
    ResponseEntity<List<DogResponseDTO>> getDogByName(@Parameter(description = "Nombre del perro") String name);

    @Operation(
            summary = "Obtener todos los perros",
            description = "Recupera el listado completo de los perros registrados en el sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Listado de perros obtenida correctamente. Puede devolver una lista vacía si no existen perros registrados."
            )
    })
    ResponseEntity<List<DogResponseDTO>> getAllDogs();

    @Operation(
            summary = "Actualizar un perro",
            description = "Actualiza los datos de un perro existente. Si se modifica el microchip o número de pedigree, se comprobará que no existan otros perros con el mismo valor."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Perro actualizado correctamente."),
            @ApiResponse(responseCode = "400",
                    description = "Los datos enviados no son válidos.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = "No existe ningún perro con el identificador indicado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "409",
                    description = "Ya existe otro perro con el microchip o el número de pedigree indicado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    ResponseEntity<DogResponseDTO> updateDog(@Parameter(description = "Identificador único del perro") Long id, DogRequestDTO requestDto);

}