package com.barberiaelmono.dao;

import com.barberiaelmono.modelo.Cita;
import com.barberiaelmono.modelo.Servicio;
import com.barberiaelmono.util.ConexionBD;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;

/**
 * Acceso a las tablas cita y cita_servicio.
 *
 * @author Jahulady Salinas Oviedo
 * @version 1.0
 * @since 2026
 */
public class CitaDAO {

    private static final String SQL_INSERTAR_CITA =
            "INSERT INTO cita (id_cliente, id_barbero, fecha, hora, estado, numero_turno, "
            + "total_pagar, descuento, abono) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERTAR_SERVICIO =
            "INSERT INTO cita_servicio (id_cita, id_servicio) VALUES (?, ?)";
    private static final String SQL_CONTAR_DEL_DIA =
            "SELECT COUNT(*) FROM cita WHERE id_barbero = ? AND fecha = ? AND estado <> 'Cancelada'";
    private static final String SQL_ACTUALIZAR_ESTADO =
            "UPDATE cita SET estado = ? WHERE id_cita = ?";

    /**
     * Guarda la cita y sus servicios en una sola transacción: si algo falla,
     * no queda nada guardado a medias.
     *
     * @param cita cita ya agendada, con cliente y barbero que existen en la base de datos.
     * @throws SQLException si falla alguna inserción.
     */
    public void insertar(Cita cita) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            conexion.setAutoCommit(false);
            try {
                insertarCita(conexion, cita);
                insertarServicios(conexion, cita);
                conexion.commit();
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        }
    }

    private void insertarCita(Connection conexion, Cita cita) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR_CITA,
                Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setInt(1, cita.getCliente().getIdCliente());
            sentencia.setInt(2, cita.getBarbero().getIdBarbero());
            sentencia.setDate(3, Date.valueOf(cita.getFecha()));
            sentencia.setTime(4, Time.valueOf(cita.getHora()));
            sentencia.setString(5, cita.getEstado());
            sentencia.setInt(6, cita.getNumeroTurno());
            sentencia.setDouble(7, cita.getTotalPagar());
            sentencia.setDouble(8, cita.getDescuento());
            sentencia.setDouble(9, cita.getAbono());
            sentencia.executeUpdate();
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) {
                    cita.setIdCita(claves.getInt(1));
                }
            }
        }
    }

    private void insertarServicios(Connection conexion, Cita cita) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR_SERVICIO)) {
            for (Servicio servicio : cita.getServicios()) {
                sentencia.setInt(1, cita.getIdCita());
                sentencia.setInt(2, servicio.getIdServicio());
                sentencia.addBatch();
            }
            sentencia.executeBatch();
        }
    }

    /**
     * Cuenta las citas no canceladas que un barbero tiene en un día.
     *
     * @param idBarbero id del barbero.
     * @param fecha día a consultar.
     * @return cantidad de citas de ese día.
     * @throws SQLException si falla la consulta.
     */
    public int contarCitasDelDia(int idBarbero, LocalDate fecha) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_CONTAR_DEL_DIA)) {
            sentencia.setInt(1, idBarbero);
            sentencia.setDate(2, Date.valueOf(fecha));
            try (ResultSet fila = sentencia.executeQuery()) {
                fila.next();
                return fila.getInt(1);
            }
        }
    }

    /**
     * Cambia el estado de una cita (Pendiente, Atendida o Cancelada).
     *
     * @param idCita id de la cita.
     * @param estado nuevo estado.
     * @throws SQLException si falla la actualización.
     */
    public void actualizarEstado(int idCita, String estado) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR_ESTADO)) {
            sentencia.setString(1, estado);
            sentencia.setInt(2, idCita);
            sentencia.executeUpdate();
        }
    }
}
