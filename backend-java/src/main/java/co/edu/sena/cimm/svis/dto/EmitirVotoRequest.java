package co.edu.sena.cimm.svis.dto;

public class EmitirVotoRequest {
    private String token;
    private String otp;
    private Long opcionId;
    private Long encuestaId;

    public EmitirVotoRequest() {
    }

    public EmitirVotoRequest(String token, Long opcionId) {
        this.token = token;
        this.opcionId = opcionId;
    }

    public String getToken() {
        if (token != null && !token.trim().isEmpty()) {
            return token.trim();
        }
        if (otp != null && !otp.trim().isEmpty()) {
            return otp.trim();
        }
        return "";
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOtp() {
        return getToken();
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Long getOpcionId() {
        return opcionId;
    }

    public void setOpcionId(Long opcionId) {
        this.opcionId = opcionId;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }

    public void setEncuestaId(Long encuestaId) {
        this.encuestaId = encuestaId;
    }
}
