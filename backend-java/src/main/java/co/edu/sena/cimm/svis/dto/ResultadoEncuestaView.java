package co.edu.sena.cimm.svis.dto;

import co.edu.sena.cimm.svis.model.EstadoEncuesta;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ResultadoEncuestaView {
    private Long id;
    private String titulo;
    private String descripcion;
    private EstadoEncuesta estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaCierre;
    private int totalVotos;
    private int totalPadron;
    private double participacionPorcentaje;
    private List<OpcionResultado> opciones = new ArrayList<>();

    public ResultadoEncuestaView() {
    }

    public ResultadoEncuestaView(Long id, String titulo, String descripcion, EstadoEncuesta estado,
                                 LocalDateTime fechaCreacion, LocalDateTime fechaCierre,
                                 int totalVotos, int totalPadron, double participacionPorcentaje,
                                 List<OpcionResultado> opciones) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaCierre = fechaCierre;
        this.totalVotos = totalVotos;
        this.totalPadron = totalPadron;
        this.participacionPorcentaje = participacionPorcentaje;
        this.opciones = opciones;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EstadoEncuesta getEstado() {
        return estado;
    }

    public void setEstado(EstadoEncuesta estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public int getTotalVotos() {
        return totalVotos;
    }

    public void setTotalVotos(int totalVotos) {
        this.totalVotos = totalVotos;
    }

    public int getTotalPadron() {
        return totalPadron;
    }

    public void setTotalPadron(int totalPadron) {
        this.totalPadron = totalPadron;
    }

    public double getParticipacionPorcentaje() {
        return participacionPorcentaje;
    }

    public void setParticipacionPorcentaje(double participacionPorcentaje) {
        this.participacionPorcentaje = participacionPorcentaje;
    }

    public List<OpcionResultado> getOpciones() {
        return opciones;
    }

    public void setOpciones(List<OpcionResultado> opciones) {
        this.opciones = opciones;
    }
}
