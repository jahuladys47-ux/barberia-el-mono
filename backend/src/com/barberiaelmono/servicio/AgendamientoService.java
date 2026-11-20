package com.barberiaelmono.servicio;

import com.barberiaelmono.excepcion.CitaNoDisponibleException;
import com.barberiaelmono.modelo.Barbero;
import com.barberiaelmono.modelo.Cita;
import com.barberiaelmono.modelo.Pago;

/**
 * Reglas de negocio del agendamiento: disponibilidad del barbero, cupo
 * diario, número de turno, abono y estrellas del cliente.
 *
 * @author Jahulady Salinas Oviedo
 * @version 1.0
 * @since 2026
 */
public class AgendamientoService {

    /**
     * Valida y confirma una cita pendiente de agendar.
     *
     * @param cita cita con cliente, barbero, fecha, hora y servicios.
     * @param citasDelDia citas que el barbero ya tiene ese día.
     * @throws CitaNoDisponibleException si el barbero no tiene franja libre o llegó a su cupo.
     * @throws IllegalStateException si la cita no incluye ningún servicio.
     */
    public void agendar(Cita cita, int citasDelDia) throws CitaNoDisponibleException {
        if (cita.getServicios().isEmpty()) {
            throw new IllegalStateException("La cita debe incluir al menos un servicio.");
        }
        Barbero barbero = cita.getBarbero();
        if (!barbero.estaDisponible(cita.getFecha(), cita.getHora())) {
            throw new CitaNoDisponibleException("El barbero no está disponible en ese horario.");
        }
        if (!barbero.tieneCupo(citasDelDia)) {
            throw new CitaNoDisponibleException("El barbero ya completó su cupo máximo del día.");
        }
        cita.setNumeroTurno(citasDelDia + 1);
        cita.recalcular();
    }

    /**
     * Crea el pago del abono del 50 % de una cita agendada.
     *
     * @param cita cita ya agendada.
     * @param metodoPago canal de pago (Efectivo, Nequi, Tarjeta).
     * @return pago de tipo Abono por el valor calculado de la cita.
     */
    public Pago registrarAbono(Cita cita, String metodoPago) {
        return Pago.abonoDe(cita, metodoPago);
    }

    /**
     * Marca la cita como atendida y suma una estrella al cliente por cada
     * servicio recibido.
     *
     * @param cita cita pendiente que se atendió.
     */
    public void completarCita(Cita cita) {
        cita.atender();
        cita.getCliente().sumarEstrellas(cita.getServicios().size());
    }
}
