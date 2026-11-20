package com.barberiaelmono.modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Reserva de un cliente con un barbero. Calcula el total, el descuento del
 * cliente fijo y el abono del 50 %.
 *
 * @author Jahulady Salinas Oviedo
 * @version 1.0
 * @since 2026
 */
public class Cita {

    /** Hora de apertura de la barbería. */
    public static final LocalTime HORA_APERTURA = LocalTime.of(8, 0);
    /** Hora de cierre de la barbería. */
    public static final LocalTime HORA_CIERRE = LocalTime.of(19, 0);
    /** Porcentaje del valor que se abona al agendar. */
    public static final double PORCENTAJE_ABONO = 0.50;
    /** Descuento en el corte para el cliente fijo. */
    public static final double DESCUENTO_CLIENTE_FIJO = 0.25;

    public static final String ESTADO_PENDIENTE = "Pendiente";
    public static final String ESTADO_ATENDIDA = "Atendida";
    public static final String ESTADO_CANCELADA = "Cancelada";

    private int idCita;
    private final Cliente cliente;
    private final Barbero barbero;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado = ESTADO_PENDIENTE;
    private int numeroTurno;
    private final List<Servicio> servicios = new ArrayList<>();
    private double totalPagar;
    private double descuento;
    private double abono;

    /**
     * Crea una cita pendiente.
     *
     * @param cliente cliente que reserva.
     * @param barbero barbero elegido.
     * @param fecha día de la cita (hoy o posterior).
     * @param hora hora de la cita (entre 8:00 a. m. y 7:00 p. m.).
     * @throws IllegalArgumentException si falta un dato o la fecha u hora no son válidas.
     */
    public Cita(Cliente cliente, Barbero barbero, LocalDate fecha, LocalTime hora) {
        if (cliente == null || barbero == null) {
            throw new IllegalArgumentException("La cita necesita un cliente y un barbero.");
        }
        this.cliente = cliente;
        this.barbero = barbero;
        setFecha(fecha);
        setHora(hora);
    }

    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Barbero getBarbero() {
        return barbero;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    /**
     * Cambia la fecha de la cita.
     *
     * @param fecha día de la cita.
     * @throws IllegalArgumentException si es nula o anterior a hoy.
     */
    public final void setFecha(LocalDate fecha) {
        if (fecha == null || fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha no puede ser anterior a hoy.");
        }
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    /**
     * Cambia la hora de la cita.
     *
     * @param hora hora entre la apertura y el cierre.
     * @throws IllegalArgumentException si es nula o está fuera del horario.
     */
    public final void setHora(LocalTime hora) {
        if (hora == null || hora.isBefore(HORA_APERTURA) || hora.isAfter(HORA_CIERRE)) {
            throw new IllegalArgumentException("La hora debe estar entre las 8:00 a. m. y las 7:00 p. m.");
        }
        this.hora = hora;
    }

    public String getEstado() {
        return estado;
    }

    public int getNumeroTurno() {
        return numeroTurno;
    }

    public void setNumeroTurno(int numeroTurno) {
        if (numeroTurno <= 0) {
            throw new IllegalArgumentException("El número de turno debe ser mayor que cero.");
        }
        this.numeroTurno = numeroTurno;
    }

    public List<Servicio> getServicios() {
        return new ArrayList<>(servicios);
    }

    public double getTotalPagar() {
        return totalPagar;
    }

    public double getDescuento() {
        return descuento;
    }

    public double getAbono() {
        return abono;
    }

    /**
     * Calcula lo que falta por pagar al llegar a la barbería.
     *
     * @return total menos descuento menos abono.
     */
    public double getSaldo() {
        return redondear(totalPagar - descuento - abono);
    }

    /**
     * Agrega un servicio a la cita y recalcula los valores.
     *
     * @param servicio servicio del catálogo.
     */
    public void agregarServicio(Servicio servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio no puede ser nulo.");
        }
        servicios.add(servicio);
        recalcular();
    }

    /**
     * Suma el precio de todos los servicios de la cita.
     *
     * @return valor bruto de la cita.
     */
    public double calcularTotal() {
        double total = 0.0;
        for (Servicio servicio : servicios) {
            total += servicio.getPrecio();
        }
        return redondear(total);
    }

    /**
     * Calcula el descuento: 25 % del precio del corte si el cliente es fijo.
     *
     * @return valor del descuento, o cero si no aplica.
     */
    public double calcularDescuento() {
        if (!cliente.isEsClienteFijo()) {
            return 0.0;
        }
        for (Servicio servicio : servicios) {
            if (servicio.esCorte()) {
                return redondear(servicio.getPrecio() * DESCUENTO_CLIENTE_FIJO);
            }
        }
        return 0.0;
    }

    /**
     * Calcula el abono: la mitad del valor después de aplicar el descuento.
     *
     * @return valor que se paga al agendar.
     */
    public double calcularAbono() {
        return redondear((calcularTotal() - calcularDescuento()) * PORCENTAJE_ABONO);
    }

    /** Recalcula total, descuento y abono con los servicios actuales. */
    public void recalcular() {
        this.totalPagar = calcularTotal();
        this.descuento = calcularDescuento();
        this.abono = calcularAbono();
    }

    /**
     * Marca la cita como atendida.
     *
     * @throws IllegalStateException si la cita no está pendiente.
     */
    public void atender() {
        cambiarEstado(ESTADO_ATENDIDA);
    }

    /**
     * Marca la cita como cancelada.
     *
     * @throws IllegalStateException si la cita no está pendiente.
     */
    public void cancelar() {
        cambiarEstado(ESTADO_CANCELADA);
    }

    private void cambiarEstado(String nuevoEstado) {
        if (!ESTADO_PENDIENTE.equals(estado)) {
            throw new IllegalStateException("Solo se puede cambiar el estado de una cita pendiente.");
        }
        this.estado = nuevoEstado;
    }

    private static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
