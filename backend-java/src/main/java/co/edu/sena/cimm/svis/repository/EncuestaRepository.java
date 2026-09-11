package co.edu.sena.cimm.svis.repository;

import co.edu.sena.cimm.svis.dto.ResultadoEncuestaView;
import co.edu.sena.cimm.svis.model.Encuesta;
import co.edu.sena.cimm.svis.model.EstadoEncuesta;

import java.util.List;
import java.util.Optional;

public interface EncuestaRepository {
    Encuesta save(Encuesta encuesta);
    void actualizarEstado(Long encuestaId, EstadoEncuesta estado);
    Optional<Encuesta> buscarPorId(Long id);
    List<Encuesta> listarTodas();
    List<Encuesta> listarActivas();
    ResultadoEncuestaView obtenerResultados(Long encuestaId);
}
