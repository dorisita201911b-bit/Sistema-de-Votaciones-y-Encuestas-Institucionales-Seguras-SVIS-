package co.edu.sena.cimm.svis.service;

import co.edu.sena.cimm.svis.dto.EmitirVotoRequest;
import co.edu.sena.cimm.svis.dto.NegocioException;
import co.edu.sena.cimm.svis.dto.VotoResponse;
import co.edu.sena.cimm.svis.repository.VotoRepository;

public class VotacionService {

    private final VotoRepository votoRepository;

    public VotacionService(VotoRepository votoRepository) {
        this.votoRepository = votoRepository;
    }

    public VotoResponse emitirVoto(EmitirVotoRequest req) {
        if (req == null) {
            throw new NegocioException("Petición de sufragio vacía.");
        }
        return votoRepository.emitirVoto(req.getToken(), req.getOpcionId());
    }
}
