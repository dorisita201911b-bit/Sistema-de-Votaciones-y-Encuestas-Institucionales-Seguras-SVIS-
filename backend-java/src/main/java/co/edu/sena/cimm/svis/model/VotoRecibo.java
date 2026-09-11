package co.edu.sena.cimm.svis.model;

import java.time.LocalDateTime;

/**
 * Representa el comprobante digital y registro de auditoría anónima.
 * 
 * Cumplimiento de REGLA 4: No contiene usuarioId ni opcionId.
 * Garantiza que el sistema audita la emisión del voto sin vulnerar el secreto.
 */
public class VotoRecibo {
    private Long id;
    private Long encuestaId;
    private String hashRecibo;
    private LocalDateTime fechaVoto;

    public VotoRecibo() {
    }

    public VotoRecibo(Long id, Long encuestaId, String hashRecibo, LocalDateTime fechaVoto) {
        this.id = id;
        this.encuestaId = encuestaId;
        this.hashRecibo = hashRecibo;
        this.fechaVoto = fechaVoto;
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
}
