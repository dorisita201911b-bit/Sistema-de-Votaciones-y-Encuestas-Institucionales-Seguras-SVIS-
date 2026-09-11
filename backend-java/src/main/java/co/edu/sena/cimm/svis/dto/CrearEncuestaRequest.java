package co.edu.sena.cimm.svis.dto;

import java.util.List;

public class CrearEncuestaRequest {
    private String titulo;
    private String descripcion;
    private List<String> opciones;

    public CrearEncuestaRequest() {
    }

    public CrearEncuestaRequest(String titulo, String descripcion, List<String> opciones) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.opciones = opciones;
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

    public List<String> getOpciones() {
        return opciones;
    }

    public void setOpciones(List<String> opciones) {
        this.opciones = opciones;
    }
}
