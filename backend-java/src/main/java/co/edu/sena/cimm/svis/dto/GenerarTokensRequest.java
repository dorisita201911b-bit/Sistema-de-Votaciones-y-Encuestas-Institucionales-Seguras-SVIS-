package co.edu.sena.cimm.svis.dto;

public class GenerarTokensRequest {
    private Long encuestaId;
    private int ttlMinutos;

    public GenerarTokensRequest() {
        this.ttlMinutos = 1440; // 24 horas por defecto
    }

    public GenerarTokensRequest(Long encuestaId, int ttlMinutos) {
        this.encuestaId = encuestaId;
        this.ttlMinutos = ttlMinutos;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }

    public void setEncuestaId(Long encuestaId) {
        this.encuestaId = encuestaId;
    }

    public int getTtlMinutos() {
        return ttlMinutos;
    }

    public void setTtlMinutos(int ttlMinutos) {
        this.ttlMinutos = ttlMinutos;
    }
}
