-- =====================================================================
-- Barbería El Mono - Script de la base de datos barberia_el_mono
-- Es el mismo de la evidencia GA6-220501096-AA2-EV02 (estructura y datos de prueba).
-- Cómo usarlo: en phpMyAdmin, pestaña "SQL" (o "Importar"), pegar y ejecutar completo.
-- Si la base de datos ya existe, bórrala antes: DROP DATABASE barberia_el_mono;
-- =====================================================================

CREATE DATABASE barberia_el_mono CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE barberia_el_mono;

CREATE TABLE cliente (
  id_cliente       INT NOT NULL AUTO_INCREMENT,
  nombre           VARCHAR(45) NOT NULL,
  correo           VARCHAR(60) NOT NULL,
  contrasena       VARCHAR(255) NOT NULL,
  fecha_nacimiento DATE NULL,
  puntos_estrella  INT NOT NULL DEFAULT 0,
  es_cliente_fijo  TINYINT(1) NOT NULL DEFAULT 0,
  CONSTRAINT pk_cliente PRIMARY KEY (id_cliente),
  CONSTRAINT uq_cliente_correo UNIQUE (correo),
  CONSTRAINT ck_cliente_puntos CHECK (puntos_estrella >= 0),
  CONSTRAINT ck_cliente_fijo CHECK (es_cliente_fijo IN (0,1))
) ENGINE=InnoDB;

CREATE TABLE barbero (
  id_barbero  INT NOT NULL AUTO_INCREMENT,
  nombre      VARCHAR(45) NOT NULL,
  correo      VARCHAR(60) NOT NULL,
  contrasena  VARCHAR(255) NOT NULL,
  puesto      INT NOT NULL,
  cupo_maximo INT NOT NULL,
  CONSTRAINT pk_barbero PRIMARY KEY (id_barbero),
  CONSTRAINT uq_barbero_correo UNIQUE (correo),
  CONSTRAINT ck_barbero_cupo CHECK (cupo_maximo > 0)
) ENGINE=InnoDB;

CREATE TABLE servicio (
  id_servicio INT NOT NULL AUTO_INCREMENT,
  nombre      VARCHAR(45) NOT NULL,
  precio      DECIMAL(10,2) NOT NULL,
  descripcion VARCHAR(100) NULL,
  CONSTRAINT pk_servicio PRIMARY KEY (id_servicio),
  CONSTRAINT ck_servicio_precio CHECK (precio >= 0)
) ENGINE=InnoDB;

CREATE TABLE cita (
  id_cita         INT NOT NULL AUTO_INCREMENT,
  id_cliente      INT NOT NULL,
  id_barbero      INT NOT NULL,
  fecha           DATE NOT NULL,
  hora            TIME NOT NULL,
  estado          VARCHAR(20) NOT NULL DEFAULT 'Pendiente',
  puesto_asignado INT NULL,
  numero_turno    INT NOT NULL,
  tipo_shampoo    VARCHAR(45) NULL,
  total_pagar     DECIMAL(10,2) NOT NULL,
  descuento       DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  abono           DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  CONSTRAINT pk_cita PRIMARY KEY (id_cita),
  CONSTRAINT fk_cita_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_cita_barbero FOREIGN KEY (id_barbero) REFERENCES barbero (id_barbero)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT ck_cita_estado CHECK (estado IN ('Pendiente','Atendida','Cancelada')),
  CONSTRAINT ck_cita_total CHECK (total_pagar >= 0),
  CONSTRAINT ck_cita_descuento CHECK (descuento >= 0),
  CONSTRAINT ck_cita_abono CHECK (abono >= 0)
) ENGINE=InnoDB;

CREATE TABLE cita_servicio (
  id_cita     INT NOT NULL,
  id_servicio INT NOT NULL,
  CONSTRAINT pk_cita_servicio PRIMARY KEY (id_cita, id_servicio),
  CONSTRAINT fk_cs_cita FOREIGN KEY (id_cita) REFERENCES cita (id_cita)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_cs_servicio FOREIGN KEY (id_servicio) REFERENCES servicio (id_servicio)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE disponibilidad (
  id_disponibilidad INT NOT NULL AUTO_INCREMENT,
  id_barbero        INT NOT NULL,
  fecha             DATE NOT NULL,
  hora_inicio       TIME NOT NULL,
  hora_fin          TIME NOT NULL,
  disponible        TINYINT(1) NOT NULL DEFAULT 1,
  CONSTRAINT pk_disponibilidad PRIMARY KEY (id_disponibilidad),
  CONSTRAINT fk_disp_barbero FOREIGN KEY (id_barbero) REFERENCES barbero (id_barbero)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT ck_disp_horas CHECK (hora_fin > hora_inicio),
  CONSTRAINT ck_disp_disponible CHECK (disponible IN (0,1))
) ENGINE=InnoDB;

CREATE TABLE pago (
  id_pago     INT NOT NULL AUTO_INCREMENT,
  id_cita     INT NOT NULL,
  monto       DECIMAL(10,2) NOT NULL,
  fecha_pago  DATETIME NOT NULL,
  tipo        VARCHAR(20) NOT NULL,
  metodo_pago VARCHAR(30) NOT NULL,
  CONSTRAINT pk_pago PRIMARY KEY (id_pago),
  CONSTRAINT fk_pago_cita FOREIGN KEY (id_cita) REFERENCES cita (id_cita)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT ck_pago_monto CHECK (monto > 0),
  CONSTRAINT ck_pago_tipo CHECK (tipo IN ('Abono','Saldo'))
) ENGINE=InnoDB;

CREATE TABLE calificacion (
  id_calificacion INT NOT NULL AUTO_INCREMENT,
  id_cita         INT NOT NULL,
  puntuacion      INT NOT NULL,
  comentario      VARCHAR(200) NULL,
  fecha           DATE NOT NULL,
  CONSTRAINT pk_calificacion PRIMARY KEY (id_calificacion),
  CONSTRAINT uq_calificacion_cita UNIQUE (id_cita),
  CONSTRAINT fk_calif_cita FOREIGN KEY (id_cita) REFERENCES cita (id_cita)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT ck_calif_puntuacion CHECK (puntuacion BETWEEN 1 AND 5)
) ENGINE=InnoDB;

CREATE TABLE notificacion (
  id_notificacion INT NOT NULL AUTO_INCREMENT,
  id_cliente      INT NOT NULL,
  tipo            VARCHAR(30) NOT NULL,
  fecha_envio     DATETIME NOT NULL,
  mensaje         VARCHAR(255) NOT NULL,
  CONSTRAINT pk_notificacion PRIMARY KEY (id_notificacion),
  CONSTRAINT fk_notif_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE acceso_internet (
  id_acceso          INT NOT NULL AUTO_INCREMENT,
  id_cliente         INT NULL,
  nombre             VARCHAR(45) NOT NULL,
  correo             VARCHAR(60) NOT NULL,
  edad               INT NULL,
  acepta_promociones TINYINT(1) NOT NULL DEFAULT 0,
  fecha_acceso       DATETIME NOT NULL,
  CONSTRAINT pk_acceso PRIMARY KEY (id_acceso),
  CONSTRAINT fk_acceso_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente)
    ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT ck_acceso_edad CHECK (edad IS NULL OR edad > 0),
  CONSTRAINT ck_acceso_promo CHECK (acepta_promociones IN (0,1))
) ENGINE=InnoDB;

INSERT INTO cliente (nombre, correo, contrasena, fecha_nacimiento, puntos_estrella, es_cliente_fijo) VALUES
 ('José Peña',            'jose.pena@correo.com',   '$2y$10$hashEjemploClienteUno000000000000000000000000000000001', '1995-03-12', 5, 1),
 ('María Fernanda Gómez', 'maria.gomez@correo.com', '$2y$10$hashEjemploClienteDos000000000000000000000000000000002', '1999-07-25', 2, 0),
 ('Andrés Muñoz',         'andres.munoz@correo.com','$2y$10$hashEjemploClienteTres00000000000000000000000000000003', NULL,         0, 0),
 ('Camila Rodríguez',     'camila.rod@correo.com',  '$2y$10$hashEjemploClienteCuatro000000000000000000000000000004', '2001-11-02', 3, 0);
 
SELECT * FROM cliente;

INSERT INTO barbero (nombre, correo, contrasena, puesto, cupo_maximo) VALUES
 ('Carlos "El Mono" Ruiz', 'carlos.ruiz@barberia.com', '$2y$10$hashEjemploBarberoUno00000000000000000000000000000001', 1, 8),
 ('Luis Ángel Cárdenas',   'luis.cardenas@barberia.com','$2y$10$hashEjemploBarberoDos00000000000000000000000000000002', 2, 10),
 ('Sebastián Ñáñez',       'sebastian.nanez@barberia.com','$2y$10$hashEjemploBarberoTres000000000000000000000000000003', 3, 8);
 
SELECT * FROM barbero;

INSERT INTO servicio (nombre, precio, descripcion) VALUES
 ('Corte clásico',     20000.00, 'Corte tradicional con tijera y máquina'),
 ('Corte degradado',   25000.00, 'Degradado con acabado a navaja'),
 ('Perfilado de barba',15000.00, 'Perfilado y arreglo de barba'),
 ('Perfilado de cejas', 8000.00, 'Arreglo de cejas con navaja'),
 ('Lavado',            10000.00, 'Lavado y masaje capilar');
 
SELECT * FROM servicio;

INSERT INTO cita (id_cliente, id_barbero, fecha, hora, estado, puesto_asignado, numero_turno, tipo_shampoo, total_pagar, descuento, abono) VALUES
 (1, 1, '2026-10-13', '09:00:00', 'Atendida',  1,    1, 'Anticaspa',  40000.00, 6250.00, 16875.00),
 (2, 2, '2026-10-13', '10:00:00', 'Atendida',  2,    2, 'Hidratante', 30000.00,    0.00, 15000.00),
 (3, 1, '2026-10-14', '15:00:00', 'Pendiente', 1,    1, 'Neutro',     33000.00,    0.00, 16500.00),
 (4, 3, '2026-10-14', '16:00:00', 'Cancelada', NULL, 2, NULL,         20000.00,    0.00, 10000.00);
 
SELECT * FROM cita;

INSERT INTO cita_servicio (id_cita, id_servicio) VALUES
 (1,2),(1,3),(2,1),(2,5),(3,2),(3,4),(4,1);
 
SELECT * FROM cita_servicio;

INSERT INTO disponibilidad (id_barbero, fecha, hora_inicio, hora_fin, disponible) VALUES
 (1, '2026-10-13', '08:00:00', '12:00:00', 0),
 (1, '2026-10-14', '14:00:00', '18:00:00', 1),
 (2, '2026-10-13', '09:00:00', '13:00:00', 0),
 (2, '2026-10-15', '09:00:00', '17:00:00', 1),
 (3, '2026-10-14', '15:00:00', '19:00:00', 1);
 
SELECT * FROM disponibilidad;

INSERT INTO pago (id_cita, monto, fecha_pago, tipo, metodo_pago) VALUES
 (1, 16875.00, '2026-10-12 18:30:00', 'Abono', 'Nequi'),
 (1, 16875.00, '2026-10-13 09:50:00', 'Saldo', 'Efectivo'),
 (2, 15000.00, '2026-10-12 19:10:00', 'Abono', 'Tarjeta'),
 (2, 15000.00, '2026-10-13 10:45:00', 'Saldo', 'Efectivo'),
 (3, 16500.00, '2026-10-13 20:00:00', 'Abono', 'Nequi');
 
SELECT * FROM pago;

INSERT INTO calificacion (id_cita, puntuacion, comentario, fecha) VALUES
 (1, 5, 'Excelente atención y muy buen corte.', '2026-10-13'),
 (2, 4, 'Buen servicio, el local estaba muy limpio.', '2026-10-13');
 
SELECT * FROM calificacion;

INSERT INTO notificacion (id_cliente, tipo, fecha_envio, mensaje) VALUES
 (1, 'Registro',        '2026-10-01 10:00:00', 'Bienvenido a la Barbería El Mono, José.'),
 (1, 'Recordatorio Cita','2026-10-12 08:00:00', 'Mañana tienes cita a las 9:00 a. m. con Carlos.'),
 (2, 'Pago',            '2026-10-12 19:11:00', 'Recibimos tu abono de $15.000. El saldo se paga en la barbería.'),
 (3, 'Promocional',     '2026-10-13 09:00:00', 'Acumula 5 estrellas y obtén 25 % de descuento en tu corte.');
 
SELECT * FROM notificacion;

INSERT INTO acceso_internet (id_cliente, nombre, correo, edad, acepta_promociones, fecha_acceso) VALUES
 (NULL, 'Valentina Ospina', 'valen.ospina@correo.com', 24, 1, '2026-10-13 11:20:00'),
 (1,    'José Peña',        'jose.pena@correo.com',    31, 1, '2026-10-13 09:05:00'),
 (2,    'María Fernanda Gómez','maria.gomez@correo.com',27, 0, '2026-10-13 10:05:00'),
 (4,    'Camila Rodríguez', 'camila.rod@correo.com',   NULL,1, '2026-10-14 16:05:00');
 
SELECT * FROM acceso_internet;
