package co.edu.sena.cimm.svis.dto;

import co.edu.sena.cimm.svis.model.EstadoEncuesta;

public class CambiarEstadoRequest {
    private EstadoEncuesta estado;

    public CambiarEstadoRequest() {
    }

    public CambiarEstadoRequest(EstadoEncuesta estado) {
        this.estado = estado;
    }

    public EstadoEncuesta getEstado() {
        return estado;
    }

    public void setEstado(EstadoEncuesta estado) {
        this.estado = estado;
    }
}
