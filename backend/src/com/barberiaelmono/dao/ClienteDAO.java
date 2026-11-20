package com.barberiaelmono.dao;

import com.barberiaelmono.modelo.Cliente;
import com.barberiaelmono.util.ConexionBD;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Acceso a la tabla cliente.
 *
 * @author Jahulady Salinas Oviedo
 * @version 1.0
 * @since 2026
 */
public class ClienteDAO {

    private static final String SQL_INSERTAR =
            "INSERT INTO cliente (nombre, correo, contrasena, fecha_nacimiento, puntos_estrella, "
            + "es_cliente_fijo) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_BUSCAR_CORREO =
            "SELECT id_cliente, nombre, correo, contrasena, fecha_nacimiento, puntos_estrella "
            + "FROM cliente WHERE correo = ?";
    private static final String SQL_ACTUALIZAR_ESTRELLAS =
            "UPDATE cliente SET puntos_estrella = ?, es_cliente_fijo = ? WHERE id_cliente = ?";

    /**
     * Registra un cliente nuevo y le asigna el id generado por la base de datos.
     *
     * @param cliente cliente con la contraseña ya convertida en hash.
     * @throws SQLException si el correo ya existe o falla la conexión.
     */
    public void insertar(Cliente cliente) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR,
                        Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setString(1, cliente.getNombre());
            sentencia.setString(2, cliente.getCorreo());
            sentencia.setString(3, cliente.getContrasena());
            sentencia.setDate(4, cliente.getFechaNacimiento() == null
                    ? null : Date.valueOf(cliente.getFechaNacimiento()));
            sentencia.setInt(5, cliente.getPuntosEstrella());
            sentencia.setBoolean(6, cliente.isEsClienteFijo());
            sentencia.executeUpdate();
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) {
                    cliente.setIdCliente(claves.getInt(1));
                }
            }
        }
    }

    /**
     * Busca un cliente por su correo, que se usa para iniciar sesión.
     *
     * @param correo correo del cliente.
     * @return el cliente, o null si no existe.
     * @throws SQLException si falla la consulta.
     */
    public Cliente buscarPorCorreo(String correo) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR_CORREO)) {
            sentencia.setString(1, correo);
            try (ResultSet fila = sentencia.executeQuery()) {
                if (!fila.next()) {
                    return null;
                }
                Cliente cliente = new Cliente(fila.getString("nombre"), fila.getString("correo"),
                        fila.getString("contrasena"));
                cliente.setIdCliente(fila.getInt("id_cliente"));
                Date nacimiento = fila.getDate("fecha_nacimiento");
                if (nacimiento != null) {
                    cliente.setFechaNacimiento(nacimiento.toLocalDate());
                }
                cliente.setPuntosEstrella(fila.getInt("puntos_estrella"));
                return cliente;
            }
        }
    }

    /**
     * Guarda las estrellas del cliente y si es cliente fijo.
     *
     * @param cliente cliente con su id ya asignado.
     * @throws SQLException si falla la actualización.
     */
    public void actualizarEstrellas(Cliente cliente) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR_ESTRELLAS)) {
            sentencia.setInt(1, cliente.getPuntosEstrella());
            sentencia.setBoolean(2, cliente.isEsClienteFijo());
            sentencia.setInt(3, cliente.getIdCliente());
            sentencia.executeUpdate();
        }
    }
}
