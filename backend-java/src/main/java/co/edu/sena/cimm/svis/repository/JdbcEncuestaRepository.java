package co.edu.sena.cimm.svis.repository;

import co.edu.sena.cimm.svis.dto.OpcionResultado;
import co.edu.sena.cimm.svis.dto.ResultadoEncuestaView;
import co.edu.sena.cimm.svis.model.Encuesta;
import co.edu.sena.cimm.svis.model.EstadoEncuesta;
import co.edu.sena.cimm.svis.model.Opcion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcEncuestaRepository implements EncuestaRepository {

    @Override
    public Encuesta save(Encuesta encuesta) {
        try (Connection c = Database.getConnection()) {
            c.setAutoCommit(false);
            try {
                String sqlEncuesta = "INSERT INTO encuestas (titulo, descripcion, estado, fecha_inicio, fecha_fin, fecha_creacion) "
                        + "VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = c.prepareStatement(sqlEncuesta, Statement.RETURN_GENERATED_KEYS)) {
                    LocalDateTime ahora = LocalDateTime.now();
                    ps.setString(1, encuesta.getTitulo());
                    ps.setString(2, encuesta.getDescripcion());
                    ps.setString(3, encuesta.getEstado() != null ? encuesta.getEstado().name() : EstadoEncuesta.ACTIVA.name());
                    ps.setTimestamp(4, Timestamp.valueOf(encuesta.getFechaInicio() != null ? encuesta.getFechaInicio() : ahora));
                    ps.setTimestamp(5, encuesta.getFechaFin() != null ? Timestamp.valueOf(encuesta.getFechaFin()) : null);
                    ps.setTimestamp(6, Timestamp.valueOf(ahora));
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            encuesta.setId(keys.getLong(1));
                        }
                    }
                }

                if (encuesta.getOpciones() != null && !encuesta.getOpciones().isEmpty()) {
                    String sqlOpcion = "INSERT INTO opciones (encuesta_id, nombre, cantidad_votos) VALUES (?, ?, 0)";
                    try (PreparedStatement psOpc = c.prepareStatement(sqlOpcion, Statement.RETURN_GENERATED_KEYS)) {
                        for (Opcion opc : encuesta.getOpciones()) {
                            psOpc.setLong(1, encuesta.getId());
                            psOpc.setString(2, opc.getNombre() != null ? opc.getNombre() : opc.getTitulo());
                            psOpc.executeUpdate();
                            try (ResultSet keys = psOpc.getGeneratedKeys()) {
                                if (keys.next()) {
                                    opc.setId(keys.getLong(1));
                                    opc.setEncuestaId(encuesta.getId());
                                }
                            }
                        }
                    }
                }

                c.commit();
                return encuesta;
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error creando la encuesta y sus opciones", ex);
        }
    }

    @Override
    public void actualizarEstado(Long encuestaId, EstadoEncuesta estado) {
        String sql = "UPDATE encuestas SET estado = ?, fecha_fin = ? WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, estado.name());
            if (estado == EstadoEncuesta.CERRADA) {
                ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            } else {
                ps.setTimestamp(2, null);
            }
            ps.setLong(3, encuestaId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Error actualizando estado de la encuesta", ex);
        }
    }

    @Override
    public Optional<Encuesta> buscarPorId(Long id) {
        String sql = "SELECT id, titulo, descripcion, estado, fecha_inicio, fecha_fin, fecha_creacion FROM encuestas WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Encuesta e = mapEncuesta(rs);
                    e.setOpciones(cargarOpciones(c, e.getId()));
                    return Optional.of(e);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error consultando la encuesta por id", ex);
        }
        return Optional.empty();
    }

    @Override
    public List<Encuesta> listarTodas() {
        String sql = "SELECT id, titulo, descripcion, estado, fecha_inicio, fecha_fin, fecha_creacion FROM encuestas ORDER BY id DESC";
        List<Encuesta> lista = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Encuesta e = mapEncuesta(rs);
                e.setOpciones(cargarOpciones(c, e.getId()));
                lista.add(e);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error listando encuestas", ex);
        }
        return lista;
    }

    @Override
    public List<Encuesta> listarActivas() {
        String sql = "SELECT id, titulo, descripcion, estado, fecha_inicio, fecha_fin, fecha_creacion FROM encuestas WHERE estado = 'ACTIVA' ORDER BY id DESC";
        List<Encuesta> lista = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Encuesta e = mapEncuesta(rs);
                e.setOpciones(cargarOpciones(c, e.getId()));
                lista.add(e);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error listando encuestas activas", ex);
        }
        return lista;
    }

    @Override
    public ResultadoEncuestaView obtenerResultados(Long encuestaId) {
        Optional<Encuesta> encOpt = buscarPorId(encuestaId);
        if (encOpt.isEmpty()) {
            return null;
        }
        Encuesta enc = encOpt.get();

        String sqlOpciones = "SELECT id, nombre, cantidad_votos FROM opciones WHERE encuesta_id = ? ORDER BY id ASC";
        List<OpcionResultado> resultados = new ArrayList<>();
        int totalVotos = 0;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sqlOpciones)) {
            ps.setLong(1, encuestaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int votos = rs.getInt("cantidad_votos");
                    totalVotos += votos;
                    resultados.add(new OpcionResultado(
                            rs.getLong("id"),
                            rs.getString("nombre"),
                            votos,
                            0.0
                    ));
                }
            }

            // Calcular porcentajes exactos
            for (OpcionResultado opc : resultados) {
                if (totalVotos > 0) {
                    double pct = ((double) opc.getCantidadVotos() / totalVotos) * 100.0;
                    opc.setPorcentaje(Math.round(pct * 10.0) / 10.0);
                } else {
                    opc.setPorcentaje(0.0);
                }
            }

            // Obtener padrón electoral asignado en la tabla tokens
            int totalPadron = 0;
            String sqlPadron = "SELECT COUNT(*) FROM tokens WHERE encuesta_id = ?";
            try (PreparedStatement psPadron = c.prepareStatement(sqlPadron)) {
                psPadron.setLong(1, encuestaId);
                try (ResultSet rsPadron = psPadron.executeQuery()) {
                    if (rsPadron.next()) {
                        totalPadron = rsPadron.getInt(1);
                    }
                }
            }

            double participacion = totalPadron > 0 ? ((double) totalVotos / totalPadron) * 100.0 : 0.0;
            participacion = Math.round(participacion * 10.0) / 10.0;

            return new ResultadoEncuestaView(
                    enc.getId(),
                    enc.getTitulo(),
                    enc.getDescripcion(),
                    enc.getEstado(),
                    enc.getFechaCreacion(),
                    enc.getFechaFin(),
                    totalVotos,
                    totalPadron,
                    participacion,
                    resultados
            );
        } catch (SQLException ex) {
            throw new RuntimeException("Error consultando resultados consolidados", ex);
        }
    }

    private List<Opcion> cargarOpciones(Connection c, Long encuestaId) throws SQLException {
        String sql = "SELECT id, encuesta_id, nombre, cantidad_votos FROM opciones WHERE encuesta_id = ? ORDER BY id ASC";
        List<Opcion> lista = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, encuestaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Opcion(
                            rs.getLong("id"),
                            rs.getLong("encuesta_id"),
                            rs.getString("nombre"),
                            rs.getInt("cantidad_votos")
                    ));
                }
            }
        }
        return lista;
    }

    private Encuesta mapEncuesta(ResultSet rs) throws SQLException {
        Timestamp fInicio = rs.getTimestamp("fecha_inicio");
        Timestamp fFin = rs.getTimestamp("fecha_fin");
        Timestamp fCrea = rs.getTimestamp("fecha_creacion");
        return new Encuesta(
                rs.getLong("id"),
                rs.getString("titulo"),
                rs.getString("descripcion"),
                EstadoEncuesta.valueOf(rs.getString("estado")),
                fInicio != null ? fInicio.toLocalDateTime() : null,
                fFin != null ? fFin.toLocalDateTime() : null,
                fCrea != null ? fCrea.toLocalDateTime() : null
        );
    }
}
