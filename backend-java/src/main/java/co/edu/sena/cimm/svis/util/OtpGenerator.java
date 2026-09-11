package co.edu.sena.cimm.svis.util;

import java.security.SecureRandom;

public final class OtpGenerator {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private OtpGenerator() {
    }

    /**
     * Genera un token institucional legible y seguro con prefijo, ej: OTP-E1-X9K2M4
     */
    public static String generar(Long encuestaId) {
        StringBuilder sb = new StringBuilder("OTP-E" + encuestaId + "-");
        for (int i = 0; i < 8; i++) {
            int index = RANDOM.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(index));
        }
        return sb.toString();
    }
}
