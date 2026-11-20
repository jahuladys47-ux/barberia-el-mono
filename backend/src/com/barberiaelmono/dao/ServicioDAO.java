package com.barberiaelmono.dao;

import com.barberiaelmono.modelo.Servicio;
import com.barberiaelmono.util.ConexionBD;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla servicio (catálogo de la barbería).
 *
 * @author Jahulady Salinas Oviedo
 * @version 1.0
 * @since 2026
 */
public class ServicioDAO {

    private static final String SQL_LISTAR =
            "SELECT id_servicio, nombre, precio, descripcion FROM servicio ORDER BY id_servicio";

    /**
     * Lista todos los servicios del catálogo.
     *
     * @return servicios ordenados por su id.
     * @throws SQLException si falla la consulta.
     */
    public List<Servicio> listar() throws SQLException {
        List<Servicio> catalogo = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtenerConexion();
                Statement sentencia = conexion.createStatement();
                ResultSet fila = sentencia.executeQuery(SQL_LISTAR)) {
            while (fila.next()) {
                Servicio servicio = new Servicio(fila.getString("nombre"),
                        fila.getDouble("precio"), fila.getString("descripcion"));
                servicio.setIdServicio(fila.getInt("id_servicio"));
                catalogo.add(servicio);
            }
        }
        return catalogo;
    }
}
