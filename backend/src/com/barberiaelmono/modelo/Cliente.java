package com.barberiaelmono.modelo;

import java.time.LocalDate;

/**
 * Cliente de la barbería. Acumula una estrella por servicio y, con 5
 * estrellas, es cliente fijo y recibe el descuento en el corte.
 *
 * @author Jahulady Salinas Oviedo
 * @version 1.0
 * @since 2026
 */
public class Cliente extends Persona {

    /** Estrellas necesarias para ser cliente fijo. */
    public static final int ESTRELLAS_CLIENTE_FIJO = 5;

    private int idCliente;
    private LocalDate fechaNacimiento;
    private int puntosEstrella;
    private boolean esClienteFijo;

    /**
     * Crea un cliente nuevo, sin estrellas.
     *
     * @param nombre nombre completo.
     * @param correo correo electrónico.
     * @param contrasena hash de la contraseña.
     */
    public Cliente(String nombre, String correo, String contrasena) {
        super(nombre, correo, contrasena);
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public int getPuntosEstrella() {
        return puntosEstrella;
    }

    /**
     * Cambia las estrellas del cliente y actualiza si es cliente fijo.
     *
     * @param puntosEstrella cantidad de estrellas.
     * @throws IllegalArgumentException si es negativa.
     */
    public void setPuntosEstrella(int puntosEstrella) {
        if (puntosEstrella < 0) {
            throw new IllegalArgumentException("Los puntos no pueden ser negativos.");
        }
        this.puntosEstrella = puntosEstrella;
        this.esClienteFijo = puntosEstrella >= ESTRELLAS_CLIENTE_FIJO;
    }

    public boolean isEsClienteFijo() {
        return esClienteFijo;
    }

    /**
     * Suma estrellas por los servicios recibidos.
     *
     * @param cantidad estrellas a sumar (mayor que cero).
     */
    public void sumarEstrellas(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de estrellas debe ser mayor que cero.");
        }
        setPuntosEstrella(puntosEstrella + cantidad);
    }
}
