package co.edu.sena.cimm.svis.repository;

import co.edu.sena.cimm.svis.dto.ConflictException;
import co.edu.sena.cimm.svis.dto.NegocioException;
import co.edu.sena.cimm.svis.dto.VotoResponse;
import co.edu.sena.cimm.svis.util.HashUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementación JDBC de {@link VotoRepository} con cumplimiento inexcusable de:
 * 
 * - REGLA 2: Quema Atómica del OTP mediante transacción ACID (setAutoCommit(false), commit, rollback).
 * - REGLA 3: Aislamiento contra Condiciones de Carrera con bloqueo pesimista de fila (SELECT ... FOR UPDATE).
 * - REGLA 4: Secreto Absoluto del Sufragio (incremento numérico +1 sin vincular identidad ni token).
 */
public class JdbcVotoRepository implements VotoRepository {

    @Override
    public VotoResponse emitirVoto(String tokenStr, Long opcionId) {
        if (tokenStr == null || tokenStr.trim().isEmpty()) {
            throw new NegocioException("Debe ingresar un token OTP válido.");
        }
        if (opcionId == null || opcionId <= 0) {
            throw new NegocioException("Debe seleccionar una opción de votación válida.");
        }

        try (Connection c = Database.getConnection()) {
            c.setAutoCommit(false);
            try {
                // 1. REGLA 3 (Aislamiento contra Condiciones de Carrera):
                // Bloqueo pesimista de fila con SELECT ... FOR UPDATE en MySQL InnoDB
                String sqlLockToken = "SELECT id, encuesta_id, estado, fecha_expiracion "
                        + "FROM token_otp WHERE token = ? FOR UPDATE";

                long tokenId;
                long encuestaId;
                String estadoToken;
                Timestamp fechaExp;

                try (PreparedStatement psToken = c.prepareStatement(sqlLockToken)) {
                    psToken.setString(1, tokenStr.trim());
                    try (ResultSet rs = psToken.executeQuery()) {
                        if (!rs.next()) {
                            c.rollback();
                            throw new NegocioException("El token OTP no existe o no es válido para este sistema electoral.");
                        }
                        tokenId = rs.getLong("id");
                        encuestaId = rs.getLong("encuesta_id");
                        estadoToken = rs.getString("estado");
                        fechaExp = rs.getTimestamp("fecha_expiracion");
                    }
                }

                // REGLA 3: Si el token ya fue utilizado, abortar de inmediato con 409 Conflict
                if ("USADO".equalsIgnoreCase(estadoToken)) {
                    c.rollback();
                    throw new ConflictException("Conflicto de concurrencia: El token OTP ya ha sido utilizado previamente.");
                }

                // Validar vigencia TTL del token
                if (fechaExp != null && fechaExp.before(new Timestamp(System.currentTimeMillis()))) {
                    c.rollback();
                    throw new NegocioException("El token OTP ha expirado según su tiempo de vida (TTL).");
                }

                // Validar que la encuesta exista y se encuentre en estado ACTIVA
                String sqlVerificarEncuesta = "SELECT estado FROM encuesta WHERE id = ?";
                try (PreparedStatement psEnc = c.prepareStatement(sqlVerificarEncuesta)) {
                    psEnc.setLong(1, encuestaId);
                    try (ResultSet rsEnc = psEnc.executeQuery()) {
                        if (!rsEnc.next()) {
                            c.rollback();
                            throw new NegocioException("La encuesta vinculada al token no fue encontrada.");
                        }
                        String estadoEnc = rsEnc.getString("estado");
                        if (!"ACTIVA".equalsIgnoreCase(estadoEnc)) {
                            c.rollback();
                            throw new NegocioException("La votación ya no está habilitada (Encuesta CERRADA).");
                        }
                    }
                }

                // Validar que la opción pertenezca legítimamente a esta encuesta
                String sqlVerificarOpcion = "SELECT id FROM opcion WHERE id = ? AND encuesta_id = ?";
                try (PreparedStatement psOpc = c.prepareStatement(sqlVerificarOpcion)) {
                    psOpc.setLong(1, opcionId);
                    psOpc.setLong(2, encuestaId);
                    try (ResultSet rsOpc = psOpc.executeQuery()) {
                        if (!rsOpc.next()) {
                            c.rollback();
                            throw new NegocioException("La opción de voto seleccionada no pertenece a la encuesta activa.");
                        }
                    }
                }

                // 2. REGLA 2: Quema Inmediata del Token OTP en la misma transacción
                String sqlQuemarToken = "UPDATE token_otp SET estado = 'USADO', fecha_uso = NOW() WHERE id = ?";
                try (PreparedStatement psQuemar = c.prepareStatement(sqlQuemarToken)) {
                    psQuemar.setLong(1, tokenId);
                    int filasAfectadas = psQuemar.executeUpdate();
                    if (filasAfectadas != 1) {
                        c.rollback();
                        throw new ConflictException("No fue posible quemar el token OTP.");
                    }
                }

                // 3. REGLA 4: Secreto Absoluto del Sufragio
                // El contador de la opción solo se incrementa numéricamente (+1).
                // En ninguna parte de la tabla se almacena el usuario_id ni el token_id.
                String sqlSumarVoto = "UPDATE opcion SET votos_conteo = votos_conteo + 1 WHERE id = ? AND encuesta_id = ?";
                try (PreparedStatement psSumar = c.prepareStatement(sqlSumarVoto)) {
                    psSumar.setLong(1, opcionId);
                    psSumar.setLong(2, encuestaId);
                    int filasOpcion = psSumar.executeUpdate();
                    if (filasOpcion != 1) {
                        c.rollback();
                        throw new NegocioException("Error incrementando el conteo de la opción seleccionada.");
                    }
                }

                // 4. Comprobante Digital Anónimo: Resumen Hash SHA-256
                String semillaRecibo = encuestaId + ":" + System.currentTimeMillis() + ":" + UUID.randomUUID().toString();
                String hashRecibo = HashUtil.sha256(semillaRecibo);
                LocalDateTime fechaHoraVoto = LocalDateTime.now();

                // Registro de auditoría anónima (NO guarda id del votante ni del candidato)
                String sqlRecibo = "INSERT INTO voto_recibo (encuesta_id, hash_recibo, fecha_voto) VALUES (?, ?, ?)";
                try (PreparedStatement psRecibo = c.prepareStatement(sqlRecibo)) {
                    psRecibo.setLong(1, encuestaId);
                    psRecibo.setString(2, hashRecibo);
                    psRecibo.setTimestamp(3, Timestamp.valueOf(fechaHoraVoto));
                    psRecibo.executeUpdate();
                }

                // Confirmación atómica ACID
                c.commit();

                return new VotoResponse(
                        "Sufragio registrado y contabilizado con éxito. Su token ha sido quemado.",
                        hashRecibo,
                        fechaHoraVoto,
                        encuestaId
                );
            } catch (SQLException ex) {
                c.rollback();
                throw new RuntimeException("Error en la transacción de emisión del voto", ex);
            } catch (Exception ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error de conexión a la base de datos", ex);
        }
    }
}
