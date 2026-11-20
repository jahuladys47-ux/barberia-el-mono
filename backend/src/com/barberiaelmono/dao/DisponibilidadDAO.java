package com.barberiaelmono.dao;

import com.barberiaelmono.modelo.Disponibilidad;
import com.barberiaelmono.util.ConexionBD;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla disponibilidad (franjas de cada barbero).
 *
 * @author Jahulady Salinas Oviedo
 * @version 1.0
 * @since 2026
 */
public class DisponibilidadDAO {

    private static final String SQL_INSERTAR =
            "INSERT INTO disponibilidad (id_barbero, fecha, hora_inicio, hora_fin, disponible) "
            + "VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_LISTAR =
            "SELECT id_disponibilidad, fecha, hora_inicio, hora_fin, disponible "
            + "FROM disponibilidad WHERE id_barbero = ? ORDER BY fecha, hora_inicio";

    /**
     * Guarda una franja de disponibilidad de un barbero.
     *
     * @param idBarbero id del barbero dueño de la franja.
     * @param franja franja con fecha, hora de inicio y hora de fin.
     * @throws SQLException si falla la inserción.
     */
    public void insertar(int idBarbero, Disponibilidad franja) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR,
                        Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setInt(1, idBarbero);
            sentencia.setDate(2, Date.valueOf(franja.getFecha()));
            sentencia.setTime(3, Time.valueOf(franja.getHoraInicio()));
            sentencia.setTime(4, Time.valueOf(franja.getHoraFin()));
            sentencia.setBoolean(5, franja.isDisponible());
            sentencia.executeUpdate();
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) {
                    franja.setIdDisponibilidad(claves.getInt(1));
                }
            }
        }
    }

    /**
     * Lista las franjas registradas por un barbero.
     *
     * @param idBarbero id del barbero.
     * @return franjas ordenadas por fecha y hora de inicio.
     * @throws SQLException si falla la consulta.
     */
    public List<Disponibilidad> listarPorBarbero(int idBarbero) throws SQLException {
        List<Disponibilidad> franjas = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_LISTAR)) {
            sentencia.setInt(1, idBarbero);
            try (ResultSet fila = sentencia.executeQuery()) {
                while (fila.next()) {
                    Disponibilidad franja = new Disponibilidad(fila.getDate("fecha").toLocalDate(),
                            fila.getTime("hora_inicio").toLocalTime(),
                            fila.getTime("hora_fin").toLocalTime());
                    franja.setIdDisponibilidad(fila.getInt("id_disponibilidad"));
                    franja.setDisponible(fila.getBoolean("disponible"));
                    franjas.add(franja);
                }
            }
        }
        return franjas;
    }
}
