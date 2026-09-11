package co.edu.sena.cimm.svis.dto;

public class GenerarTokensResponse {
    private Long encuestaId;
    private int tokensGenerados;
    private int totalPadron;
    private int ttlMinutos;
    private String mensaje;

    public GenerarTokensResponse() {
    }

    public GenerarTokensResponse(Long encuestaId, int tokensGenerados, int totalPadron, int ttlMinutos, String mensaje) {
        this.encuestaId = encuestaId;
        this.tokensGenerados = tokensGenerados;
        this.totalPadron = totalPadron;
        this.ttlMinutos = ttlMinutos;
        this.mensaje = mensaje;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }

    public void setEncuestaId(Long encuestaId) {
        this.encuestaId = encuestaId;
    }

    public int getTokensGenerados() {
        return tokensGenerados;
    }

    public void setTokensGenerados(int tokensGenerados) {
        this.tokensGenerados = tokensGenerados;
    }

    public int getTotalPadron() {
        return totalPadron;
    }

    public void setTotalPadron(int totalPadron) {
        this.totalPadron = totalPadron;
    }

    public int getTtlMinutos() {
        return ttlMinutos;
    }

    public void setTtlMinutos(int ttlMinutos) {
        this.ttlMinutos = ttlMinutos;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
