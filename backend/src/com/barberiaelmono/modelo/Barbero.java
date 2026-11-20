package com.barberiaelmono.modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Barbero que atiende las citas. Registra su disponibilidad por día y hora
 * y tiene un cupo máximo diario de clientes.
 *
 * @author Jahulady Salinas Oviedo
 * @version 1.0
 * @since 2026
 */
public class Barbero extends Persona {

    private int idBarbero;
    private int puesto;
    private int cupoMaximo;
    private final List<Disponibilidad> agenda = new ArrayList<>();

    /**
     * Crea un barbero.
     *
     * @param nombre nombre completo.
     * @param correo correo electrónico.
     * @param contrasena hash de la contraseña.
     * @param puesto número de la silla de trabajo (mayor que cero).
     * @param cupoMaximo máximo de clientes por día (mayor que cero).
     */
    public Barbero(String nombre, String correo, String contrasena, int puesto, int cupoMaximo) {
        super(nombre, correo, contrasena);
        setPuesto(puesto);
        setCupoMaximo(cupoMaximo);
    }

    public int getIdBarbero() {
        return idBarbero;
    }

    public void setIdBarbero(int idBarbero) {
        this.idBarbero = idBarbero;
    }

    public int getPuesto() {
        return puesto;
    }

    public final void setPuesto(int puesto) {
        if (puesto <= 0) {
            throw new IllegalArgumentException("El puesto debe ser mayor que cero.");
        }
        this.puesto = puesto;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public final void setCupoMaximo(int cupoMaximo) {
        if (cupoMaximo <= 0) {
            throw new IllegalArgumentException("El cupo máximo debe ser mayor que cero.");
        }
        this.cupoMaximo = cupoMaximo;
    }

    public List<Disponibilidad> getAgenda() {
        return new ArrayList<>(agenda);
    }

    /**
     * Agrega una franja de disponibilidad a la agenda del barbero.
     *
     * @param disponibilidad franja con fecha, hora de inicio y hora de fin.
     */
    public void registrarDisponibilidad(Disponibilidad disponibilidad) {
        if (disponibilidad == null) {
            throw new IllegalArgumentException("La disponibilidad no puede ser nula.");
        }
        agenda.add(disponibilidad);
    }

    /**
     * Indica si el barbero tiene una franja habilitada que cubra la hora pedida.
     *
     * @param fecha día de la cita.
     * @param hora hora de la cita.
     * @return true si hay una franja disponible para ese día y hora.
     */
    public boolean estaDisponible(LocalDate fecha, LocalTime hora) {
        for (Disponibilidad franja : agenda) {
            if (franja.getFecha().equals(fecha) && franja.cubre(hora)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indica si todavía puede recibir más clientes ese día.
     *
     * @param citasDelDia cantidad de citas que ya tiene el barbero ese día.
     * @return true si no ha llegado al cupo máximo.
     */
    public boolean tieneCupo(int citasDelDia) {
        return citasDelDia < cupoMaximo;
    }
}
