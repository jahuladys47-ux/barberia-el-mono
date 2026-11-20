package com.barberiaelmono.principal;

import com.barberiaelmono.excepcion.CitaNoDisponibleException;
import com.barberiaelmono.modelo.Barbero;
import com.barberiaelmono.modelo.Cita;
import com.barberiaelmono.modelo.Cliente;
import com.barberiaelmono.modelo.Disponibilidad;
import com.barberiaelmono.modelo.Pago;
import com.barberiaelmono.modelo.Servicio;
import com.barberiaelmono.servicio.AgendamientoService;
import com.barberiaelmono.util.SeguridadUtil;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Pruebas de las reglas de negocio de los módulos, sin base de datos y sin
 * librerías externas. Cada prueba imprime OK o FALLO; si alguna falla, el
 * programa termina con código 1.
 *
 * @author Jahulady Salinas Oviedo
 * @version 1.0
 * @since 2026
 */
public class PruebasModulos {

    private static int total = 0;
    private static int fallidas = 0;

    /** Acción que se espera que lance una excepción. */
    private interface Accion {
        void ejecutar() throws Exception;
    }

    /**
     * Ejecuta todas las pruebas.
     *
     * @param args no se usan.
     * @throws Exception si una prueba falla de forma inesperada.
     */
    public static void main(String[] args) throws Exception {
        LocalDate manana = LocalDate.now().plusDays(1);
        String hash = SeguridadUtil.hashear("MiClave2026");
        Servicio clasico = new Servicio("Corte clásico", 20000.00, "Corte tradicional con tijera y máquina");
        Servicio degradado = new Servicio("Corte degradado", 25000.00, "Degradado con acabado a navaja");
        Servicio barba = new Servicio("Perfilado de barba", 15000.00, "Perfilado y arreglo de barba");
        Servicio lavado = new Servicio("Lavado", 10000.00, "Lavado y masaje capilar");
        Barbero carlos = new Barbero("Carlos \"El Mono\" Ruiz", "carlos.ruiz@barberia.com", hash, 1, 8);
        carlos.registrarDisponibilidad(new Disponibilidad(manana, LocalTime.of(8, 0), LocalTime.of(12, 0)));
        AgendamientoService servicio = new AgendamientoService();

        // Cliente fijo: datos de la cita 1 de la base de datos (total 40.000, descuento 6.250, abono 16.875)
        Cliente jose = new Cliente("José Peña", "jose.pena@correo.com", hash);
        jose.setPuntosEstrella(5);
        Cita cita1 = new Cita(jose, carlos, manana, LocalTime.of(9, 0));
        cita1.agregarServicio(degradado);
        cita1.agregarServicio(barba);
        verificar("Cita 1: total 40.000", cita1.getTotalPagar() == 40000.00);
        verificar("Cita 1: descuento 25 % del corte = 6.250", cita1.getDescuento() == 6250.00);
        verificar("Cita 1: abono = 16.875", cita1.getAbono() == 16875.00);
        verificar("Cita 1: saldo = 16.875", cita1.getSaldo() == 16875.00);

        // Cliente sin descuento: datos de la cita 2 (total 30.000, descuento 0, abono 15.000)
        Cliente maria = new Cliente("María Fernanda Gómez", "maria.gomez@correo.com", hash);
        maria.setPuntosEstrella(2);
        Cita cita2 = new Cita(maria, carlos, manana, LocalTime.of(10, 0));
        cita2.agregarServicio(clasico);
        cita2.agregarServicio(lavado);
        verificar("Cita 2: total 30.000", cita2.getTotalPagar() == 30000.00);
        verificar("Cita 2: sin descuento", cita2.getDescuento() == 0.0);
        verificar("Cita 2: abono = 15.000", cita2.getAbono() == 15000.00);

        // Descuento solo aplica al corte
        Cita soloBarba = new Cita(jose, carlos, manana, LocalTime.of(9, 0));
        soloBarba.agregarServicio(barba);
        verificar("Sin corte no hay descuento", soloBarba.getDescuento() == 0.0);

        // Estrellas y cliente fijo
        Cliente nuevo = new Cliente("Andrés Muñoz", "andres.munoz@correo.com", hash);
        verificar("Cliente nuevo no es fijo", !nuevo.isEsClienteFijo());
        nuevo.sumarEstrellas(4);
        verificar("Con 4 estrellas no es fijo", !nuevo.isEsClienteFijo());
        nuevo.sumarEstrellas(1);
        verificar("Con 5 estrellas es fijo", nuevo.isEsClienteFijo());

        // Agendamiento
        servicio.agendar(cita1, 0);
        verificar("Agendar asigna turno 1", cita1.getNumeroTurno() == 1);
        servicio.agendar(cita2, 1);
        verificar("Agendar asigna turno 2", cita2.getNumeroTurno() == 2);
        esperaExcepcion("Sin franja de disponibilidad", CitaNoDisponibleException.class, () -> {
            Cita tarde = new Cita(jose, carlos, manana, LocalTime.of(15, 0));
            tarde.agregarServicio(clasico);
            servicio.agendar(tarde, 0);
        });
        esperaExcepcion("Cupo máximo completo", CitaNoDisponibleException.class, () -> {
            Cita llena = new Cita(jose, carlos, manana, LocalTime.of(9, 0));
            llena.agregarServicio(clasico);
            servicio.agendar(llena, carlos.getCupoMaximo());
        });
        esperaExcepcion("Cita sin servicios", IllegalStateException.class, () ->
                servicio.agendar(new Cita(jose, carlos, manana, LocalTime.of(9, 0)), 0));

        // Validaciones de datos
        esperaExcepcion("Hora antes de las 8:00", IllegalArgumentException.class, () ->
                new Cita(jose, carlos, manana, LocalTime.of(7, 59)));
        esperaExcepcion("Hora después de las 19:00", IllegalArgumentException.class, () ->
                new Cita(jose, carlos, manana, LocalTime.of(19, 1)));
        verificar("Hora 8:00 y 19:00 son válidas", new Cita(jose, carlos, manana, LocalTime.of(8, 0)) != null
                && new Cita(jose, carlos, manana, LocalTime.of(19, 0)) != null);
        esperaExcepcion("Fecha anterior a hoy", IllegalArgumentException.class, () ->
                new Cita(jose, carlos, LocalDate.now().minusDays(1), LocalTime.of(9, 0)));
        esperaExcepcion("Correo inválido", IllegalArgumentException.class, () ->
                new Cliente("Ana", "correo-sin-arroba", hash));
        esperaExcepcion("Nombre vacío", IllegalArgumentException.class, () ->
                new Cliente("   ", "ana@correo.com", hash));
        esperaExcepcion("Puntos negativos", IllegalArgumentException.class, () -> jose.setPuntosEstrella(-1));
        esperaExcepcion("Precio negativo", IllegalArgumentException.class, () ->
                new Servicio("Corte", -1.0, null));
        esperaExcepcion("Cupo máximo cero", IllegalArgumentException.class, () ->
                new Barbero("Luis", "luis@barberia.com", hash, 2, 0));
        esperaExcepcion("Franja con fin antes del inicio", IllegalArgumentException.class, () ->
                new Disponibilidad(manana, LocalTime.of(12, 0), LocalTime.of(8, 0)));

        // Pagos
        Pago abono = servicio.registrarAbono(cita1, "Nequi");
        verificar("Pago de abono por 16.875", abono.getMonto() == 16875.00 && Pago.TIPO_ABONO.equals(abono.getTipo()));
        esperaExcepcion("Tipo de pago inválido", IllegalArgumentException.class, () ->
                new Pago(cita1, 1000.0, "Propina", "Efectivo"));
        esperaExcepcion("Monto cero", IllegalArgumentException.class, () ->
                new Pago(cita1, 0.0, Pago.TIPO_SALDO, "Efectivo"));

        // Estados y fidelización
        servicio.completarCita(cita2);
        verificar("Cita atendida", Cita.ESTADO_ATENDIDA.equals(cita2.getEstado()));
        verificar("Atender 2 servicios suma 2 estrellas (2 + 2 = 4)", maria.getPuntosEstrella() == 4);
        esperaExcepcion("No se cambia una cita ya atendida", IllegalStateException.class, cita2::cancelar);

        // Seguridad
        verificar("Clave correcta se verifica", SeguridadUtil.verificar("MiClave2026", hash));
        verificar("Clave incorrecta se rechaza", !SeguridadUtil.verificar("otra", hash));
        verificar("El hash no es la clave", !hash.contains("MiClave2026"));
        verificar("Dos hashes de la misma clave son distintos", !hash.equals(SeguridadUtil.hashear("MiClave2026")));
        verificar("El hash cabe en VARCHAR(255)", hash.length() < 255);
        esperaExcepcion("Clave de menos de 6 caracteres", IllegalArgumentException.class, () ->
                SeguridadUtil.hashear("abc"));

        System.out.println();
        System.out.println("Pruebas ejecutadas: " + total + " | Correctas: " + (total - fallidas)
                + " | Fallidas: " + fallidas);
        if (fallidas > 0) {
            System.exit(1);
        }
    }

    private static void verificar(String descripcion, boolean condicion) {
        total++;
        if (!condicion) {
            fallidas++;
        }
        System.out.println((condicion ? "OK     " : "FALLO  ") + descripcion);
    }

    private static void esperaExcepcion(String descripcion, Class<? extends Exception> tipo, Accion accion) {
        boolean lanzada = false;
        try {
            accion.ejecutar();
        } catch (Exception e) {
            lanzada = tipo.isInstance(e);
        }
        verificar(descripcion + " -> " + tipo.getSimpleName(), lanzada);
    }
}
