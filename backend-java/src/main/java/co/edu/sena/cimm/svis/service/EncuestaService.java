package co.edu.sena.cimm.svis.service;

import co.edu.sena.cimm.svis.dto.CrearEncuestaRequest;
import co.edu.sena.cimm.svis.dto.NegocioException;
import co.edu.sena.cimm.svis.dto.ResultadoEncuestaView;
import co.edu.sena.cimm.svis.model.Encuesta;
import co.edu.sena.cimm.svis.model.EstadoEncuesta;
import co.edu.sena.cimm.svis.model.Opcion;
import co.edu.sena.cimm.svis.repository.EncuestaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EncuestaService {

    private final EncuestaRepository encuestaRepository;

    public EncuestaService(EncuestaRepository encuestaRepository) {
        this.encuestaRepository = encuestaRepository;
    }

    public Encuesta crear(CrearEncuestaRequest req) {
        if (req == null || req.getTitulo() == null || req.getTitulo().trim().isEmpty()) {
            throw new NegocioException("El título de la encuesta es obligatorio.");
        }
        if (req.getOpciones() == null || req.getOpciones().size() < 2) {
            throw new NegocioException("Una consulta democrática debe contener al menos dos (2) opciones de elección.");
        }

        Encuesta enc = new Encuesta();
        enc.setTitulo(req.getTitulo().trim());
        enc.setDescripcion(req.getDescripcion() != null ? req.getDescripcion().trim() : "");
        enc.setEstado(EstadoEncuesta.ACTIVA);
        enc.setFechaCreacion(LocalDateTime.now());

        List<Opcion> listaOpciones = new ArrayList<>();
        for (String tituloOpcion : req.getOpciones()) {
            if (tituloOpcion != null && !tituloOpcion.trim().isEmpty()) {
                listaOpciones.add(new Opcion(null, null, tituloOpcion.trim(), "", 0));
            }
        }

        if (listaOpciones.size() < 2) {
            throw new NegocioException("Debe ingresar al menos dos opciones válidas y no vacías.");
        }

        enc.setOpciones(listaOpciones);
        return encuestaRepository.save(enc);
    }

    public void cambiarEstado(Long id, EstadoEncuesta nuevoEstado) {
        if (id == null || id <= 0) {
            throw new NegocioException("Identificador de encuesta no válido.");
        }
        if (nuevoEstado == null) {
            throw new NegocioException("Debe especificar el nuevo estado de la encuesta.");
        }

        encuestaRepository.buscarPorId(id)
                .orElseThrow(() -> new NegocioException("La encuesta solicitada no existe."));

        encuestaRepository.actualizarEstado(id, nuevoEstado);
    }

    public List<Encuesta> listarTodas() {
        return encuestaRepository.listarTodas();
    }

    public List<Encuesta> listarActivas() {
        return encuestaRepository.listarActivas();
    }

    public Encuesta obtenerPorId(Long id) {
        return encuestaRepository.buscarPorId(id)
                .orElseThrow(() -> new NegocioException("Encuesta no encontrada con ID: " + id));
    }

    public ResultadoEncuestaView obtenerResultados(Long id) {
        ResultadoEncuestaView view = encuestaRepository.obtenerResultados(id);
        if (view == null) {
            throw new NegocioException("No se encontraron resultados para la encuesta con ID: " + id);
        }
        return view;
    }
}
