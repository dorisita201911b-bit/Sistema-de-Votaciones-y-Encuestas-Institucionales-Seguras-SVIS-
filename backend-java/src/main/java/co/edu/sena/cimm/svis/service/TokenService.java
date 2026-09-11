package co.edu.sena.cimm.svis.service;

import co.edu.sena.cimm.svis.dto.GenerarTokensRequest;
import co.edu.sena.cimm.svis.dto.GenerarTokensResponse;
import co.edu.sena.cimm.svis.dto.NegocioException;
import co.edu.sena.cimm.svis.dto.TokenView;
import co.edu.sena.cimm.svis.model.Encuesta;
import co.edu.sena.cimm.svis.model.TokenOtp;
import co.edu.sena.cimm.svis.model.Usuario;
import co.edu.sena.cimm.svis.repository.EncuestaRepository;
import co.edu.sena.cimm.svis.repository.TokenOtpRepository;
import co.edu.sena.cimm.svis.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

public class TokenService {

    private final TokenOtpRepository tokenOtpRepository;
    private final UsuarioRepository usuarioRepository;
    private final EncuestaRepository encuestaRepository;

    public TokenService(TokenOtpRepository tokenOtpRepository, UsuarioRepository usuarioRepository, EncuestaRepository encuestaRepository) {
        this.tokenOtpRepository = tokenOtpRepository;
        this.usuarioRepository = usuarioRepository;
        this.encuestaRepository = encuestaRepository;
    }

    public GenerarTokensResponse generarPadron(GenerarTokensRequest req) {
        if (req == null || req.getEncuestaId() == null || req.getEncuestaId() <= 0) {
            throw new NegocioException("Debe indicar el ID de la encuesta.");
        }
        int ttl = req.getTtlMinutos() > 0 ? req.getTtlMinutos() : 1440; // 24h default

        Encuesta enc = encuestaRepository.buscarPorId(req.getEncuestaId())
                .orElseThrow(() -> new NegocioException("La encuesta indicada no existe."));

        List<Usuario> votantes = usuarioRepository.listarVotantes();
        if (votantes.isEmpty()) {
            throw new NegocioException("No se encuentran aprendices registrados en el padrón electoral.");
        }

        int nuevosTokens = tokenOtpRepository.generarPadron(enc.getId(), votantes, ttl);
        int totalPadron = tokenOtpRepository.contarPorEncuesta(enc.getId());

        String mensaje = nuevosTokens > 0
                ? "Se generaron exitosamente " + nuevosTokens + " tokens OTP para el padrón electoral."
                : "El padrón electoral ya contaba con credenciales OTP generadas para esta encuesta.";

        return new GenerarTokensResponse(
                enc.getId(),
                nuevosTokens,
                totalPadron,
                ttl,
                mensaje
        );
    }

    public List<TokenView> listarTokensEncuesta(Long encuestaId) {
        if (encuestaId == null || encuestaId <= 0) {
            throw new NegocioException("Identificador de encuesta no válido.");
        }
        return tokenOtpRepository.listarPorEncuesta(encuestaId);
    }

    public Optional<TokenOtp> obtenerTokenVotante(Long encuestaId, Long usuarioId) {
        if (encuestaId == null || usuarioId == null) {
            return Optional.empty();
        }
        return tokenOtpRepository.buscarPorEncuestaYUsuario(encuestaId, usuarioId);
    }

    public Optional<TokenOtp> buscarPorValor(String tokenStr) {
        if (tokenStr == null || tokenStr.trim().isEmpty()) {
            return Optional.empty();
        }
        return tokenOtpRepository.buscarPorValor(tokenStr.trim());
    }
}
