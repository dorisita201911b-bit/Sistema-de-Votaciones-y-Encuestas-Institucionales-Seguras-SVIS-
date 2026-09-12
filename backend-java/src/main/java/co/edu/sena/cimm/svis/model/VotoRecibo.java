package co.edu.sena.cimm.svis.model;

import java.time.LocalDateTime;

/**
 * Representa el voto y comprobante digital en la tabla 'votos'
 * (id, encuesta_id, opcion_id, fecha_voto, comprobante_hash).
 */
public class VotoRecibo {
    private Long id;
    private Long encuestaId;
    private Long opcionId;
    private LocalDateTime fechaVoto;
    private String comprobanteHash;

    public VotoRecibo() {
    }

    public VotoRecibo(Long id, Long encuestaId, Long opcionId, LocalDateTime fechaVoto, String comprobanteHash) {
        this.id = id;
        this.encuestaId = encuestaId;
        this.opcionId = opcionId;
        this.fechaVoto = fechaVoto;
        this.comprobanteHash = comprobanteHash;
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

    public Long getOpcionId() {
        return opcionId;
    }

    public void setOpcionId(Long opcionId) {
        this.opcionId = opcionId;
    }

    public LocalDateTime getFechaVoto() {
        return fechaVoto;
    }

    public void setFechaVoto(LocalDateTime fechaVoto) {
        this.fechaVoto = fechaVoto;
    }

    public String getComprobanteHash() {
        return comprobanteHash;
    }

    public void setComprobanteHash(String comprobanteHash) {
        this.comprobanteHash = comprobanteHash;
    }

    public String getHashRecibo() {
        return comprobanteHash;
    }

    public void setHashRecibo(String hashRecibo) {
        this.comprobanteHash = hashRecibo;
    }
}
