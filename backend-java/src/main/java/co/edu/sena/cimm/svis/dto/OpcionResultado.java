package co.edu.sena.cimm.svis.dto;

public class OpcionResultado {
    private Long id;
    private String titulo;
    private String descripcion;
    private int votosConteo;
    private double porcentaje;

    public OpcionResultado() {
    }

    public OpcionResultado(Long id, String titulo, String descripcion, int votosConteo, double porcentaje) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.votosConteo = votosConteo;
        this.porcentaje = porcentaje;
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

    public int getVotosConteo() {
        return votosConteo;
    }

    public void setVotosConteo(int votosConteo) {
        this.votosConteo = votosConteo;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }
}
