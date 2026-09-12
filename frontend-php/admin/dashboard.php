<?php
require_once __DIR__ . '/../config.php';
require_once __DIR__ . '/../includes/ApiClient.php';
require_once __DIR__ . '/../includes/auth.php';

require_admin();
$adminPage = true;
$titulo = 'Panel de Control Electoral';
$msg = null;
$err = null;
$detalleResultados = null;
$detalleTokens = null;
$encuestaSeleccionada = null;

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $accion = $_POST['accion'] ?? '';
    $encuestaId = (int) ($_POST['encuestaId'] ?? 0);

    // 1. Crear nueva encuesta y opciones
    if ($accion === 'crear') {
        $tituloEnc = trim($_POST['titulo'] ?? '');
        $descripcion = trim($_POST['descripcion'] ?? '');
        $opciones = array_values(array_filter(array_map('trim', $_POST['opciones'] ?? [])));

        if ($tituloEnc === '') {
            $err = 'El título de la consulta institucional es obligatorio.';
        } elseif (count($opciones) < 2) {
            $err = 'Una consulta democrática debe contener al menos dos (2) candidatos u opciones.';
        } else {
            $res = api()->post('/encuestas', [
                'titulo' => $tituloEnc,
                'descripcion' => $descripcion,
                'opciones' => $opciones,
            ]);

            if ($res['ok']) {
                $msg = '¡Encuesta electoral "' . h($tituloEnc) . '" creada con éxito con ' . count($opciones) . ' opciones!';
            } else {
                $err = ($res['data']['mensaje'] ?? null) ?: ($res['error'] ?? 'No fue posible crear la encuesta.');
            }
        }
    }
    // 2. Generar Padrón y Tokens OTP masivos con TTL
    elseif ($accion === 'generar') {
        $ttlMinutos = (int) ($_POST['ttlMinutos'] ?? 1440);
        $res = api()->post('/tokens/generar', [
            'encuestaId' => $encuestaId,
            'ttlMinutos' => $ttlMinutos,
        ]);

        if ($res['ok']) {
            $data = $res['data'] ?? [];
            $msg = ($data['mensaje'] ?? 'Padrón de tokens OTP generado.')
                . ' (Tokens nuevos: ' . ($data['tokensGenerados'] ?? 0) . ' | Total Padrón: ' . ($data['totalPadron'] ?? 0) . ')';
        } else {
            $err = ($res['data']['mensaje'] ?? null) ?: ($res['error'] ?? 'Error generando padrón de tokens.');
        }
    }
    // 3. Apertura / Cierre de encuesta
    elseif ($accion === 'estado') {
        $nuevoEstado = strtoupper(trim($_POST['estado'] ?? 'CERRADA'));
        $res = api()->post('/encuestas/' . $encuestaId . '/estado', [
            'estado' => $nuevoEstado,
        ]);

        if ($res['ok']) {
            $msg = 'Estado de la encuesta #' . $encuestaId . ' actualizado a: ' . $nuevoEstado . '.';
        } else {
            $err = ($res['data']['mensaje'] ?? null) ?: ($res['error'] ?? 'Error actualizando estado.');
        }
    }
    // 4. Escrutinio en tiempo real
    elseif ($accion === 'resultados') {
        $res = api()->get('/encuestas/' . $encuestaId . '/resultados');
        if ($res['ok']) {
            $detalleResultados = $res['data'] ?? [];
            $encuestaSeleccionada = $encuestaId;
        } else {
            $err = ($res['data']['mensaje'] ?? null) ?: ($res['error'] ?? 'No se pudieron consultar los resultados.');
        }
    }
    // 5. Ver padrón de credenciales
    elseif ($accion === 'ver_tokens') {
        $res = api()->get('/tokens/encuesta/' . $encuestaId);
        if ($res['ok']) {
            $detalleTokens = $res['data'] ?? [];
            $encuestaSeleccionada = $encuestaId;
        } else {
            $err = ($res['data']['mensaje'] ?? null) ?: ($res['error'] ?? 'No se pudo consultar el padrón de tokens.');
        }
    }
}

// Cargar todas las encuestas
$listaRes = api()->get('/encuestas');
$encuestas = $listaRes['ok'] ? ($listaRes['data']['encuestas'] ?? $listaRes['data'] ?? []) : [];
if (!$listaRes['ok'] && !$err) {
    $err = $listaRes['error'] ?? 'No se pudo consultar el listado de encuestas.';
}

// Métricas acumuladas para KPIs
$totalEncuestas = count($encuestas);
$activasCount = 0;
foreach ($encuestas as $e) {
    if (strtoupper($e['estado'] ?? '') === 'ACTIVA') {
        $activasCount++;
    }
}

require __DIR__ . '/../includes/header.php';
?>

<div class="page-heading">
    <div>
        <span class="eyebrow">GESTIÓN ELECTORAL INSTITUCIONAL</span>
        <h1>Panel de Administración SVIS</h1>
        <p class="muted">
            Administraci&oacute;n del sistema a cargo de <strong>Andrea Mart&iacute;nez Cruz</strong> & <strong>Doris Yasm&iacute;n L&oacute;pez Chocont&aacute;</strong>.
        </p>
    </div>
    <span class="badge info" style="padding: 0.5rem 1.1rem; font-size: 0.85rem;">
        ⚙️ PANEL ADMINISTRADOR
    </span>
</div>

<!-- Widgets de Métricas Rápidas (KPIs) -->
<div class="stats-grid">
    <div class="stat-card">
        <div class="stat-bubble emerald">🗳️</div>
        <div class="stat-info">
            <span class="stat-value"><?= $totalEncuestas ?></span>
            <span class="stat-label">Total Consultas</span>
        </div>
    </div>

    <div class="stat-card">
        <div class="stat-bubble blue">🟢</div>
        <div class="stat-info">
            <span class="stat-value"><?= $activasCount ?></span>
            <span class="stat-label">Consultas Activas</span>
        </div>
    </div>

    <div class="stat-card">
        <div class="stat-bubble indigo">🔑</div>
        <div class="stat-info">
            <span class="stat-value">OTP Único</span>
            <span class="stat-label">Seguridad REGLA 1</span>
        </div>
    </div>

    <div class="stat-card">
        <div class="stat-bubble amber">⚡</div>
        <div class="stat-info">
            <span class="stat-value">ACID</span>
            <span class="stat-label">Concurrencia REGLA 3</span>
        </div>
    </div>
</div>

<?php if ($msg): ?>
    <div class="alert ok"><strong>Operación exitosa:</strong> <?= h($msg) ?></div>
<?php endif; ?>

<?php if ($err): ?>
    <div class="alert error"><strong>Aviso del servidor:</strong> <?= h($err) ?></div>
<?php endif; ?>

<div class="cols">
    <!-- Formulario: Crear Nueva Consulta -->
    <section class="card" style="border-top: 5px solid var(--primary);">
        <h2>Crear Nueva Consulta Electoral</h2>
        <p class="muted" style="margin-bottom: 1.5rem; font-size: 0.92rem;">
            Define los datos de la elección y los candidatos disponibles. El contador de cada opción iniciará en cero.
        </p>

        <form method="post" class="form" autocomplete="off">
            <input type="hidden" name="accion" value="crear">

            <label>Título Institucional de la Consulta *
                <input type="text" name="titulo" required placeholder="Ej: Elección Vocero ADSO 2026" value="<?= h($_POST['titulo'] ?? '') ?>">
            </label>

            <label>Descripción / Propósito Democrático
                <textarea name="descripcion" rows="3" placeholder="Descripción detallada para orientar a los votantes..."><?= h($_POST['descripcion'] ?? '') ?></textarea>
            </label>

            <div>
                <label style="margin-bottom: 0.6rem;">Candidatos u Opciones de Elección *</label>
                <div id="contenedor-opciones" style="display: flex; flex-direction: column; gap: 0.6rem;">
                    <div style="display: flex; gap: 0.5rem; align-items: center;">
                        <span style="font-weight: 800; color: var(--primary); width: 24px;">1.</span>
                        <input type="text" name="opciones[]" required placeholder="Candidato / Lista 1 (Ej: Lista A)">
                    </div>
                    <div style="display: flex; gap: 0.5rem; align-items: center;">
                        <span style="font-weight: 800; color: var(--primary); width: 24px;">2.</span>
                        <input type="text" name="opciones[]" required placeholder="Candidato / Lista 2 (Ej: Lista B)">
                    </div>
                    <div style="display: flex; gap: 0.5rem; align-items: center;">
                        <span style="font-weight: 800; color: var(--text-muted); width: 24px;">3.</span>
                        <input type="text" name="opciones[]" placeholder="Opción 3 (Ej: Voto en Blanco)">
                    </div>
                </div>

                <button type="button" class="btn small ghost" style="margin-top: 0.8rem; width: 100%;" onclick="agregarOpcion()">
                    + Agregar otra opción
                </button>
            </div>

            <button class="btn" type="submit" style="margin-top: 0.8rem; width: 100%; padding: 0.95rem;">
                💾 Guardar y Habilitar Consulta
            </button>
        </form>
    </section>

    <!-- Lista de Encuestas Registradas y Acciones -->
    <section>
        <h2>Consultas Electorales Parametrizadas</h2>
        <?php if (empty($encuestas)): ?>
            <div class="card" style="text-align: center; padding: 3rem 1.5rem;">
                <p class="muted">Aún no se han creado consultas en el sistema. Utiliza el formulario de la izquierda.</p>
            </div>
        <?php endif; ?>

        <div style="display: flex; flex-direction: column; gap: 1.2rem;">
        <?php foreach ($encuestas as $e): ?>
            <?php
                $id = (int) ($e['id'] ?? 0);
                $estado = strtoupper($e['estado'] ?? 'CERRADA');
                $isActiva = ($estado === 'ACTIVA');
                $opcionesCount = count($e['opciones'] ?? []);
            ?>
            <article class="card" style="border-left: 6px solid <?= $isActiva ? 'var(--primary)' : '#94a3b8' ?>;">
                <div style="display: flex; justify-content: space-between; align-items: flex-start; gap: 0.5rem; flex-wrap: wrap;">
                    <span class="badge <?= $isActiva ? 'ok' : 'cerrada' ?>">
                        <?= $isActiva ? '🟢 ACTIVA' : '🔴 CERRADA' ?>
                    </span>
                    <small class="muted" style="font-weight: 700;">ID #<?= $id ?> · <?= $opcionesCount ?> opciones</small>
                </div>

                <h3 style="margin-top: 0.6rem; font-size: 1.25rem;"><?= h($e['titulo'] ?? 'Encuesta') ?></h3>
                <p class="muted" style="margin-top: 0.2rem; font-size: 0.94rem;"><?= h($e['descripcion'] ?? '') ?></p>

                <!-- Acciones Electorales -->
                <div style="display: flex; gap: 0.5rem; margin-top: 1.3rem; flex-wrap: wrap; border-top: 1px solid var(--border); padding-top: 1rem;">
                    <!-- Generar Padrón OTP -->
                    <form method="post" style="display:inline;">
                        <input type="hidden" name="accion" value="generar">
                        <input type="hidden" name="encuestaId" value="<?= $id ?>">
                        <button class="btn small" type="submit" title="Genera un token OTP exclusivo por cada aprendiz">
                            🔑 Generar Padrón OTP
                        </button>
                    </form>

                    <!-- Conmutador de Estado -->
                    <form method="post" style="display:inline;">
                        <input type="hidden" name="accion" value="estado">
                        <input type="hidden" name="encuestaId" value="<?= $id ?>">
                        <input type="hidden" name="estado" value="<?= $isActiva ? 'CERRADA' : 'ACTIVA' ?>">
                        <button class="btn small <?= $isActiva ? 'btn-danger' : 'ghost' ?>" type="submit">
                            <?= $isActiva ? '⛔ Cerrar Votación' : '▶️ Abrir Votación' ?>
                        </button>
                    </form>

                    <!-- Ver Escrutinio -->
                    <form method="post" style="display:inline;">
                        <input type="hidden" name="accion" value="resultados">
                        <input type="hidden" name="encuestaId" value="<?= $id ?>">
                        <button class="btn small primary" type="submit">
                            📊 Ver Escrutinio
                        </button>
                    </form>

                    <!-- Ver Padrón -->
                    <form method="post" style="display:inline;">
                        <input type="hidden" name="accion" value="ver_tokens">
                        <input type="hidden" name="encuestaId" value="<?= $id ?>">
                        <button class="btn small ghost" type="submit">
                            👥 Ver Padrón
                        </button>
                    </form>
                </div>
            </article>
        <?php endforeach; ?>
        </div>
    </section>
</div>

<!-- Sección de Escrutinio en Vivo -->
<?php if ($detalleResultados !== null): ?>
    <section class="card" style="margin-top: 3rem; border-top: 5px solid var(--primary);" id="escrutinio">
        <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem; border-bottom: 1.5px solid var(--border); padding-bottom: 1.2rem;">
            <div>
                <span class="eyebrow">CONSOLIDADO DEFINITIVO DE RESULTADOS</span>
                <h2>Resultados: <?= h($detalleResultados['titulo'] ?? 'Encuesta #' . $encuestaSeleccionada) ?></h2>
                <p class="muted">
                    Total Sufragios Emitidos: <strong><?= (int) ($detalleResultados['totalVotos'] ?? 0) ?></strong> |
                    Padrón Habilitado: <strong><?= (int) ($detalleResultados['totalPadron'] ?? 0) ?></strong> |
                    Participación: <strong><?= h($detalleResultados['participacionPorcentaje'] ?? 0) ?>%</strong>
                </p>
            </div>
            <span class="badge <?= ($detalleResultados['estado'] ?? '') === 'ACTIVA' ? 'ok' : 'cerrada' ?>" style="font-size: 0.85rem;">
                ESTADO: <?= h($detalleResultados['estado'] ?? '') ?>
            </span>
        </div>

        <div class="results-list">
            <?php foreach (($detalleResultados['opciones'] ?? []) as $opc): ?>
                <?php
                    $votos = (int) ($opc['cantidadVotos'] ?? $opc['votosConteo'] ?? 0);
                    $pct = (float) ($opc['porcentaje'] ?? 0.0);
                ?>
                <div class="result-item">
                    <div class="result-info">
                        <span class="result-name"><?= h($opc['nombre'] ?? $opc['titulo'] ?? 'Opción') ?></span>
                        <span class="result-stats"><?= $votos ?> sufragios (<?= $pct ?>%)</span>
                    </div>
                    <div class="progress-track">
                        <div class="progress-fill" style="width: <?= $pct ?>%;"></div>
                    </div>
                </div>
            <?php endforeach; ?>
        </div>
    </section>
<?php endif; ?>

<!-- Sección de Auditoría de Tokens OTP -->
<?php if ($detalleTokens !== null): ?>
    <section class="card" style="margin-top: 3rem; border-top: 5px solid var(--secondary);" id="padron">
        <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1.5px solid var(--border); padding-bottom: 1.2rem; flex-wrap: wrap; gap: 1rem;">
            <div>
                <span class="eyebrow">AUDITORÍA Y PADRÓN ELECTORAL</span>
                <h2>Tokens OTP Generados para Encuesta #<?= (int) $encuestaSeleccionada ?></h2>
                <p class="muted">Total de credenciales emitidas: <strong><?= count($detalleTokens) ?></strong></p>
            </div>
        </div>

        <?php if (empty($detalleTokens)): ?>
            <p class="muted" style="padding: 2rem 0; text-align: center;">Aún no se han generado tokens para esta consulta. Haz clic en "🔑 Generar Padrón OTP".</p>
        <?php else: ?>
            <div class="table-wrap">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Aprendiz / Votante</th>
                            <th>Correo Institucional</th>
                            <th>Token OTP (Voto Único)</th>
                            <th>Estado</th>
                            <th>Expiración TTL</th>
                            <th>Fecha de Uso</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php foreach ($detalleTokens as $t): ?>
                            <?php $usado = (($t['estado'] ?? '') === 'USADO'); ?>
                            <tr>
                                <td><?= (int) ($t['id'] ?? 0) ?></td>
                                <td><strong><?= h($t['nombre'] ?? $t['nombreCompleto'] ?? '') ?></strong></td>
                                <td><code><?= h($t['correo'] ?? $t['documento'] ?? '') ?></code></td>
                                <td><span class="otp-code"><?= h($t['token'] ?? '') ?></span></td>
                                <td>
                                    <span class="badge <?= $usado ? 'usado' : 'ok' ?>">
                                        <?= $usado ? '🔒 USADO' : '🟢 DISPONIBLE' ?>
                                    </span>
                                </td>
                                <td><small class="muted"><?= h(fmtFecha($t['fechaExpiracion'] ?? null)) ?></small></td>
                                <td><small class="muted"><?= h(fmtFecha($t['fechaUso'] ?? null)) ?></small></td>
                            </tr>
                        <?php endforeach; ?>
                    </tbody>
                </table>
            </div>
        <?php endif; ?>
    </section>
<?php endif; ?>

<script>
function agregarOpcion() {
    const c = document.getElementById('contenedor-opciones');
    const total = c.children.length + 1;
    const div = document.createElement('div');
    div.style = "display: flex; gap: 0.5rem; align-items: center;";
    div.innerHTML = `<span style="font-weight: 800; color: var(--primary); width: 24px;">${total}.</span>
                     <input type="text" name="opciones[]" required placeholder="Nueva opción o candidato...">`;
    c.appendChild(div);
    div.querySelector('input').focus();
}
</script>

<?php require __DIR__ . '/../includes/footer.php'; ?>
