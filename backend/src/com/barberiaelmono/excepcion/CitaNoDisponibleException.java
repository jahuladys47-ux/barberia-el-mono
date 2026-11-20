package com.barberiaelmono.excepcion;

/**
 * Se lanza cuando una cita no se puede agendar porque el barbero no tiene
 * disponibilidad en la fecha y hora pedidas o ya completó su cupo máximo.
 *
 * @author Jahulady Salinas Oviedo
 * @version 1.0
 * @since 2026
 */
public class CitaNoDisponibleException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Crea la excepción con un mensaje que explica el motivo.
     *
     * @param mensaje motivo por el cual no se puede agendar la cita.
     */
    public CitaNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
