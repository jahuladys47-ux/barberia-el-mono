package com.barberiaelmono.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Entrega la conexión JDBC a la base de datos barberia_el_mono. Los datos de
 * conexión se leen del archivo db.properties, que no se sube al repositorio.
 *
 * @author Jahulady Salinas Oviedo
 * @version 1.0
 * @since 2026
 */
public final class ConexionBD {

    private static final String ARCHIVO_CONFIGURACION = "db.properties";

    private ConexionBD() {
    }

    /**
     * Abre una conexión nueva a MySQL.
     *
     * @return conexión lista para usar; quien la pide debe cerrarla.
     * @throws SQLException si no se puede leer la configuración o conectar.
     */
    public static Connection obtenerConexion() throws SQLException {
        Properties config = new Properties();
        try (FileInputStream archivo = new FileInputStream(ARCHIVO_CONFIGURACION)) {
            config.load(archivo);
        } catch (IOException e) {
            throw new SQLException("No se encontró el archivo " + ARCHIVO_CONFIGURACION, e);
        }
        return DriverManager.getConnection(config.getProperty("url"),
                config.getProperty("usuario"), config.getProperty("clave"));
    }
}
