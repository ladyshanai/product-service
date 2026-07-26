# Product Service

## 📌 Descripción
Microservicio REST para gestionar productos financieros por cliente (cuentas, tarjetas, préstamos e inversiones).  
Expone operaciones CRUD y consulta por `customerId`.

## 🏗 Arquitectura
- Arquitectura de capas: **Controller → Service → Repository → MySQL**
- `ProductController`: endpoints HTTP en `/api/products`
- `ProductService`: validaciones y reglas de negocio
- `ProductRepository`: acceso a datos con Spring Data JPA
- `ProductMapper` (MapStruct): mapeo entre `ProductEntity` y DTOs
- Integración cloud: **Config Server** + **Eureka Client**

## ⚙ Tecnologías
- Java 21
- Spring Boot 3.4
- Spring Web
- Spring Data JPA
- Spring Cloud Config Client
- Spring Cloud Netflix Eureka Client
- OpenFeign
- MySQL 8.4
- MapStruct 1.6.3
- springdoc-openapi (Swagger UI)
- Maven Wrapper (`mvnw`, `mvnw.cmd`)

## 🚀 Cómo ejecutar
1. Levantar MySQL:
   ```bash
   docker compose up -d
   ```
2. Levantar Config Server (en `http://localhost:8888`).
3. Ejecutar el microservicio:
   ```bash
   ./mvnw spring-boot:run
   ```
   En Windows:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

## 📖 Configuración (Config Server)
El servicio usa:

```yaml
spring:
  application:
    name: product-service
  config:
    import: configserver:http://localhost:8888
```

Debe existir configuración remota para `product-service` (por ejemplo `product-service.yml`) con propiedades como datasource, puerto y perfil.

## 📡 Registro en Eureka
El proyecto incluye dependencia de Eureka Client, por lo que se registra en el servidor Eureka al iniciar (según propiedades entregadas por Config Server).  
Ejemplo típico de configuración remota:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
```

## 📄 Swagger
- UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 🧪 Ejemplos con cURL
Base URL:

```bash
http://localhost:8080/api/products
```

Crear producto:

```bash
curl -X POST "http://localhost:8080/api/products" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "productType": "ACCOUNT",
    "productNumber": "ACC-9001",
    "balance": 1000.00,
    "active": true
  }'
```

Listar todos:

```bash
curl -X GET "http://localhost:8080/api/products"
```

Buscar por ID:

```bash
curl -X GET "http://localhost:8080/api/products/1"
```

Buscar por cliente:

```bash
curl -X GET "http://localhost:8080/api/products/customer/1"
```

Actualizar:

```bash
curl -X PUT "http://localhost:8080/api/products/1" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "productType": "ACCOUNT",
    "productNumber": "ACC-9001",
    "balance": 1500.50,
    "active": true
  }'
```

Eliminar:

```bash
curl -X DELETE "http://localhost:8080/api/products/1"
```

`productType` válidos: `ACCOUNT`, `CREDIT_CARD`, `LOAN`, `INVESTMENT`.

## 📮 Colección de Postman
Puedes importar una colección manualmente usando la documentación de Swagger:
1. Abrir `http://localhost:8080/swagger-ui/index.html`
2. Validar endpoints y payloads
3. Exportar/crear requests en Postman para armar la colección del equipo
