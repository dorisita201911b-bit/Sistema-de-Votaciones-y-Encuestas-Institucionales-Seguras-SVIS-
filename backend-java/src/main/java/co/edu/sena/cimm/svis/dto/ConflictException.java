package co.edu.sena.cimm.svis.dto;

/**
 * Excepción lanzada cuando ocurre una condición de carrera o intento de doble voto con un mismo token OTP.
 * Corresponde a HTTP 409 Conflict conforme a la REGLA 3 del taller.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
