package co.edu.sena.cimm.svis.servlet;

import co.edu.sena.cimm.svis.config.AppContext;
import co.edu.sena.cimm.svis.dto.ApiError;
import co.edu.sena.cimm.svis.dto.ConflictException;
import co.edu.sena.cimm.svis.dto.EmitirVotoRequest;
import co.edu.sena.cimm.svis.dto.NegocioException;
import co.edu.sena.cimm.svis.dto.VotoResponse;
import co.edu.sena.cimm.svis.service.VotacionService;
import co.edu.sena.cimm.svis.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Endpoint Crítico de Sufragio:
 * POST /api/votos/emitir -> Emisión atómica de voto.
 * 
 * Lógica de Evaluación:
 * - 200 OK: Voto emitido, token quemado y comprobante generado.
 * - 409 Conflict: El token ya fue usado / condición de carrera prevenida (REGLA 3).
 * - 400 Bad Request: Datos faltantes o token expirado.
 */
@WebServlet("/api/votos/*")
public class VotoServlet extends BaseApiServlet {

    private final VotacionService votacionService = AppContext.get().getVotacionService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path == null || path.equals("/") || path.equals("/emitir")) {
            try {
                EmitirVotoRequest voteReq = JsonUtil.fromJson(readBody(req), EmitirVotoRequest.class);
                VotoResponse voteResp = votacionService.emitirVoto(voteReq);
                writeJson(resp, HttpServletResponse.SC_OK, voteResp);
            } catch (ConflictException ex) {
                // REGLA 3: Respuesta 409 Conflict obligatoria ante condición de carrera o doble voto
                writeJson(resp, HttpServletResponse.SC_CONFLICT, new ApiError("CONFLICT", ex.getMessage()));
            } catch (NegocioException ex) {
                writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, new ApiError("BAD_REQUEST", ex.getMessage()));
            } catch (Exception ex) {
                writeJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new ApiError("SERVER_ERROR", ex.getMessage()));
            }
        } else {
            writeJson(resp, HttpServletResponse.SC_NOT_FOUND, new ApiError("NOT_FOUND", "Endpoint de votación no encontrado"));
        }
    }
}
