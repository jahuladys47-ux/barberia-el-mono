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
import java.util.Locale;

/**
 * Demostración por consola de los módulos del sistema de la Barbería El Mono.
 * No necesita base de datos: usa los mismos datos de ejemplo de la base de datos
 * barberia_el_mono para mostrar las reglas de negocio.
 *
 * @author Jahulady Salinas Oviedo
 * @version 1.0
 * @since 2026
 */
public class Main {

    private static final Locale COLOMBIA = Locale.forLanguageTag("es-CO");

    /**
     * Punto de entrada de la demostración.
     *
     * @param args no se usan.
     */
    public static void main(String[] args) {
        AgendamientoService agendamiento = new AgendamientoService();
        LocalDate manana = LocalDate.now().plusDays(1);

        Servicio corteClasico = new Servicio("Corte clásico", 20000.00, "Corte tradicional con tijera y máquina");
        Servicio corteDegradado = new Servicio("Corte degradado", 25000.00, "Degradado con acabado a navaja");
        Servicio barba = new Servicio("Perfilado de barba", 15000.00, "Perfilado y arreglo de barba");
        Servicio cejas = new Servicio("Perfilado de cejas", 8000.00, "Arreglo de cejas con navaja");
        Servicio lavado = new Servicio("Lavado", 10000.00, "Lavado y masaje capilar");

        titulo("MÓDULO DE SERVICIOS: catálogo");
        for (Servicio servicio : new Servicio[] {corteClasico, corteDegradado, barba, cejas, lavado}) {
            System.out.println(String.format("- %-20s %s", servicio.getNombre(), moneda(servicio.getPrecio())));
        }

        titulo("MÓDULO DE CLIENTE: contraseña con hash");
        String hash = SeguridadUtil.hashear("MiClave2026");
        System.out.println("Hash guardado (no la clave): " + hash.substring(0, 22) + "...");
        System.out.println("Clave correcta verificada: " + SeguridadUtil.verificar("MiClave2026", hash));
        System.out.println("Clave incorrecta verificada: " + SeguridadUtil.verificar("otraClave", hash));

        Cliente jose = new Cliente("José Peña", "jose.pena@correo.com", hash);
        jose.setPuntosEstrella(5);
        Cliente maria = new Cliente("María Fernanda Gómez", "maria.gomez@correo.com", hash);
        maria.setPuntosEstrella(2);

        titulo("MÓDULO DE BARBERO: disponibilidad y cupo");
        Barbero carlos = new Barbero("Carlos \"El Mono\" Ruiz", "carlos.ruiz@barberia.com", hash, 1, 8);
        carlos.registrarDisponibilidad(new Disponibilidad(manana, LocalTime.of(8, 0), LocalTime.of(12, 0)));
        System.out.println(carlos.getNombre() + " atiende mañana de 8:00 a. m. a 12:00 m. (cupo máximo: "
                + carlos.getCupoMaximo() + ")");
        System.out.println("¿Disponible a las 9:00? " + carlos.estaDisponible(manana, LocalTime.of(9, 0)));
        System.out.println("¿Disponible a las 3:00 p. m.? " + carlos.estaDisponible(manana, LocalTime.of(15, 0)));

        titulo("MÓDULO DE AGENDAMIENTO: cliente fijo (5 estrellas)");
        Cita citaJose = new Cita(jose, carlos, manana, LocalTime.of(9, 0));
        citaJose.agregarServicio(corteDegradado);
        citaJose.agregarServicio(barba);
        confirmar(agendamiento, citaJose, 0);
        mostrar(citaJose);

        titulo("MÓDULO DE AGENDAMIENTO: cliente sin descuento");
        Cita citaMaria = new Cita(maria, carlos, manana, LocalTime.of(10, 0));
        citaMaria.agregarServicio(corteClasico);
        citaMaria.agregarServicio(lavado);
        confirmar(agendamiento, citaMaria, 1);
        mostrar(citaMaria);

        titulo("MÓDULO DE PAGOS: abono del 50 %");
        Pago abono = agendamiento.registrarAbono(citaJose, "Nequi");
        System.out.println("Pago registrado: " + abono.getTipo() + " de " + moneda(abono.getMonto())
                + " por " + abono.getMetodoPago());

        titulo("MÓDULO DE FIDELIZACIÓN: estrellas al atender");
        System.out.println(maria.getNombre() + " tiene " + maria.getPuntosEstrella() + " estrellas.");
        agendamiento.completarCita(citaMaria);
        System.out.println("Después de atender 2 servicios tiene " + maria.getPuntosEstrella()
                + " estrellas. ¿Cliente fijo? " + maria.isEsClienteFijo());

        titulo("VALIDACIONES");
        probar("Hora 7:00 a. m.", () -> new Cita(jose, carlos, manana, LocalTime.of(7, 0)));
        probar("Fecha de ayer", () -> new Cita(jose, carlos, LocalDate.now().minusDays(1), LocalTime.of(9, 0)));
        probar("Correo inválido", () -> new Cliente("Ana", "correo-sin-arroba", hash));
        Cita fueraDeHorario = new Cita(jose, carlos, manana, LocalTime.of(15, 0));
        fueraDeHorario.agregarServicio(corteClasico);
        try {
            agendamiento.agendar(fueraDeHorario, 0);
        } catch (CitaNoDisponibleException e) {
            System.out.println("Barbero sin franja a las 3:00 p. m.: " + e.getMessage());
        }
    }

    private static void confirmar(AgendamientoService agendamiento, Cita cita, int citasDelDia) {
        try {
            agendamiento.agendar(cita, citasDelDia);
        } catch (CitaNoDisponibleException e) {
            System.out.println("No se pudo agendar: " + e.getMessage());
        }
    }

    private static void mostrar(Cita cita) {
        System.out.println("Cliente: " + cita.getCliente().getNombre() + " | Barbero: "
                + cita.getBarbero().getNombre() + " | Turno: " + cita.getNumeroTurno()
                + " | Estado: " + cita.getEstado());
        for (Servicio servicio : cita.getServicios()) {
            System.out.println("  - " + servicio.getNombre() + ": " + moneda(servicio.getPrecio()));
        }
        System.out.println("  Total:      " + moneda(cita.getTotalPagar()));
        System.out.println("  Descuento:  " + moneda(cita.getDescuento()));
        System.out.println("  Abono 50 %: " + moneda(cita.getAbono()));
        System.out.println("  Saldo:      " + moneda(cita.getSaldo()));
    }

    private static void probar(String descripcion, Runnable accion) {
        try {
            accion.run();
            System.out.println(descripcion + ": se aceptó (revisar)");
        } catch (IllegalArgumentException e) {
            System.out.println(descripcion + " -> rechazado: " + e.getMessage());
        }
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("=== " + texto + " ===");
    }

    private static String moneda(double valor) {
        return String.format(COLOMBIA, "$%,.0f", valor);
    }
}
