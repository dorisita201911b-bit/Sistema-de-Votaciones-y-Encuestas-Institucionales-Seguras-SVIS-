package co.edu.sena.cimm.svis.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Encuesta {
    private Long id;
    private String titulo;
    private String descripcion;
    private EstadoEncuesta estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaCierre;
    private List<Opcion> opciones = new ArrayList<>();

    public Encuesta() {
    }

    public Encuesta(Long id, String titulo, String descripcion, EstadoEncuesta estado, LocalDateTime fechaCreacion, LocalDateTime fechaCierre) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaCierre = fechaCierre;
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

    public List<Opcion> getOpciones() {
        return opciones;
    }

    public void setOpciones(List<Opcion> opciones) {
        this.opciones = opciones;
    }
}
