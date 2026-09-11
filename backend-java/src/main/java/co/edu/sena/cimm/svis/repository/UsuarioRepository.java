package co.edu.sena.cimm.svis.repository;

import co.edu.sena.cimm.svis.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    Optional<Usuario> buscarPorDocumento(String documento);
    Optional<Usuario> buscarPorId(Long id);
    List<Usuario> listarVotantes();
    int contarVotantes();
}
