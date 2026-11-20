package com.barberiaelmono.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Utilidades de seguridad para guardar contraseñas como hash y nunca en
 * texto plano, según el estándar definido para el proyecto.
 *
 * @author Jahulady Salinas Oviedo
 * @version 1.0
 * @since 2026
 */
public final class SeguridadUtil {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACIONES = 65536;
    private static final int LONGITUD_CLAVE_BITS = 256;
    private static final int LONGITUD_SAL_BYTES = 16;
    private static final String SEPARADOR = ":";

    private SeguridadUtil() {
    }

    /**
     * Genera el hash de una contraseña con una sal aleatoria.
     *
     * @param clave contraseña en texto plano.
     * @return texto con formato sal:hash en Base64, de menos de 255 caracteres.
     * @throws IllegalArgumentException si la clave es nula o tiene menos de 6 caracteres.
     */
    public static String hashear(String clave) {
        validarClave(clave);
        byte[] sal = new byte[LONGITUD_SAL_BYTES];
        new SecureRandom().nextBytes(sal);
        byte[] hash = calcular(clave, sal);
        return Base64.getEncoder().encodeToString(sal) + SEPARADOR
                + Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Comprueba si una contraseña coincide con el hash guardado.
     *
     * @param clave contraseña escrita por el usuario.
     * @param almacenado texto sal:hash guardado en la base de datos.
     * @return true si la contraseña es correcta.
     */
    public static boolean verificar(String clave, String almacenado) {
        if (clave == null || almacenado == null || !almacenado.contains(SEPARADOR)) {
            return false;
        }
        String[] partes = almacenado.split(SEPARADOR);
        byte[] sal = Base64.getDecoder().decode(partes[0]);
        byte[] esperado = Base64.getDecoder().decode(partes[1]);
        byte[] obtenido = calcular(clave, sal);
        return MessageDigest.isEqual(esperado, obtenido);
    }

    private static void validarClave(String clave) {
        if (clave == null || clave.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener mínimo 6 caracteres.");
        }
    }

    private static byte[] calcular(String clave, byte[] sal) {
        try {
            PBEKeySpec especificacion = new PBEKeySpec(clave.toCharArray(), sal,
                    ITERACIONES, LONGITUD_CLAVE_BITS);
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(especificacion).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("No se pudo calcular el hash de la contraseña.", e);
        }
    }
}
