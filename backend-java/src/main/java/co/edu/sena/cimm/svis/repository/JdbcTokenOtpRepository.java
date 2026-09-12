package co.edu.sena.cimm.svis.repository;

import co.edu.sena.cimm.svis.dto.TokenView;
import co.edu.sena.cimm.svis.model.EstadoToken;
import co.edu.sena.cimm.svis.model.TokenOtp;
import co.edu.sena.cimm.svis.model.Usuario;
import co.edu.sena.cimm.svis.util.OtpGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcTokenOtpRepository implements TokenOtpRepository {

    @Override
    public int generarPadron(Long encuestaId, List<Usuario> votantes, int ttlMinutos) {
        String sqlVerificar = "SELECT COUNT(*) FROM tokens WHERE encuesta_id = ? AND usuario_id = ?";
        String sqlInsert = "INSERT INTO tokens (encuesta_id, usuario_id, token, estado, fecha_expiracion) "
                + "VALUES (?, ?, ?, 'DISPONIBLE', ?)";

        int creados = 0;
        try (Connection c = Database.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement psVerificar = c.prepareStatement(sqlVerificar);
                 PreparedStatement psInsert = c.prepareStatement(sqlInsert)) {

                LocalDateTime ahora = LocalDateTime.now();
                LocalDateTime expiracion = ahora.plusMinutes(ttlMinutos);

                for (Usuario u : votantes) {
                    psVerificar.setLong(1, encuestaId);
                    psVerificar.setLong(2, u.getId());
                    try (ResultSet rs = psVerificar.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            // Ya tiene token asignado para esta encuesta (REGLA 1 Unicidad)
                            continue;
                        }
                    }

                    String tokenValor = OtpGenerator.generar(encuestaId);
                    psInsert.setLong(1, encuestaId);
                    psInsert.setLong(2, u.getId());
                    psInsert.setString(3, tokenValor);
                    psInsert.setTimestamp(4, Timestamp.valueOf(expiracion));
                    psInsert.executeUpdate();
                    creados++;
                }

                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error generando padrón de tokens OTP", ex);
        }
        return creados;
    }

    @Override
    public Optional<TokenOtp> buscarPorValor(String tokenStr) {
        String sql = "SELECT id, encuesta_id, usuario_id, token, estado, fecha_expiracion, fecha_uso "
                + "FROM tokens WHERE token = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tokenStr != null ? tokenStr.trim() : "");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error consultando token por valor", ex);
        }
        return Optional.empty();
    }

    @Override
    public Optional<TokenOtp> buscarPorEncuestaYUsuario(Long encuestaId, Long usuarioId) {
        String sql = "SELECT id, encuesta_id, usuario_id, token, estado, fecha_expiracion, fecha_uso "
                + "FROM tokens WHERE encuesta_id = ? AND usuario_id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, encuestaId);
            ps.setLong(2, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error consultando token de usuario para la encuesta", ex);
        }
        return Optional.empty();
    }

    @Override
    public List<TokenView> listarPorEncuesta(Long encuestaId) {
        String sql = "SELECT t.id, t.encuesta_id, t.usuario_id, u.correo, u.nombre, "
                + "t.token, t.estado, t.fecha_expiracion, t.fecha_uso "
                + "FROM tokens t "
                + "JOIN usuarios u ON u.id = t.usuario_id "
                + "WHERE t.encuesta_id = ? "
                + "ORDER BY u.nombre ASC";

        List<TokenView> lista = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, encuestaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp fExp = rs.getTimestamp("fecha_expiracion");
                    Timestamp fUso = rs.getTimestamp("fecha_uso");
                    lista.add(new TokenView(
                            rs.getLong("id"),
                            rs.getLong("encuesta_id"),
                            rs.getLong("usuario_id"),
                            rs.getString("correo"),
                            rs.getString("nombre"),
                            rs.getString("token"),
                            EstadoToken.valueOf(rs.getString("estado")),
                            null,
                            fExp != null ? fExp.toLocalDateTime() : null,
                            fUso != null ? fUso.toLocalDateTime() : null
                    ));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error listando tokens de la encuesta", ex);
        }
        return lista;
    }

    @Override
    public int contarPorEncuesta(Long encuestaId) {
        String sql = "SELECT COUNT(*) FROM tokens WHERE encuesta_id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, encuestaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error contando tokens de la encuesta", ex);
        }
        return 0;
    }

    @Override
    public int contarUsadosPorEncuesta(Long encuestaId) {
        String sql = "SELECT COUNT(*) FROM tokens WHERE encuesta_id = ? AND estado = 'USADO'";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, encuestaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error contando tokens usados", ex);
        }
        return 0;
    }

    private TokenOtp map(ResultSet rs) throws SQLException {
        Timestamp fExp = rs.getTimestamp("fecha_expiracion");
        Timestamp fUso = rs.getTimestamp("fecha_uso");
        return new TokenOtp(
                rs.getLong("id"),
                rs.getLong("encuesta_id"),
                rs.getLong("usuario_id"),
                rs.getString("token"),
                EstadoToken.valueOf(rs.getString("estado")),
                null,
                fExp != null ? fExp.toLocalDateTime() : null,
                fUso != null ? fUso.toLocalDateTime() : null
        );
    }
}
