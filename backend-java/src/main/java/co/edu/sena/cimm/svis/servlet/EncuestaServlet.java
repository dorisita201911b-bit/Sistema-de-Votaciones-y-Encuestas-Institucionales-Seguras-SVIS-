package co.edu.sena.cimm.svis.servlet;

import co.edu.sena.cimm.svis.config.AppContext;
import co.edu.sena.cimm.svis.dto.ApiError;
import co.edu.sena.cimm.svis.dto.CambiarEstadoRequest;
import co.edu.sena.cimm.svis.dto.CrearEncuestaRequest;
import co.edu.sena.cimm.svis.dto.NegocioException;
import co.edu.sena.cimm.svis.model.Encuesta;
import co.edu.sena.cimm.svis.service.EncuestaService;
import co.edu.sena.cimm.svis.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Endpoints:
 * GET  /api/encuestas/activas           -> [VOTANTE] Retorna encuestas habilitadas
 * GET  /api/encuestas                   -> [ADMIN] Retorna todas las encuestas
 * GET  /api/encuestas/{id}              -> [DETALLE] Retorna una encuesta por ID
 * GET  /api/encuestas/{id}/resultados   -> [ADMIN] Retorna el consolidado de votos y porcentajes
 * POST /api/encuestas                   -> [ADMIN] Crea encuesta y opciones asociadas
 * POST /api/encuestas/{id}/estado        -> [ADMIN] Cambia estado (ACTIVA / CERRADA)
 */
@WebServlet("/api/encuestas/*")
public class EncuestaServlet extends BaseApiServlet {

    private final EncuestaService service = AppContext.get().getEncuestaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String path = req.getPathInfo();
            if (path == null || path.equals("/")) {
                writeJson(resp, HttpServletResponse.SC_OK, service.listarTodas());
                return;
            }

            String cleanPath = path.startsWith("/") ? path.substring(1) : path;

            if ("activas".equalsIgnoreCase(cleanPath)) {
                writeJson(resp, HttpServletResponse.SC_OK, service.listarActivas());
                return;
            }

            // Formato: /{id}/resultados
            if (cleanPath.contains("/resultados")) {
                String idStr = cleanPath.substring(0, cleanPath.indexOf("/resultados"));
                Long id = Long.parseLong(idStr);
                writeJson(resp, HttpServletResponse.SC_OK, service.obtenerResultados(id));
                return;
            }

            // Formato: /{id}
            Long id = Long.parseLong(cleanPath);
            writeJson(resp, HttpServletResponse.SC_OK, service.obtenerPorId(id));

        } catch (NumberFormatException ex) {
            writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, new ApiError("BAD_REQUEST", "Identificador de encuesta no válido"));
        } catch (NegocioException ex) {
            writeJson(resp, HttpServletResponse.SC_NOT_FOUND, new ApiError("NOT_FOUND", ex.getMessage()));
        } catch (Exception ex) {
            writeJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new ApiError("SERVER_ERROR", ex.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String path = req.getPathInfo();

            // POST /api/encuestas -> Crear encuesta
            if (path == null || path.equals("/")) {
                CrearEncuestaRequest r = JsonUtil.fromJson(readBody(req), CrearEncuestaRequest.class);
                Encuesta creada = service.crear(r);
                writeJson(resp, HttpServletResponse.SC_CREATED, creada);
                return;
            }

            String cleanPath = path.startsWith("/") ? path.substring(1) : path;

            // POST /api/encuestas/{id}/estado -> Cambiar estado
            if (cleanPath.contains("/estado")) {
                String idStr = cleanPath.substring(0, cleanPath.indexOf("/estado"));
                Long id = Long.parseLong(idStr);
                CambiarEstadoRequest estReq = JsonUtil.fromJson(readBody(req), CambiarEstadoRequest.class);
                service.cambiarEstado(id, estReq.getEstado());
                writeJson(resp, HttpServletResponse.SC_OK, java.util.Map.of(
                        "mensaje", "Estado de la encuesta actualizado correctamente",
                        "nuevoEstado", estReq.getEstado()
                ));
                return;
            }

            writeJson(resp, HttpServletResponse.SC_NOT_FOUND, new ApiError("NOT_FOUND", "Recurso no encontrado"));

        } catch (NumberFormatException ex) {
            writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, new ApiError("BAD_REQUEST", "Identificador de encuesta no válido"));
        } catch (NegocioException ex) {
            writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, new ApiError("BAD_REQUEST", ex.getMessage()));
        } catch (Exception ex) {
            writeJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new ApiError("SERVER_ERROR", ex.getMessage()));
        }
    }
}
