package com.esports.teamservice.controller;

import com.esports.teamservice.dto.EquipoDTO;
import com.esports.teamservice.model.Equipo;
import com.esports.teamservice.service.EquipoService;
import io.swagger.v3.oas.annotations.Operation; // <-- NUEVAS IMPORTACIONES DE SWAGGER
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/equipos")
@Tag(name = "Módulo Equipos", description = "Controlador interactivo para la gestión integral y el control de plantillas de escuadras de eSports")
public class EquipoController {

    private static final Logger log = LoggerFactory.getLogger(EquipoController.class);
    private final EquipoService equipoService;

    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    @PostMapping
    @Operation(summary = "Registrar una nueva escuadra", description = "Crea un equipo en la base de datos local previa validación del juego y del capitán mediante OpenFeign hacia los microservicios externos correspondientes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Escuadra dada de alta exitosamente",
                    content = @Content(schema = @Schema(implementation = EquipoDTO.Response.class))),
            @ApiResponse(responseCode = "400", description = "Petición incorrecta (Fallo en la validación gramatical o campos del Request)"),
            @ApiResponse(responseCode = "422", description = "Error de negocio (El capitán o el juego principal provistos no existen en el ecosistema)")
    })
    public ResponseEntity<EquipoDTO.Response> crearEquipo(
            @Valid @RequestBody EquipoDTO.Request request) {

        log.info("[team-service] POST /api/v1/equipos - nombre={}", request.getNombre());
        EquipoDTO.Response response = equipoService.crearEquipo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar y filtrar escuadras", description = "Recupera una colección de todos los equipos del sistema, permitiendo aplicar filtros condicionales opcionales por juego y por estado de disponibilidad.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de equipos recuperado con éxito")
    })
    public ResponseEntity<List<EquipoDTO.Response>> listarEquipos(
            @Parameter(description = "ID del videojuego para filtrar las escuadras asociadas", example = "2")
            @RequestParam(required = false) Long juegoId,
            @Parameter(description = "Estado operacional para restringir la consulta (ACTIVO, INACTIVO)", example = "ACTIVO")
            @RequestParam(required = false) Equipo.EstadoEquipo estado) {

        log.info("[team-service] GET /api/v1/equipos - juegoId={}, estado={}", juegoId, estado);
        return ResponseEntity.ok(equipoService.listarEquipos(juegoId, estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar un equipo por su ID", description = "Recupera los datos operativos estructurados y la nómina de jugadores de una escuadra mediante su clave primaria única.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipo localizado correctamente",
                    content = @Content(schema = @Schema(implementation = EquipoDTO.Response.class))),
            @ApiResponse(responseCode = "404", description = "El identificador provisto no corresponde a ninguna escuadra registrada")
    })
    public ResponseEntity<EquipoDTO.Response> buscarPorId(
            @Parameter(description = "ID único incremental de la escuadra", example = "1", required = true)
            @PathVariable Long id) {
        log.info("[team-service] GET /api/v1/equipos/{}", id);
        return ResponseEntity.ok(equipoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de una escuadra", description = "Modifica las propiedades editables (nombre, capitán, juego principal) de una escuadra basándose en su ID y un payload válido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Escuadra modificada exitosamente",
                    content = @Content(schema = @Schema(implementation = EquipoDTO.Response.class))),
            @ApiResponse(responseCode = "400", description = "Errores de validación en el cuerpo de la solicitud"),
            @ApiResponse(responseCode = "404", description = "No se encontró el equipo para actualizar")
    })
    public ResponseEntity<EquipoDTO.Response> actualizarEquipo(
            @Parameter(description = "ID de la escuadra a modificar", example = "1", required = true) @PathVariable Long id,
            @Valid @RequestBody EquipoDTO.Request request) {

        log.info("[team-service] PUT /api/v1/equipos/{}", id);
        return ResponseEntity.ok(equipoService.actualizarEquipo(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar un equipo (Soft Delete)", description = "Cambia de manera lógica el estado operativo del equipo a 'INACTIVO' para darlo de baja temporal o definitiva sin destruir el registro físico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipo dado de baja en el sistema de forma lógica"),
            @ApiResponse(responseCode = "404", description = "El ID enviado no pertenece a ningún equipo operativo")
    })
    public ResponseEntity<EquipoDTO.Response> desactivarEquipo(
            @Parameter(description = "ID de la escuadra a dar de baja", example = "1", required = true)
            @PathVariable Long id) {
        log.info("[team-service] DELETE /api/v1/equipos/{}", id);
        return ResponseEntity.ok(equipoService.desactivarEquipo(id));
    }

    @PostMapping("/{id}/miembros")
    @Operation(summary = "Fichar/Agregar un jugador al equipo", description = "Incorpora un nuevo competidor a las filas de la escuadra con un rol específico, validando el cupo del equipo y la existencia real del usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Jugador fichado e inscrito en la nómina con éxito",
                    content = @Content(schema = @Schema(implementation = EquipoDTO.Response.class))),
            @ApiResponse(responseCode = "400", description = "Formato de datos erróneo o infracción de restricciones de validación"),
            @ApiResponse(responseCode = "404", description = "El equipo de destino o el ID del usuario provisto no existen")
    })
    public ResponseEntity<EquipoDTO.Response> agregarMiembro(
            @Parameter(description = "ID de la escuadra que recibe al jugador", example = "1", required = true) @PathVariable Long id,
            @Valid @RequestBody EquipoDTO.MiembroRequest request) {

        log.info("[team-service] POST /api/v1/equipos/{}/miembros - usuarioId={}", id, request.getUsuarioId());
        return ResponseEntity.status(HttpStatus.CREATED).body(equipoService.agregarMiembro(id, request));
    }

    @DeleteMapping("/{id}/miembros/{usuarioId}")
    @Operation(summary = "Remover un jugador de la plantilla", description = "Elimina la asociación formal de membresía de un jugador respecto a la escuadra especificada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Jugador removido y liberado del equipo de manera correcta"),
            @ApiResponse(responseCode = "404", description = "No se localizó la escuadra o el usuario no formaba parte de las filas del equipo")
    })
    public ResponseEntity<EquipoDTO.Response> eliminarMiembro(
            @Parameter(description = "ID del equipo del cual saldrá el jugador", example = "1", required = true) @PathVariable Long id,
            @Parameter(description = "ID del usuario que será revocado de la escuadra", example = "42", required = true) @PathVariable Long usuarioId) {

        log.info("[team-service] DELETE /api/v1/equipos/{}/miembros/{}", id, usuarioId);
        return ResponseEntity.ok(equipoService.eliminarMiembro(id, usuarioId));
    }
}