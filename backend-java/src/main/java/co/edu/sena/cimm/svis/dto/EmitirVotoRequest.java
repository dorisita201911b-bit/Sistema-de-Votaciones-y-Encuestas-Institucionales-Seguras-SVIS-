package co.edu.sena.cimm.svis.dto;

public class EmitirVotoRequest {
    private String token;
    private Long opcionId;

    public EmitirVotoRequest() {
    }

    public EmitirVotoRequest(String token, Long opcionId) {
        this.token = token;
        this.opcionId = opcionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getOpcionId() {
        return opcionId;
    }

    public void setOpcionId(Long opcionId) {
        this.opcionId = opcionId;
    }
}
