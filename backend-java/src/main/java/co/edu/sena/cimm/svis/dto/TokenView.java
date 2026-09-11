package co.edu.sena.cimm.svis.dto;

import co.edu.sena.cimm.svis.model.EstadoToken;

import java.time.LocalDateTime;

public class TokenView {
    private Long id;
    private Long encuestaId;
    private Long usuarioId;
    private String documento;
    private String nombreCompleto;
    private String token;
    private EstadoToken estado;
    private LocalDateTime fechaGeneracion;
    private LocalDateTime fechaExpiracion;
    private LocalDateTime fechaUso;

    public TokenView() {
    }

    public TokenView(Long id, Long encuestaId, Long usuarioId, String documento, String nombreCompleto,
                     String token, EstadoToken estado, LocalDateTime fechaGeneracion,
                     LocalDateTime fechaExpiracion, LocalDateTime fechaUso) {
        this.id = id;
        this.encuestaId = encuestaId;
        this.usuarioId = usuarioId;
        this.documento = documento;
        this.nombreCompleto = nombreCompleto;
        this.token = token;
        this.estado = estado;
        this.fechaGeneracion = fechaGeneracion;
        this.fechaExpiracion = fechaExpiracion;
        this.fechaUso = fechaUso;
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

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public EstadoToken getEstado() {
        return estado;
    }

    public void setEstado(EstadoToken estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public LocalDateTime getFechaUso() {
        return fechaUso;
    }

    public void setFechaUso(LocalDateTime fechaUso) {
        this.fechaUso = fechaUso;
    }
}
