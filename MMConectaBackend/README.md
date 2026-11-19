# MMConecta Backend

Backend REST en Spring Boot para el EP4 de DSY1105. Es un unico servicio
(no varios microservicios separados) que expone los datos de catalogo y
el login, pensado para ser consumido desde la app Android con Retrofit.

## Requisitos

- Java 21 (ya instalado junto con Android Studio, carpeta `.jdks`)
- Maven (la carpeta `.m2` del usuario indica que ya esta configurado)

## Como ejecutarlo

Desde una terminal (PowerShell o cmd) dentro de esta carpeta:

```
mvn spring-boot:run
```

El servicio queda escuchando en `http://localhost:8080`.

Para probarlo desde el emulador de Android Studio, la URL base NO es
`localhost` sino `http://10.0.2.2:8080/` (alias especial que el emulador
usa para apuntar al computador anfitrion).

## Endpoints

| Metodo | Ruta | Descripcion |
|---|---|---|
| GET | `/api/catalogo` | Lista todo el catalogo |
| GET | `/api/catalogo?categoria=Panaderia` | Filtra por categoria (Panaderia, Pasteleria, Chocolateria) |
| GET | `/api/catalogo/{id}` | Detalle de un contenido por id |
| POST | `/api/usuarios/login` | Body `{ "username": "...", "password": "..." }`. Responde 200 + `{success:true}` o 401 + `{success:false, message:"..."}` |

## Pruebas

`src/test/java/.../CatalogoControllerTests.java` usa MockMvc para probar
los 4 endpoints (listar, filtrar, detalle no encontrado, login valido e
invalido). Ejecutar con:

```
mvn test
```

## Siguiente paso (lado Android)

En la app, reemplazar `LocalLoginRepository` y el repositorio de catalogo
por implementaciones que usen Retrofit apuntando a este backend, sin
cambiar los ViewModels (la interfaz `LoginRepository` ya esta preparada
para este cambio).
