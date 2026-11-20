package com.barberiaelmono.dao;

import com.barberiaelmono.modelo.Barbero;
import com.barberiaelmono.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Acceso a la tabla barbero.
 *
 * @author Jahulady Salinas Oviedo
 * @version 1.0
 * @since 2026
 */
public class BarberoDAO {

    private static final String SQL_BUSCAR_CORREO =
            "SELECT id_barbero, nombre, correo, contrasena, puesto, cupo_maximo "
            + "FROM barbero WHERE correo = ?";
    private static final String SQL_ACTUALIZAR_CUPO =
            "UPDATE barbero SET cupo_maximo = ? WHERE id_barbero = ?";

    /**
     * Busca un barbero por su correo, que se usa para iniciar sesión.
     *
     * @param correo correo del barbero.
     * @return el barbero, o null si no existe.
     * @throws SQLException si falla la consulta.
     */
    public Barbero buscarPorCorreo(String correo) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR_CORREO)) {
            sentencia.setString(1, correo);
            try (ResultSet fila = sentencia.executeQuery()) {
                if (!fila.next()) {
                    return null;
                }
                Barbero barbero = new Barbero(fila.getString("nombre"), fila.getString("correo"),
                        fila.getString("contrasena"), fila.getInt("puesto"), fila.getInt("cupo_maximo"));
                barbero.setIdBarbero(fila.getInt("id_barbero"));
                return barbero;
            }
        }
    }

    /**
     * Actualiza el cupo máximo diario de un barbero.
     *
     * @param barbero barbero con su id ya asignado.
     * @throws SQLException si falla la actualización.
     */
    public void actualizarCupo(Barbero barbero) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR_CUPO)) {
            sentencia.setInt(1, barbero.getCupoMaximo());
            sentencia.setInt(2, barbero.getIdBarbero());
            sentencia.executeUpdate();
        }
    }
}
