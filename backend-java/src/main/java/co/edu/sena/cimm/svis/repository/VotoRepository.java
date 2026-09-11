package co.edu.sena.cimm.svis.repository;

import co.edu.sena.cimm.svis.dto.VotoResponse;

public interface VotoRepository {
    VotoResponse emitirVoto(String tokenStr, Long opcionId);
}
