package co.edu.sena.cimm.svis.dto;

public class OpcionResultado {
    private Long id;
    private String nombre;
    private String titulo;
    private String descripcion;
    private int cantidadVotos;
    private int votosConteo;
    private double porcentaje;

    public OpcionResultado() {
    }

    public OpcionResultado(Long id, String nombre, int cantidadVotos, double porcentaje) {
        this.id = id;
        this.nombre = nombre;
        this.titulo = nombre;
        this.descripcion = "";
        this.cantidadVotos = cantidadVotos;
        this.votosConteo = cantidadVotos;
        this.porcentaje = porcentaje;
    }

    public OpcionResultado(Long id, String titulo, String descripcion, int votosConteo, double porcentaje) {
        this.id = id;
        this.nombre = titulo;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.cantidadVotos = votosConteo;
        this.votosConteo = votosConteo;
        this.porcentaje = porcentaje;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre != null ? nombre : titulo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
        this.titulo = nombre;
    }

    public String getTitulo() {
        return titulo != null ? titulo : nombre;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
        this.nombre = titulo;
    }

    public String getDescripcion() {
        return descripcion != null ? descripcion : "";
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidadVotos() {
        return cantidadVotos;
    }

    public void setCantidadVotos(int cantidadVotos) {
        this.cantidadVotos = cantidadVotos;
        this.votosConteo = cantidadVotos;
    }

    public int getVotosConteo() {
        return votosConteo;
    }

    public void setVotosConteo(int votosConteo) {
        this.votosConteo = votosConteo;
        this.cantidadVotos = votosConteo;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }
}
