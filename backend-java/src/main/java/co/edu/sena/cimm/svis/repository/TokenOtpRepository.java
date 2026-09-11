package co.edu.sena.cimm.svis.repository;

import co.edu.sena.cimm.svis.dto.TokenView;
import co.edu.sena.cimm.svis.model.TokenOtp;
import co.edu.sena.cimm.svis.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface TokenOtpRepository {
    int generarPadron(Long encuestaId, List<Usuario> votantes, int ttlMinutos);
    Optional<TokenOtp> buscarPorValor(String tokenStr);
    Optional<TokenOtp> buscarPorEncuestaYUsuario(Long encuestaId, Long usuarioId);
    List<TokenView> listarPorEncuesta(Long encuestaId);
    int contarPorEncuesta(Long encuestaId);
    int contarUsadosPorEncuesta(Long encuestaId);
}
