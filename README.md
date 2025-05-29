
# CF-MS-Bodega 🍷

Este es un sistema distribuido basado en microservicios desarrollado en Java con Spring Boot, diseñado para gestionar el flujo completo de una tienda de vinos en línea. Utiliza autenticación JWT, autorización por roles, WebSocket para notificaciones en tiempo real, y Kafka para comunicación asíncrona entre servicios.

## 🧱 Arquitectura de microservicios

El sistema está compuesto por los siguientes microservicios:

- **UserService**: Maneja autenticación, registro de usuarios, gestión de roles y generación/validación de JWT.
- **WineService**: Gestión CRUD de vinos, con filtros dinámicos por precio, stock, nombre, etc.
- **InventoryService**: Control de inventario, movimientos de stock, y alerta cuando un producto alcanza stock mínimo.
- **CartService**: Gestión de carritos de compra.
- **CartItemService**: Control de productos dentro del carrito, conexión con stock y detalles del vino.
- **NotificationService**: Recibe eventos de Kafka (como alertas de stock) y los emite a través de WebSocket en tiempo real.
- **API Gateway**: Centraliza el ruteo de peticiones y la validación de tokens JWT, delegando la seguridad al UserService.

## 🚀 Tecnologías utilizadas

- Java 23 + Spring Boot
- Spring Security + JWT
- Spring Cloud Gateway
- Kafka (Apache Kafka + Spring Kafka)
- Spring WebFlux (WebClient, WebSocket)
- MySQL
- JPA + Hibernate
- Maven
- Docker
- Lombok

## 📁 Estructura del proyecto

```
BodegaGR/
├── api-gateway/
├── cart-service/
├── cart-item-service/
├── inventory-service/
├── notification-service/
├── user-service/
├── wine-service/
└── README.md
```

## 🔐 Seguridad y JWT

- Autenticación con JWT en el `user-service`
- Autorización basada en roles (`ROLE_USER`, `ROLE_ADMIN`)
- Validación centralizada de tokens en el `api-gateway`
- Roles enviados como claim en el token
- Protección por rutas según rol

## 📡 Comunicación entre servicios

- **Síncrona**: WebClient (WineService <--> InventoryService, etc.)
- **Asíncrona**: Kafka (InventoryService publica alertas, NotificationService las consume)
- **Tiempo real**: WebSocket con STOMP en NotificationService

## ⚙️ Cómo clonar y ejecutar

```bash
git clone https://github.com/Gabeust/cf-ms-bodega.git
cd cf-ms-bodega
```

Cada microservicio puede ejecutarse de forma individual desde tu IDE o desde terminal con:

```bash
cd user-service
./mvnw spring-boot:run
```

## 🔗 Principales endpoints (ejemplos)

| Servicio         | Método | Endpoint                            | Descripción                          |
|------------------|--------|--------------------------------------|--------------------------------------|
| user-service     | POST   | `/api/v1/auth/register`             | Registro de usuario                  |
| user-service     | POST   | `/api/v1/auth/login`                | Login con JWT                        |
| wine-service     | GET    | `/api/v1/wines?name=Malbec`         | Buscar vinos por nombre              |
| inventory-service| POST   | `/api/v1/inventory/move`            | Registrar movimiento de stock        |
| cart-service     | POST   | `/api/v1/cart/{userId}`             | Crear o recuperar carrito de usuario |
| notification     | WS     | `/topic/stock`                      | Suscripción a alertas de stock       |

## 📦 WebSocket y notificaciones

Para recibir alertas de stock en tiempo real:

- Front-end debe suscribirse al canal `/topic/stock`
- Se activa cuando InventoryService detecta stock crítico y publica en Kafka

## 📬 Kafka

- `stock-alerts` → Tópico utilizado para alertas de stock
- Configuración en `inventory-service` y `notification-service`

## 📝 Licencia

Este proyecto está licenciado bajo la licencia [MIT](LICENSE).

---

© 2025 - Gabriel Romero (Gabeust) – Proyecto de práctica y demostración con arquitectura moderna de microservicios.
