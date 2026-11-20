package com.barberiaelmono.modelo;

import java.time.LocalDateTime;

/**
 * Pago de una cita: el abono al agendar o el saldo al llegar.
 *
 * @author Jahulady Salinas Oviedo
 * @version 1.0
 * @since 2026
 */
public class Pago {

    public static final String TIPO_ABONO = "Abono";
    public static final String TIPO_SALDO = "Saldo";

    private int idPago;
    private final Cita cita;
    private final double monto;
    private final LocalDateTime fechaPago;
    private final String tipo;
    private final String metodoPago;

    /**
     * Crea un pago.
     *
     * @param cita cita que se liquida.
     * @param monto valor pagado (mayor que cero).
     * @param tipo "Abono" o "Saldo".
     * @param metodoPago canal de pago, por ejemplo Efectivo, Nequi o Tarjeta.
     * @throws IllegalArgumentException si algún dato no es válido.
     */
    public Pago(Cita cita, double monto, String tipo, String metodoPago) {
        if (cita == null) {
            throw new IllegalArgumentException("El pago necesita una cita.");
        }
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }
        if (!TIPO_ABONO.equals(tipo) && !TIPO_SALDO.equals(tipo)) {
            throw new IllegalArgumentException("El tipo de pago debe ser Abono o Saldo.");
        }
        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            throw new IllegalArgumentException("El método de pago es obligatorio.");
        }
        this.cita = cita;
        this.monto = monto;
        this.tipo = tipo;
        this.metodoPago = metodoPago.trim();
        this.fechaPago = LocalDateTime.now();
    }

    /**
     * Crea el pago del abono de una cita con el valor calculado por la cita.
     *
     * @param cita cita que se agenda.
     * @param metodoPago canal de pago.
     * @return pago de tipo Abono.
     */
    public static Pago abonoDe(Cita cita, String metodoPago) {
        return new Pago(cita, cita.getAbono(), TIPO_ABONO, metodoPago);
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public Cita getCita() {
        return cita;
    }

    public double getMonto() {
        return monto;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public String getTipo() {
        return tipo;
    }

    public String getMetodoPago() {
        return metodoPago;
    }
}
