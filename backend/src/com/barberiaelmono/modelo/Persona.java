package com.barberiaelmono.modelo;

/**
 * Datos comunes de las personas que inician sesión en el sistema: clientes
 * y barberos.
 *
 * @author Jahulady Salinas Oviedo
 * @version 1.0
 * @since 2026
 */
public class Persona {

    private static final int LONGITUD_MAXIMA_NOMBRE = 45;
    private static final int LONGITUD_MAXIMA_CORREO = 60;

    protected String nombre;
    protected String correo;
    protected String contrasena;

    /**
     * Crea una persona con sus datos de acceso.
     *
     * @param nombre nombre completo (máximo 45 caracteres).
     * @param correo correo electrónico (máximo 60 caracteres).
     * @param contrasena contraseña ya convertida en hash.
     */
    public Persona(String nombre, String correo, String contrasena) {
        setNombre(nombre);
        setCorreo(correo);
        setContrasena(contrasena);
    }

    public String getNombre() {
        return nombre;
    }

    /**
     * Cambia el nombre de la persona.
     *
     * @param nombre nombre completo.
     * @throws IllegalArgumentException si está vacío o supera 45 caracteres.
     */
    public final void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (nombre.trim().length() > LONGITUD_MAXIMA_NOMBRE) {
            throw new IllegalArgumentException("El nombre no puede superar 45 caracteres.");
        }
        this.nombre = nombre.trim();
    }

    public String getCorreo() {
        return correo;
    }

    /**
     * Cambia el correo electrónico.
     *
     * @param correo correo con formato válido.
     * @throws IllegalArgumentException si el formato no es válido o supera 60 caracteres.
     */
    public final void setCorreo(String correo) {
        if (correo == null || !correo.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            throw new IllegalArgumentException("El correo electrónico no es válido.");
        }
        if (correo.length() > LONGITUD_MAXIMA_CORREO) {
            throw new IllegalArgumentException("El correo no puede superar 60 caracteres.");
        }
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    /**
     * Guarda la contraseña. Debe recibir el hash, no la clave en texto plano.
     *
     * @param contrasena hash de la contraseña.
     * @throws IllegalArgumentException si viene vacía.
     */
    public final void setContrasena(String contrasena) {
        if (contrasena == null || contrasena.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        this.contrasena = contrasena;
    }
}
