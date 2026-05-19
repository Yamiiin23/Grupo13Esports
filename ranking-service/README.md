# ranking-service — eSports Arena Manager

Calcula y mantiene la tabla de posiciones de cada torneo.
Actualiza puntos, victorias, derrotas y diferencia a partir de resultados validados.
Recalcula posiciones automáticamente tras cada actualización.

---

## Puerto: **8089** | Base de datos: `ranking_db`

## Dependencias activas
| Servicio            | Puerto | Propósito                          |
|---------------------|--------|------------------------------------|
| tournament-service  | :8085  | Validar existencia del torneo      |
| result-service      | :8088  | Consultar resultados validados     |

---

## Endpoints REST

| Método | Ruta                                                          | Descripción                        | Código |
|--------|---------------------------------------------------------------|------------------------------------|--------|
| POST   | /api/v1/rankings                                              | Registrar participante             | 201    |
| GET    | /api/v1/rankings/torneo/{torneoId}                            | Tabla de posiciones                | 200    |
| GET    | /api/v1/rankings/torneo/{torneoId}/participante/{id}          | Posición de un participante        | 200    |
| GET    | /api/v1/rankings/{id}                                         | Buscar por ID                      | 200    |
| POST   | /api/v1/rankings/torneo/{torneoId}/actualizar                 | Actualizar con resultado validado  | 200    |
| DELETE | /api/v1/rankings/torneo/{torneoId}/reiniciar                  | Reiniciar ranking del torneo       | 200    |

---

## Ejemplos Postman

### Registrar participante en ranking
```json
POST http://localhost:8089/api/v1/rankings
{
  "torneoId": 1,
  "participanteId": 1
}
```

### Ver tabla de posiciones
```
GET http://localhost:8089/api/v1/rankings/torneo/1
```

### Actualizar con resultado validado
```json
POST http://localhost:8089/api/v1/rankings/torneo/1/actualizar
{
  "ganadorId": 1,
  "perdedorId": 2,
  "puntajeGanador": 2,
  "puntajePerdedor": 0
}
```

### Respuesta tabla de posiciones:
```json
[
  { "posicion": 1, "participanteId": 1, "puntos": 3, "victorias": 1, "derrotas": 0, "diferencia": 2 },
  { "posicion": 2, "participanteId": 2, "puntos": 0, "victorias": 0, "derrotas": 1, "diferencia": -2 }
]
```

---

## Sistema de puntuación

| Resultado  | Puntos |
|-----------|--------|
| Victoria  | 3      |
| Derrota   | 0      |

Desempate: diferencia de puntaje → victorias totales.

---

## Estructura del proyecto

```
ranking-service/
├── src/main/java/com/esports/rankingservice/
│   ├── controller/   → RankingController
│   ├── service/      → RankingService (lógica + recálculo de posiciones)
│   ├── repository/   → RankingRepository (queries ordenadas)
│   ├── model/        → Ranking (entidad JPA + helpers registrarVictoria/Derrota)
│   ├── dto/          → RankingDTO (Request, ActualizarRequest, Response)
│   ├── client/       → ResultServiceClient, TournamentServiceClient
│   └── exception/    → GlobalExceptionHandler + RankingNotFoundException
├── src/test/         → RankingServiceTest (JUnit 5 + Mockito)
└── pom.xml
```

---

## Reglas de negocio
1. Solo resultados **VALIDADOS** deben actualizar el ranking
2. **No duplicar** participante en el ranking del mismo torneo
3. La posición se **recalcula automáticamente** al cambiar puntos
4. Criterio de orden: `puntos DESC` → `diferencia DESC` → `victorias DESC`
5. **3 puntos** por victoria, **0** por derrota
6. `reiniciar` pone todos los puntos a 0 (útil para correcciones)
