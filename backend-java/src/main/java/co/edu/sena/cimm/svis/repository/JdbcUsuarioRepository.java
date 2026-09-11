package co.edu.sena.cimm.svis.repository;

import co.edu.sena.cimm.svis.model.Rol;
import co.edu.sena.cimm.svis.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcUsuarioRepository implements UsuarioRepository {

    @Override
    public Optional<Usuario> buscarPorDocumento(String documento) {
        String sql = "SELECT id, documento, nombre_completo, email, password, rol, fecha_registro FROM usuario WHERE documento = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, documento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error consultando usuario por documento", ex);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        String sql = "SELECT id, documento, nombre_completo, email, password, rol, fecha_registro FROM usuario WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error consultando usuario por id", ex);
        }
        return Optional.empty();
    }

    @Override
    public List<Usuario> listarVotantes() {
        String sql = "SELECT id, documento, nombre_completo, email, password, rol, fecha_registro FROM usuario WHERE rol = 'VOTANTE' ORDER BY nombre_completo ASC";
        List<Usuario> lista = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error listando votantes", ex);
        }
        return lista;
    }

    @Override
    public int contarVotantes() {
        String sql = "SELECT COUNT(*) FROM usuario WHERE rol = 'VOTANTE'";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error contando votantes", ex);
        }
        return 0;
    }

    private Usuario map(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getLong("id"),
                rs.getString("documento"),
                rs.getString("nombre_completo"),
                rs.getString("email"),
                rs.getString("password"),
                Rol.valueOf(rs.getString("rol")),
                rs.getObject("fecha_registro", LocalDateTime.class)
        );
    }
}
