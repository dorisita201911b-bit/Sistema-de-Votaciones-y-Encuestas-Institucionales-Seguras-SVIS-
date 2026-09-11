package co.edu.sena.cimm.svis.config;

import co.edu.sena.cimm.svis.repository.EncuestaRepository;
import co.edu.sena.cimm.svis.repository.JdbcEncuestaRepository;
import co.edu.sena.cimm.svis.repository.JdbcTokenOtpRepository;
import co.edu.sena.cimm.svis.repository.JdbcUsuarioRepository;
import co.edu.sena.cimm.svis.repository.JdbcVotoRepository;
import co.edu.sena.cimm.svis.repository.TokenOtpRepository;
import co.edu.sena.cimm.svis.repository.UsuarioRepository;
import co.edu.sena.cimm.svis.repository.VotoRepository;
import co.edu.sena.cimm.svis.service.AuthService;
import co.edu.sena.cimm.svis.service.EncuestaService;
import co.edu.sena.cimm.svis.service.TokenService;
import co.edu.sena.cimm.svis.service.VotacionService;

/**
 * Contenedor Singleton de dependencias del backend SVIS.
 */
public final class AppContext {

    private static final AppContext INSTANCE = new AppContext();

    private final UsuarioRepository usuarioRepository;
    private final EncuestaRepository encuestaRepository;
    private final TokenOtpRepository tokenOtpRepository;
    private final VotoRepository votoRepository;

    private final AuthService authService;
    private final EncuestaService encuestaService;
    private final TokenService tokenService;
    private final VotacionService votacionService;

    private AppContext() {
        this.usuarioRepository = new JdbcUsuarioRepository();
        this.encuestaRepository = new JdbcEncuestaRepository();
        this.tokenOtpRepository = new JdbcTokenOtpRepository();
        this.votoRepository = new JdbcVotoRepository();

        this.authService = new AuthService(this.usuarioRepository);
        this.encuestaService = new EncuestaService(this.encuestaRepository);
        this.tokenService = new TokenService(this.tokenOtpRepository, this.usuarioRepository, this.encuestaRepository);
        this.votacionService = new VotacionService(this.votoRepository);

        System.out.println("[SVIS Backend] Inicializado correctamente con motor MySQL InnoDB.");
    }

    public static AppContext get() {
        return INSTANCE;
    }

    public UsuarioRepository getUsuarioRepository() {
        return usuarioRepository;
    }

    public EncuestaRepository getEncuestaRepository() {
        return encuestaRepository;
    }

    public TokenOtpRepository getTokenOtpRepository() {
        return tokenOtpRepository;
    }

    public VotoRepository getVotoRepository() {
        return votoRepository;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public EncuestaService getEncuestaService() {
        return encuestaService;
    }

    public TokenService getTokenService() {
        return tokenService;
    }

    public VotacionService getVotacionService() {
        return votacionService;
    }
}
