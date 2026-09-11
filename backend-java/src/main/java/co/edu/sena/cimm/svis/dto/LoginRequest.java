package co.edu.sena.cimm.svis.dto;

public class LoginRequest {
    private String documento;
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String documento, String password) {
        this.documento = documento;
        this.password = password;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
