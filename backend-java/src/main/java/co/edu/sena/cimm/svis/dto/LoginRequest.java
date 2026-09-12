package co.edu.sena.cimm.svis.dto;

public class LoginRequest {
    private String correo;
    private String documento;
    private String usuario;
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String correo, String password) {
        this.correo = correo;
        this.password = password;
    }

    public String getCorreo() {
        return getIdentificador();
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDocumento() {
        return getIdentificador();
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getUsuario() {
        return getIdentificador();
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getIdentificador() {
        if (correo != null && !correo.trim().isEmpty()) {
            return correo.trim();
        }
        if (documento != null && !documento.trim().isEmpty()) {
            return documento.trim();
        }
        if (usuario != null && !usuario.trim().isEmpty()) {
            return usuario.trim();
        }
        return "";
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
