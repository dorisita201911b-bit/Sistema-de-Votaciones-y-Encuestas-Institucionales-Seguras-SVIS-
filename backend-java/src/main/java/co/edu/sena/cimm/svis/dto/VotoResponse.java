package co.edu.sena.cimm.svis.dto;

import java.time.LocalDateTime;

public class VotoResponse {
    private String mensaje;
    private String hashRecibo;
    private LocalDateTime fechaVoto;
    private Long encuestaId;

    public VotoResponse() {
    }

    public VotoResponse(String mensaje, String hashRecibo, LocalDateTime fechaVoto, Long encuestaId) {
        this.mensaje = mensaje;
        this.hashRecibo = hashRecibo;
        this.fechaVoto = fechaVoto;
        this.encuestaId = encuestaId;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getHashRecibo() {
        return hashRecibo;
    }

    public void setHashRecibo(String hashRecibo) {
        this.hashRecibo = hashRecibo;
    }

    public LocalDateTime getFechaVoto() {
        return fechaVoto;
    }

    public void setFechaVoto(LocalDateTime fechaVoto) {
        this.fechaVoto = fechaVoto;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }

    public void setEncuestaId(Long encuestaId) {
        this.encuestaId = encuestaId;
    }
}
