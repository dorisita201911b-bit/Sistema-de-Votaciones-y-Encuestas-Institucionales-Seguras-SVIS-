package co.edu.sena.cimm.svis.dto;

import co.edu.sena.cimm.svis.model.Rol;

public class LoginResponse {
    private Long id;
    private String documento;
    private String nombreCompleto;
    private String email;
    private Rol rol;

    public LoginResponse() {
    }

    public LoginResponse(Long id, String documento, String nombreCompleto, String email, Rol rol) {
        this.id = id;
        this.documento = documento;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
