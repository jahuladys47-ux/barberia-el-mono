package com.barberiaelmono.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Franja de disponibilidad de un barbero: un día, una hora de inicio y una
 * hora de fin.
 *
 * @author Jahulady Salinas Oviedo
 * @version 1.0
 * @since 2026
 */
public class Disponibilidad {

    private int idDisponibilidad;
    private final LocalDate fecha;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;
    private boolean disponible;

    /**
     * Crea una franja habilitada.
     *
     * @param fecha día de la franja.
     * @param horaInicio hora en que empieza.
     * @param horaFin hora en que termina; debe ser posterior al inicio.
     * @throws IllegalArgumentException si falta algún dato o la hora de fin no es posterior.
     */
    public Disponibilidad(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        if (fecha == null || horaInicio == null || horaFin == null) {
            throw new IllegalArgumentException("La fecha y las horas son obligatorias.");
        }
        if (!horaFin.isAfter(horaInicio)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la de inicio.");
        }
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.disponible = true;
    }

    public int getIdDisponibilidad() {
        return idDisponibilidad;
    }

    public void setIdDisponibilidad(int idDisponibilidad) {
        this.idDisponibilidad = idDisponibilidad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    /**
     * Comprueba si una hora cae dentro de la franja y la franja está habilitada.
     *
     * @param hora hora a revisar.
     * @return true si la franja está habilitada y la hora está entre inicio y fin.
     */
    public boolean cubre(LocalTime hora) {
        return disponible && !hora.isBefore(horaInicio) && hora.isBefore(horaFin);
    }
}
