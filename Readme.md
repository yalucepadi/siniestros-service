
# Reto IDM: Microservicios de Poliza y Siniestro

Introducción

-El proyecto implementa dos microservicios:

Microservicio de Poliza: Gestiona la creación de poliza y busqueda por DNI y Id.

-Microservicio de Siniestro:

Gestiona la creacion del Siniestro, usando los Id de las poliza(Webclient conexión), busqueda por Id, por polizaId.



## 🚀 Tecnologías Usadas


- Spring Boot 3.2.0
- Spring WebFlux (programación reactiva, funcional)
- Spring WebClient
- Spring test reactor



## Uso

- Crear Siniestro

```javascript
curl --location 'http://localhost:8081/api/siniestros' \
--header 'Content-Type: application/json' \
--data '{
    "id": "SIN3",
    "polizaId": "POL12345",
    "fechaSiniestro": "2025-08-05",
    "descripcion": "Choque vehicular",
    "estado": "En proceso"
}'
```
- busqueda por polizaId

```javascript
curl --location 'http://localhost:8081/api/siniestros?polizaId=POL12346' \
--header 'Content-Type: application/json' \
--data ''
```



## Appendice

Poliza microservicio:

https://github.com/yalucepadi/polizas-service

