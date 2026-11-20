package com.barberiaelmono.modelo;

/**
 * Servicio del catálogo de la barbería (por ejemplo, corte clásico o lavado).
 * Los datos se cargan desde la tabla servicio de la base de datos.
 *
 * @author Jahulady Salinas Oviedo
 * @version 1.0
 * @since 2026
 */
public class Servicio {

    private int idServicio;
    private final String nombre;
    private final double precio;
    private final String descripcion;

    /**
     * Crea un servicio del catálogo.
     *
     * @param nombre nombre comercial del servicio.
     * @param precio tarifa vigente (cero o mayor).
     * @param descripcion detalle del servicio; puede ser nulo.
     * @throws IllegalArgumentException si el nombre está vacío o el precio es negativo.
     */
    public Servicio(String nombre, double precio, String descripcion) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del servicio no puede estar vacío.");
        }
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        this.nombre = nombre.trim();
        this.precio = precio;
        this.descripcion = descripcion;
    }

    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Indica si el servicio es un corte, que es el único al que se aplica
     * el descuento del cliente fijo.
     *
     * @return true si el nombre empieza por "Corte".
     */
    public boolean esCorte() {
        return nombre.toLowerCase().startsWith("corte");
    }
}
