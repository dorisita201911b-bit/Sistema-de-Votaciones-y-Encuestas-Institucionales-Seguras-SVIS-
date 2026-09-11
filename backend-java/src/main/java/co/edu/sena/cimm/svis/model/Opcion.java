package co.edu.sena.cimm.svis.model;

/**
 * Representa una opción o candidatura dentro de una encuesta.
 * 
 * Cumplimiento estricto de REGLA 4 (Secreto Absoluto del Sufragio):
 * No se almacena ninguna relación con el usuario ni con el token OTP.
 * Únicamente se incrementa el contador de votos (+1).
 */
public class Opcion {
    private Long id;
    private Long encuestaId;
    private String titulo;
    private String descripcion;
    private int votosConteo;

    public Opcion() {
    }

    public Opcion(Long id, Long encuestaId, String titulo, String descripcion, int votosConteo) {
        this.id = id;
        this.encuestaId = encuestaId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.votosConteo = votosConteo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }

    public void setEncuestaId(Long encuestaId) {
        this.encuestaId = encuestaId;
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
}
