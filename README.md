# Barbería El Mono

Sistema web para agendar citas en la Barbería El Mono (Medellín). Proyecto formativo ADSO, ficha 2977334.

## Estructura

```
barberia_el_mono/
├── backend/
│   ├── src/com/barberiaelmono/
│   │   ├── modelo/       Persona, Cliente, Barbero, Servicio, Disponibilidad, Cita, Pago
│   │   ├── servicio/     AgendamientoService (reglas de negocio)
│   │   ├── dao/          ClienteDAO, BarberoDAO, ServicioDAO, DisponibilidadDAO, CitaDAO (JDBC)
│   │   ├── excepcion/    CitaNoDisponibleException
│   │   ├── util/         SeguridadUtil (hash de contraseñas), ConexionBD
│   │   └── principal/    Main (demostración) y PruebasModulos (pruebas)
│   └── db.properties.example
├── database/
│   └── script_barberia_el_mono.sql
└── frontend/
    ├── index.html, css/, js/
    └── interfaces/       login.html, agendar.html, barbero.html
```

## Cómo compilar y ejecutar (Git Bash, desde la carpeta backend)

Necesitas el JDK instalado (probado con JDK 21).

```
cd backend
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -Dstdout.encoding=UTF-8 -cp out com.barberiaelmono.principal.PruebasModulos
java -Dstdout.encoding=UTF-8 -cp out com.barberiaelmono.principal.Main
```

`PruebasModulos` y `Main` no necesitan base de datos.

En NetBeans: crea un proyecto "Java with Ant > Java Application", copia la carpeta `com` dentro de `src` y ejecuta `Main` o `PruebasModulos`.

## Base de datos y clases DAO

1. Inicia MySQL en XAMPP y ejecuta `database/script_barberia_el_mono.sql` en phpMyAdmin.
2. Copia `backend/db.properties.example` como `backend/db.properties` y escribe tu usuario y clave.
3. Descarga el conector MySQL Connector/J (archivo .jar) y ponlo en `backend/lib/`. Al ejecutar, agrégalo al classpath.

Las clases del paquete `dao` solo se compilaron; para probarlas necesitas el paso 1 a 3.

## Frontend

Abre `frontend/index.html` o las pantallas de `frontend/interfaces/` en el navegador. Las pantallas validan los datos con JavaScript; todavía no se conectan a Java.
