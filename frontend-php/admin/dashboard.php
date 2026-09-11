<?php
require_once __DIR__ . '/../config.php';
require_once __DIR__ . '/../includes/ApiClient.php';
require_once __DIR__ . '/../includes/auth.php';

require_admin();
$adminPage = true;
$titulo = 'Panel electoral';
$msg = null;
$err = null;
$detalleResultados = null;

function encuesta_lista($data)
{
    return $data['encuestas'] ?? $data['content'] ?? $data ?? [];
}

function respuesta_error($res, $fallback)
{
    return ($res['data']['mensaje'] ?? null) ?: ($res['data']['message'] ?? null) ?: ($res['error'] ?? $fallback);
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $accion = $_POST['accion'] ?? '';
    if ($accion === 'crear') {
        $opciones = array_values(array_filter(array_map('trim', $_POST['opciones'] ?? [])));
        if (count($opciones) < 2) {
            $err = 'Una encuesta debe tener al menos dos opciones.';
        } else {
            $res = api()->post('/encuestas', [
                'titulo' => trim($_POST['titulo'] ?? ''),
                'descripcion' => trim($_POST['descripcion'] ?? ''),
                'fechaCierre' => $_POST['fechaCierre'] ?? null,
                'opciones' => $opciones,
            ]);
            $res['ok'] ? $msg = 'Encuesta creada correctamente.' : $err = respuesta_error($res, 'No se pudo crear la encuesta.');
        }
    } elseif ($accion === 'generar') {
        $res = api()->post('/tokens/generar', ['encuestaId' => (int) ($_POST['encuestaId'] ?? 0)]);
        $res['ok'] ? $msg = 'Padrón de tokens OTP generado.' : $err = respuesta_error($res, 'No se pudo generar el padrón.');
    } elseif ($accion === 'estado') {
        $res = api()->post('/encuestas/' . (int) ($_POST['encuestaId'] ?? 0) . '/estado', ['estado' => $_POST['estado'] ?? 'CERRADA']);
        $res['ok'] ? $msg = 'Estado de la encuesta actualizado.' : $err = respuesta_error($res, 'No se pudo actualizar el estado.');
    } elseif ($accion === 'resultados') {
        $res = api()->get('/encuestas/' . (int) ($_POST['encuestaId'] ?? 0) . '/resultados');
        if ($res['ok']) {
            $detalleResultados = $res['data'] ?? [];
        } else {
            $err = respuesta_error($res, 'No se pudieron cargar los resultados.');
        }
    }
}

$listaRes = api()->get('/encuestas');
$encuestas = $listaRes['ok'] ? encuesta_lista($listaRes['data']) : [];
if (!$listaRes['ok'] && !$err) {
    $err = respuesta_error($listaRes, 'No se pudieron cargar las encuestas.');
}
require __DIR__ . '/../includes/header.php';
?>
<div class="page-heading">
    <div><span class="eyebrow">GESTIÓN ELECTORAL</span><h1>Panel de encuestas</h1><p class="muted">Configura la consulta, genera el padrón y supervisa sus resultados.</p></div>
    <span class="badge ok">ROL ADMINISTRADOR</span>
</div>
<?php if ($msg): ?><div class="alert ok"><?= h($msg) ?></div><?php endif; ?>
<?php if ($err): ?><div class="alert error"><?= h($err) ?></div><?php endif; ?>
<div class="cols">
    <section class="card">
        <h2>Crear encuesta</h2>
        <form method="post" class="form">
            <input type="hidden" name="accion" value="crear">
            <label>Título institucional *<input type="text" name="titulo" maxlength="160" required></label>
            <label>Descripción<textarea name="descripcion" rows="3" maxlength="600"></textarea></label>
            <label>Fecha de cierre<input type="datetime-local" name="fechaCierre"></label>
            <div id="opciones" class="form"><label>Opciones de elección *<input type="text" name="opciones[]" required placeholder="Ej: Lista A"></label><label><input type="text" name="opciones[]" required placeholder="Ej: Lista B"></label></div>
            <button class="btn" type="button" onclick="agregarOpcion()">+ Agregar opción</button>
            <button class="btn" type="submit">Crear encuesta</button>
        </form>
    </section>
    <section>
        <h2>Encuestas y control</h2>
        <?php if (empty($encuestas)): ?><div class="card"><p class="muted">Aún no hay encuestas registradas.</p></div><?php endif; ?>
        <?php foreach ($encuestas as $encuesta): ?>
            <?php $id = (int) ($encuesta['id'] ?? $encuesta['encuestaId'] ?? 0); $estado = strtoupper($encuesta['estado'] ?? 'CERRADA'); ?>
            <article class="card survey-row">
                <div><span class="badge <?= $estado === 'ACTIVA' ? 'ok' : 'pending' ?>"><?= h($estado) ?></span><h3><?= h($encuesta['titulo'] ?? $encuesta['nombre'] ?? 'Encuesta') ?></h3><p class="muted"><?= h($encuesta['descripcion'] ?? '') ?></p></div>
                <div class="action-grid">
                    <form method="post"><input type="hidden" name="accion" value="generar"><input type="hidden" name="encuestaId" value="<?= $id ?>"><button class="btn small ghost" type="submit">Generar OTP</button></form>
                    <form method="post"><input type="hidden" name="accion" value="estado"><input type="hidden" name="encuestaId" value="<?= $id ?>"><input type="hidden" name="estado" value="<?= $estado === 'ACTIVA' ? 'CERRADA' : 'ACTIVA' ?>"><button class="btn small" type="submit"><?= $estado === 'ACTIVA' ? 'Cerrar votación' : 'Abrir votación' ?></button></form>
                    <form method="post"><input type="hidden" name="accion" value="resultados"><input type="hidden" name="encuestaId" value="<?= $id ?>"><button class="btn small ghost" type="submit">Ver resultados</button></form>
                </div>
            </article>
        <?php endforeach; ?>
    </section>
</div>
<?php if ($detalleResultados !== null): ?>
<section class="card results"><h2>Resultados consolidados</h2><div class="results-list">
<?php foreach (($detalleResultados['opciones'] ?? $detalleResultados['resultados'] ?? []) as $resultado): ?><div class="result-item"><div><strong><?= h($resultado['texto'] ?? $resultado['nombre'] ?? 'Opción') ?></strong><span class="muted"> <?= (int) ($resultado['votos'] ?? $resultado['cantidad'] ?? 0) ?> votos</span></div><strong><?= h($resultado['porcentaje'] ?? 0) ?>%</strong></div><?php endforeach; ?>
</div></section>
<?php endif; ?>
<script>function agregarOpcion(){const c=document.getElementById('opciones');const l=document.createElement('label');l.innerHTML='<input type="text" name="opciones[]" required placeholder="Nueva opción">';c.appendChild(l);}</script>
<?php require __DIR__ . '/../includes/footer.php'; ?>
