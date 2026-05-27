# eSports Arena Manager Backend

Proyecto Semestral | Desarrollo FullStack I (Backend) | DuocUC

Plataforma backend distribuida para organizar torneos gamer. Permite administrar juegos,
jugadores, equipos, torneos, inscripciones, partidas, resultados, rankings y sanciones.

---

## Integrantes del equipo

| Nombre | Rol principal |
|--------|---------------|
| Christian Hernandez | auth-service, user-service, game-service |
| Yasmine Lodder | team-service, tournament-service, sanction-service, registration-service |
| Nicolas Lara | match-service, result-service, ranking-service |

---

## Tecnologias utilizadas

- Java 17
- Spring Boot 3.2.5
- Spring Data JPA + Hibernate
- Spring Cloud OpenFeign (comunicacion entre microservicios)
- Spring Security + JWT (auth-service)
- MySQL 8
- Maven
- Lombok
- JUnit 5 + Mockito (pruebas unitarias)

---

## Arquitectura

El sistema esta compuesto por 10 microservicios independientes, cada uno con su propia
base de datos. La comunicacion entre servicios se realiza mediante REST usando OpenFeign.

```
                        ┌─────────────────┐
                        │   auth-service  │ :8081
                        │   (JWT + BCrypt)│
                        └────────┬────────┘
                                 │
              ┌──────────────────┼──────────────────┐
              │                  │                  │
     ┌────────▼───────┐ ┌────────▼───────┐ ┌───────▼────────┐
     │  user-service  │ │  game-service  │ │  team-service  │
     │     :8082      │ │     :8086      │ │     :8085      │
     └────────┬───────┘ └────────┬───────┘ └───────┬────────┘
              │                  │                  │
              └──────────────────┼──────────────────┘
                                 │
                    ┌────────────▼───────────┐
                    │   tournament-service   │ :8083
                    └────────────┬───────────┘
                                 │
              ┌──────────────────┼──────────────────┐
              │                  │                  │
     ┌────────▼───────┐ ┌────────▼───────┐         │
     │sanction-service│ │registration-   │         │
     │     :8088      │ │service :8090   │         │
     └────────────────┘ └────────┬───────┘         │
                                 │                  │
                    ┌────────────▼───────────┐      │
                    │    match-service       │◄─────┘
                    │        :8084           │
                    └────────────┬───────────┘
                                 │
                    ┌────────────▼───────────┐
                    │    result-service      │ :8089
                    └────────────┬───────────┘
                                 │
                    ┌────────────▼───────────┐
                    │   ranking-service      │ :8087
                    └────────────────────────┘
```

---

## Microservicios

| Microservicio | Puerto | Base de datos | Descripcion |
|---|---|---|---|
| auth-service | 8080 | esports_auth_db | Autenticacion JWT + BCrypt |
| user-service | 8081 | esports_user_db | Perfiles de jugadores y organizadores |
| tournament-service | 8084 | esports_tournament_db | Torneos, fechas, cupos y estados |
| match-service | 8084 | esports_match_db | Partidas y enfrentamientos |
| team-service | 8085 | esports_team_db | Equipos y miembros |
| game-service | 8082 | esports_game_db | Videojuegos habilitados para torneos |
| ranking-service | 8088 | esports_ranking_db | Tabla de posiciones |
| sanction-service | 8089 | esports_sanction_db | Sanciones y bloqueos |
| result-service | 8087 | esports_result_db | Resultados y validaciones |
| registration-service | 8086 | esports_registration_db | Inscripciones a torneos |

---

## Requisitos previos

- JDK 17 o superior
- Maven 3.9 o superior
- MySQL 8 corriendo en `localhost:3306`
- IntelliJ IDEA o VS Code

---

## Configuracion de base de datos. (******REVISAR******)

Cada microservicio crea su base de datos automaticamente al iniciar gracias a:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/nombre_db?createDatabaseIfNotExist=true
```

Solo debes asegurarte de que MySQL este corriendo y actualizar usuario/contraseña en cada
`application.properties` si es distinto de `root/root`.

---

## Ejecucion

Levantar los microservicios **en este orden** para respetar las dependencias:

```bash
# 1. Sin dependencias externas
cd game-service && mvn spring-boot:run
cd user-service && mvn spring-boot:run

# 2. Dependen de user-service y/o game-service
cd auth-service        && mvn spring-boot:run
cd team-service        && mvn spring-boot:run
cd tournament-service  && mvn spring-boot:run
cd sanction-service    && mvn spring-boot:run

# 3. Dependen de los anteriores
cd registration-service && mvn spring-boot:run

# 4. Dependen de registration-service y tournament-service
cd match-service && mvn spring-boot:run

# 5. Dependen de match-service
cd result-service && mvn spring-boot:run

# 6. Dependen de result-service y tournament-service
cd ranking-service && mvn spring-boot:run
```

---

## Endpoints principales

### auth-service (:8080)
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | /api/v1/auth/register | Registrar cuenta |
| POST | /api/v1/auth/login | Login y obtener JWT |
| POST | /api/v1/auth/validar | Validar token |
| GET | /api/v1/auth/cuentas | Listar cuentas |

### user-service (:8081)
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | /api/v1/usuarios | Crear usuario |
| GET | /api/v1/usuarios | Listar usuarios |
| GET | /api/v1/usuarios/{id} | Buscar por ID |
| GET | /api/v1/usuarios/{id}/resumen | Resumen para otros servicios |
| PATCH | /api/v1/usuarios/{id}/estado | Cambiar estado |
| DELETE | /api/v1/usuarios/{id} | Desactivar |

### game-service (:8082)
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | /api/v1/juegos | Crear juego |
| GET | /api/v1/juegos | Listar juegos |
| GET | /api/v1/juegos/{id} | Buscar por ID |
| PUT | /api/v1/juegos/{id} | Actualizar |
| DELETE | /api/v1/juegos/{id} | Desactivar |

### team-service (:8083)
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | /api/v1/equipos | Crear equipo |
| GET | /api/v1/equipos | Listar equipos |
| GET | /api/v1/equipos/{id} | Buscar por ID |
| POST | /api/v1/equipos/{id}/miembros | Agregar miembro |
| DELETE | /api/v1/equipos/{id}/miembros/{uid} | Eliminar miembro |
| DELETE | /api/v1/equipos/{id} | Desactivar |

### tournament-service (:8084)
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | /api/v1/torneos | Crear torneo |
| GET | /api/v1/torneos | Listar torneos |
| GET | /api/v1/torneos/{id}/resumen | Resumen para otros servicios |
| PATCH | /api/v1/torneos/{id}/estado | Cambiar estado |
| DELETE | /api/v1/torneos/{id}/cancelar | Cancelar |

### registration-service (:8086)
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | /api/v1/inscripciones | Crear inscripcion |
| GET | /api/v1/inscripciones | Listar inscripciones |
| GET | /api/v1/inscripciones/{id} | Buscar por ID |
| DELETE | /api/v1/inscripciones/{id}/cancelar | Cancelar |

### match-service (:8085)
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | /api/v1/partidas | Crear partida |
| GET | /api/v1/partidas | Listar partidas |
| PATCH | /api/v1/partidas/{id}/estado | Cambiar estado |
| DELETE | /api/v1/partidas/{id}/cancelar | Cancelar |

### result-service (:8087)
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | /api/v1/resultados | Registrar resultado |
| GET | /api/v1/resultados | Listar resultados |
| GET | /api/v1/resultados/partida/{id} | Buscar por partida |
| PATCH | /api/v1/resultados/{id}/validacion | Validar o anular |

### ranking-service (:8088)
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | /api/v1/rankings | Registrar participante |
| GET | /api/v1/rankings/torneo/{id} | Tabla de posiciones |
| POST | /api/v1/rankings/torneo/{id}/actualizar | Actualizar con resultado |

### sanction-service (:8089)
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | /api/v1/sanciones | Crear sancion |
| GET | /api/v1/sanciones | Listar sanciones |
| GET | /api/v1/sanciones/verificar/usuario/{id} | Verificar bloqueo usuario |
| GET | /api/v1/sanciones/verificar/equipo/{id} | Verificar bloqueo equipo |
| PATCH | /api/v1/sanciones/{id}/cerrar | Cerrar sancion |

---

## Flujo integrador principal

```
1. Registrar juego          → game-service
2. Crear torneo             → tournament-service (valida juego)
3. Crear usuarios           → user-service
4. Crear cuentas            → auth-service (login → JWT)
5. Crear equipos            → team-service (valida usuario + juego)
6. Abrir inscripciones      → tournament-service (PATCH estado → ABIERTO)
7. Inscribir equipos        → registration-service (valida torneo, equipo, sanciones)
8. Iniciar torneo           → tournament-service (PATCH estado → EN_CURSO)
9. Crear partidas           → match-service (valida inscripción)
10. Iniciar partida         → match-service (PATCH estado → EN_CURSO)
11. Registrar resultado     → result-service (valida partida EN_CURSO)
12. Validar resultado       → result-service → notifica match-service (FINALIZADA)
13. Actualizar ranking      → ranking-service (con ganador y perdedor)
```

---

## Estructura del repositorio

```
esports-arena-manager/
├── auth-service/
├── user-service/
├── team-service/
├── game-service/
├── tournament-service/
├── registration-service/
├── match-service/
├── result-service/
├── ranking-service/
├── sanction-service/
└── README.md
```

---

## Pruebas

Cada microservicio incluye pruebas unitarias con JUnit 5 y Mockito.
Para ejecutar los tests de un servicio:

```bash
cd nombre-service
mvn test
```

---

## Patrones y buenas practicas aplicadas

- **Patron CSR** - Controller / Service / Repository con separacion estricta de responsabilidades
- **DTOs separados** de entidades para entrada y salida
- **Bean Validation** (JSR 380) en todos los DTOs de entrada
- **@ControllerAdvice** para manejo centralizado de errores en cada servicio
- **SLF4J** para logs estructurados en todas las capas
- **Soft delete** - desactivacion logica en lugar de borrado fisico
- **OpenFeign** para comunicacion entre microservicios con manejo de timeouts
- **ResponseEntity** en todos los endpoints con codigos HTTP adecuados
