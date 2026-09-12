package co.edu.sena.cimm.svis.dto;

import co.edu.sena.cimm.svis.model.Rol;

public class LoginResponse {
    private Long id;
    private String nombre;
    private String correo;
    private String documento;
    private String nombreCompleto;
    private String email;
    private Rol rol;

    public LoginResponse() {
    }

    public LoginResponse(Long id, String nombre, String correo, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.nombreCompleto = nombre;
        this.documento = correo;
        this.email = correo;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
        this.nombreCompleto = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
        this.email = correo;
        this.documento = correo;
    }

    public String getDocumento() {
        return documento != null ? documento : correo;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombreCompleto() {
        return nombreCompleto != null ? nombreCompleto : nombre;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email != null ? email : correo;
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
