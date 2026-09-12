package co.edu.sena.cimm.svis.model;

/**
 * Representa una opción o candidatura dentro de una encuesta.
 * Mapea la tabla 'opciones' (id, encuesta_id, nombre, cantidad_votos).
 * 
 * Cumplimiento estricto de REGLA 4 (Secreto Absoluto del Sufragio):
 * No se almacena ninguna relación con el usuario ni con el token OTP.
 * Únicamente se incrementa el contador numérico de votos (+1).
 */
public class Opcion {
    private Long id;
    private Long encuestaId;
    private String nombre;
    private int cantidadVotos;

    public Opcion() {
    }

    public Opcion(Long id, Long encuestaId, String nombre, int cantidadVotos) {
        this.id = id;
        this.encuestaId = encuestaId;
        this.nombre = nombre;
        this.cantidadVotos = cantidadVotos;
    }

    public Opcion(Long id, Long encuestaId, String nombre, String descripcion, int cantidadVotos) {
        this.id = id;
        this.encuestaId = encuestaId;
        this.nombre = nombre;
        this.cantidadVotos = cantidadVotos;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTitulo() {
        return nombre;
    }

    public void setTitulo(String titulo) {
        this.nombre = titulo;
    }

    public String getDescripcion() {
        return "";
    }

    public void setDescripcion(String descripcion) {
        // Compatibilidad
    }

    public int getCantidadVotos() {
        return cantidadVotos;
    }

    public void setCantidadVotos(int cantidadVotos) {
        this.cantidadVotos = cantidadVotos;
    }

    public int getVotosConteo() {
        return cantidadVotos;
    }

    public void setVotosConteo(int votosConteo) {
        this.cantidadVotos = votosConteo;
    }
}
