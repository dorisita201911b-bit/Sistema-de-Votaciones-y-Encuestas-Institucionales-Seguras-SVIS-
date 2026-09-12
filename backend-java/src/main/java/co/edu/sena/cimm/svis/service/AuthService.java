package co.edu.sena.cimm.svis.service;

import co.edu.sena.cimm.svis.dto.LoginRequest;
import co.edu.sena.cimm.svis.dto.LoginResponse;
import co.edu.sena.cimm.svis.dto.NegocioException;
import co.edu.sena.cimm.svis.model.Usuario;
import co.edu.sena.cimm.svis.repository.UsuarioRepository;

public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public LoginResponse autenticar(LoginRequest req) {
        if (req == null || req.getIdentificador() == null || req.getIdentificador().trim().isEmpty()) {
            throw new NegocioException("Debe ingresar su correo institucional o usuario.");
        }
        if (req.getPassword() == null || req.getPassword().trim().isEmpty()) {
            throw new NegocioException("Debe ingresar su contraseña.");
        }

        Usuario usuario = usuarioRepository.buscarPorDocumento(req.getIdentificador().trim())
                .orElseThrow(() -> new NegocioException("Credenciales no válidas en la plataforma institucional."));

        if (!usuario.isActivo()) {
            throw new NegocioException("El usuario se encuentra inactivo.");
        }

        if (!usuario.getPassword().equals(req.getPassword())) {
            throw new NegocioException("Contraseña incorrecta. Por favor intente de nuevo.");
        }

        return new LoginResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol()
        );
    }
}
