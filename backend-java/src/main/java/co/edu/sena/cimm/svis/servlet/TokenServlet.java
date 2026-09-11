package co.edu.sena.cimm.svis.servlet;

import co.edu.sena.cimm.svis.config.AppContext;
import co.edu.sena.cimm.svis.dto.ApiError;
import co.edu.sena.cimm.svis.dto.GenerarTokensRequest;
import co.edu.sena.cimm.svis.dto.GenerarTokensResponse;
import co.edu.sena.cimm.svis.dto.NegocioException;
import co.edu.sena.cimm.svis.model.TokenOtp;
import co.edu.sena.cimm.svis.service.TokenService;
import co.edu.sena.cimm.svis.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Endpoints:
 * POST /api/tokens/generar                                  -> [ADMIN] Genera el padrón masivo de OTPs con TTL
 * GET  /api/tokens/encuesta/{id}                            -> [ADMIN] Lista los tokens generados de la encuesta
 * GET  /api/tokens/mi-token?encuesta_id={id}&usuario_id={uid} -> [VOTANTE] Consulta token asignado al usuario
 */
@WebServlet("/api/tokens/*")
public class TokenServlet extends BaseApiServlet {

    private final TokenService tokenService = AppContext.get().getTokenService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String path = req.getPathInfo();
            if (path == null || path.equals("/") || path.equals("/generar")) {
                GenerarTokensRequest r = JsonUtil.fromJson(readBody(req), GenerarTokensRequest.class);
                GenerarTokensResponse res = tokenService.generarPadron(r);
                writeJson(resp, HttpServletResponse.SC_OK, res);
                return;
            }

            writeJson(resp, HttpServletResponse.SC_NOT_FOUND, new ApiError("NOT_FOUND", "Ruta de tokens no encontrada"));

        } catch (NegocioException ex) {
            writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, new ApiError("BAD_REQUEST", ex.getMessage()));
        } catch (Exception ex) {
            writeJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new ApiError("SERVER_ERROR", ex.getMessage()));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String path = req.getPathInfo();
            if (path == null || path.equals("/")) {
                writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, new ApiError("BAD_REQUEST", "Debe especificar una acción sobre tokens"));
                return;
            }

            String cleanPath = path.startsWith("/") ? path.substring(1) : path;

            // GET /api/tokens/encuesta/{id}
            if (cleanPath.startsWith("encuesta/")) {
                String idStr = cleanPath.substring("encuesta/".length());
                Long id = Long.parseLong(idStr);
                writeJson(resp, HttpServletResponse.SC_OK, tokenService.listarTokensEncuesta(id));
                return;
            }

            // GET /api/tokens/mi-token?encuesta_id={id}&usuario_id={uid}
            if (cleanPath.startsWith("mi-token")) {
                String encIdStr = req.getParameter("encuesta_id");
                String usuIdStr = req.getParameter("usuario_id");
                if (encIdStr == null || usuIdStr == null) {
                    writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, new ApiError("BAD_REQUEST", "Parámetros encuesta_id y usuario_id requeridos"));
                    return;
                }
                Long encId = Long.parseLong(encIdStr);
                Long usuId = Long.parseLong(usuIdStr);
                Optional<TokenOtp> tokenOpt = tokenService.obtenerTokenVotante(encId, usuId);
                if (tokenOpt.isPresent()) {
                    writeJson(resp, HttpServletResponse.SC_OK, tokenOpt.get());
                } else {
                    writeJson(resp, HttpServletResponse.SC_NOT_FOUND, new ApiError("NOT_FOUND", "No se encontró token OTP asignado para esta encuesta"));
                }
                return;
            }

            writeJson(resp, HttpServletResponse.SC_NOT_FOUND, new ApiError("NOT_FOUND", "Ruta de consulta de tokens no encontrada"));

        } catch (NumberFormatException ex) {
            writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, new ApiError("BAD_REQUEST", "Parámetros numéricos no válidos"));
        } catch (NegocioException ex) {
            writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, new ApiError("BAD_REQUEST", ex.getMessage()));
        } catch (Exception ex) {
            writeJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new ApiError("SERVER_ERROR", ex.getMessage()));
        }
    }
}
