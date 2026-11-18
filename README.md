# Hernan Dimitri - Backend Challenge - Trading API

API REST desarrollada en **Java 21 (OpenJDK 21.0.13.0.3)** y **Spring Boot 3.3.0** para la gestión de portfolio de inversores, búsqueda de instrumentos financieros y envío/cancelación de órdenes de mercado (`MARKET` y `LIMIT`).

- **Repositorio:** https://github.com/hdimitri/trading-api

---

## 1. Arquitectura Implementada y Diseño de Endpoints

### Arquitectura Hexagonal (Ports & Adapters)
El proyecto está diseñado bajo **Arquitectura Hexagonal**, aislando la lógica de negocio de los detalles de transporte (REST) y persistencia (JPA/PostgreSQL):

```text
src/main/java/ar/com/cocos/
├── domain/                 # Reglas de negocio puras, modelos, enums y validaciones (sin dependencias externas)
├── application/
│   ├── port/in/            # Casos de uso (*UseCase), comandos (command/) y resultados puros (result/)
│   ├── port/out/           # Puertos de persistencia (*Repository)
│   └── service/            # Orquestadores de aplicación (*Service)
└── infrastructure/
    └── adapter/
        ├── in/rest/        # Controladores REST (/api/v1), DTOs HTTP, RequestValidator y GlobalExceptionHandler
        └── out/persistence/# Entidades JPA, Spring Data Repositories y adaptadores de mapeo
```

### Decisiones Técnicas y Supuestos de Negocio
- **Identificación de Usuario por Header (`X-User-Id`):** Se recibe el identificador del inversor mediante el header `X-User-Id` para mantener las rutas y payloads limpios y simular el contexto de identidad de un token real.
- **Cálculo de Portfolio (`GET /api/v1/portfolio`):** El `availableCash` y las tenencias se calculan estrictamente sobre órdenes en estado `FILLED` (incluyendo `CASH_IN` y `CASH_OUT`). El activo `ARS` (`type = MONEDA`) se excluye de las posiciones bursátiles y cada instrumento se valúa al último precio `close` disponible ordenado por fecha descendente.
- **Reglas de Órdenes (`POST` / `DELETE /api/v1/orders`):**
  - **Sin fracciones de acciones:** Si se envía `amount` (monto en pesos), la cantidad se calcula con `Math.floor(amount / price)`.
  - **Reserva en órdenes activas (`NEW`):** Al validar saldo o tenencia disponible, se descuentan también los fondos/acciones comprometidos en órdenes `LIMIT` activas (`NEW`) para evitar doble gasto.
  - **Trazabilidad de rechazos:** Toda orden inválida por fondos o tenencias insuficientes se persiste en estado `REJECTED` con su motivo detallado.

### Diseño de API Endpoints (`/api/v1`)

| Método | Endpoint | Headers / Query Params | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/portfolio` | Header: `X-User-Id` (ej. `1`) | Devuelve valor total de cuenta, pesos disponibles y posiciones valuadas. |
| `GET` | `/api/v1/instruments` | `?query=pamp&filterBy=ticker` | Busca activos similares por `ticker` y/o `name` (case-insensitive). |
| `GET` | `/api/v1/instruments/{id}/marketdata` | Path: `instrumentId` | Devuelve la última cotización (`close`, `previousClose`, `date`) del activo. |
| `POST` | `/api/v1/orders` | Header: `X-User-Id` + JSON Body | Envía órdenes `BUY`, `SELL`, `CASH_IN` o `CASH_OUT` (`MARKET` o `LIMIT`). |
| `DELETE` | `/api/v1/orders/{orderId}` | Header: `X-User-Id`, Path: `orderId` | Cancela una orden activa únicamente si su estado es `NEW`. |

---

## 2. Otros Aspectos Importantes que Consideraría si Fuera una API Real

- **Idempotencia en Órdenes:** Garantizar que el reintento de una solicitud de compra o venta ante fallos de red no duplique una operación ni impacte dos veces el saldo del inversor.
- **Control de Concurrencia:** Serializar operaciones simultáneas sobre una misma cuenta para evitar condiciones de carrera.
- **Estrategia de Caché:** Cachear información con baja frecuencia de cambio como el catálogo de instrumentos.
- **Seguridad (Autenticación y Autorización):** Controlar el acceso mediante OAuth2 y JWT, validando que cada inversor solo pueda consultar y operar sobre sus propios datos.

---

## 3. Cómo Ejecutar el Proyecto Localmente

### Requisitos en PC Local
- **Java 21** (desarrollado y probado con `OpenJDK 21.0.13.0.3`)
- **Gradle 8.14.7** (o utilizar el wrapper `./gradlew` incluido en el repositorio)
- No requiere instalar PostgreSQL localmente: la aplicación debe conectarse a la base de datos PostgreSQL remota utilizando los datos de conexión provistos por **Emilia Delpech**.

### 1. Clonar el repositorio
```bash
git clone https://github.com/hdimitri/trading-api.git
cd trading-api
```

### 2. Ejecutar la aplicación pasando los datos de conexión de la base de datos remota
Reemplaza `<DB_URL>`, `<DB_USER>` y `<DB_PASSWORD>` por los valores provistos por Emilia Delpech y ejecuta:

```bash
./gradlew bootRun --args="--DB_URL=<DB_URL> --DB_USER=<DB_USER> --DB_PASSWORD=<DB_PASSWORD>"
```
La API quedará levantada en `http://localhost:8080`.

Si el puerto 8080 está ocupado, conviene liberarlo, dado que la collection Postman detallada más abajo ya apunta a dicho puerto, evitando la necesidad de configurar variables en Postman.

### 3. Ejecutar la suite de tests
Los tests utilizan un perfil aislado (`test`) con base de datos **H2 en memoria** (modo PostgreSQL):

```bash
./gradlew test
```

### 4. Pruebas con Postman
En la carpeta **`docs/`** del repositorio se incluye:
- **Colección de Postman:** [`docs/trading-api.postman_collection.json`](docs/trading-api.postman_collection.json) lista para importar, con ejemplos preconfigurados para consultar el portfolio, buscar activos por ticker/nombre, enviar órdenes `MARKET` (por cantidad o monto en pesos) y `LIMIT`, validar rechazos (`REJECTED`) y cancelar órdenes en estado `NEW`.