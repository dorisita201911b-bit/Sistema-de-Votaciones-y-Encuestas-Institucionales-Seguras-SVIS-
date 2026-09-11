package co.edu.sena.cimm.svis.servlet;

import co.edu.sena.cimm.svis.config.AppContext;
import co.edu.sena.cimm.svis.dto.ApiError;
import co.edu.sena.cimm.svis.dto.LoginRequest;
import co.edu.sena.cimm.svis.dto.LoginResponse;
import co.edu.sena.cimm.svis.dto.NegocioException;
import co.edu.sena.cimm.svis.service.AuthService;
import co.edu.sena.cimm.svis.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * POST /api/auth/login -> Autenticación para el frontend desacoplado.
 */
@WebServlet("/api/auth/*")
public class AuthServlet extends BaseApiServlet {

    private final AuthService authService = AppContext.get().getAuthService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path == null || path.equals("/login") || path.equals("/")) {
            try {
                LoginRequest loginReq = JsonUtil.fromJson(readBody(req), LoginRequest.class);
                LoginResponse loginResp = authService.autenticar(loginReq);
                writeJson(resp, HttpServletResponse.SC_OK, loginResp);
            } catch (NegocioException ex) {
                writeJson(resp, HttpServletResponse.SC_UNAUTHORIZED, new ApiError("AUTH_ERROR", ex.getMessage()));
            } catch (Exception ex) {
                writeJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new ApiError("SERVER_ERROR", ex.getMessage()));
            }
        } else {
            writeJson(resp, HttpServletResponse.SC_NOT_FOUND, new ApiError("NOT_FOUND", "Ruta de autenticación no encontrada"));
        }
    }
}
