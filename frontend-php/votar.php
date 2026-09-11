<?php
require_once __DIR__ . '/config.php';
require_once __DIR__ . '/includes/ApiClient.php';
require_once __DIR__ . '/includes/auth.php';

require_login();
$titulo = 'Emitir voto';
$msg = null;
$err = null;
$comprobante = null;

function voto_error($res)
{
    if (($res['status'] ?? 0) === 409) {
        return 'Este token ya fue usado o la votación acaba de recibir otro voto. No es posible votar nuevamente.';
    }
    return ($res['data']['mensaje'] ?? null) ?: ($res['data']['message'] ?? null) ?: ($res['error'] ?? 'No se pudo procesar el voto.');
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $encuestaId = (int) ($_POST['encuestaId'] ?? 0);
    $res = api()->post('/votos/emitir', [
        'encuestaId' => $encuestaId,
        'opcionId' => (int) ($_POST['opcionId'] ?? 0),
        'otp' => trim($_POST['otp'] ?? ''),
    ]);
    if ($res['ok']) {
        $comprobante = $res['data']['comprobante'] ?? $res['data']['recibo'] ?? $res['data']['hash'] ?? null;
        $msg = 'Voto procesado correctamente. Tu OTP fue marcado como USADO.';
    } else {
        $err = voto_error($res);
    }
}

$activasRes = api()->get('/encuestas/activas');
$encuestas = $activasRes['ok'] ? ($activasRes['data']['encuestas'] ?? $activasRes['data'] ?? []) : [];
if (!$activasRes['ok'] && !$err) {
    $err = $activasRes['error'] ?? 'No se pudieron cargar las encuestas activas.';
}
require __DIR__ . '/includes/header.php';
?>
<div class="page-heading"><div><span class="eyebrow">EJERCICIO DEL SUFRAGIO</span><h1>Mis votaciones</h1><p class="muted">Cada OTP permite una sola emisión y nunca se guarda junto con tu elección.</p></div><span class="badge ok">VOTANTE</span></div>
<?php if ($msg): ?><div class="alert ok"><?= h($msg) ?><?php if ($comprobante): ?><br><strong>Comprobante anónimo:</strong> <code><?= h($comprobante) ?></code><?php endif; ?></div><?php endif; ?>
<?php if ($err): ?><div class="alert error"><?= h($err) ?></div><?php endif; ?>
<?php if (empty($encuestas)): ?><div class="card empty-state"><h2>No tienes consultas activas</h2><p class="muted">Cuando exista una encuesta habilitada para tu padrón aparecerá aquí.</p></div><?php endif; ?>
<div class="grid">
<?php foreach ($encuestas as $encuesta): ?>
    <?php $id = (int) ($encuesta['id'] ?? $encuesta['encuestaId'] ?? 0); $opciones = $encuesta['opciones'] ?? []; $yaVoto = !empty($encuesta['yaVoto']) || !empty($encuesta['votado']); ?>
    <article class="card vote-card <?= $yaVoto ? 'is-locked' : '' ?>"><span class="badge <?= $yaVoto ? 'pending' : 'ok' ?>"><?= $yaVoto ? 'VOTO REGISTRADO' : 'DISPONIBLE' ?></span><h2><?= h($encuesta['titulo'] ?? $encuesta['nombre'] ?? 'Encuesta') ?></h2><p class="muted"><?= h($encuesta['descripcion'] ?? '') ?></p>
    <?php if ($yaVoto): ?><p class="locked-message">Esta consulta ya está bloqueada para tu usuario.</p><?php else: ?>
    <form method="post" class="form"><input type="hidden" name="encuestaId" value="<?= $id ?>"><label>Selecciona una opción<select name="opcionId" required><option value="">Elegir...</option><?php foreach ($opciones as $opcion): ?><option value="<?= (int) ($opcion['id'] ?? $opcion['opcionId'] ?? 0) ?>"><?= h($opcion['texto'] ?? $opcion['nombre'] ?? $opcion['descripcion'] ?? '') ?></option><?php endforeach; ?></select></label><label>Token OTP asignado<input type="text" name="otp" required maxlength="128" autocomplete="one-time-code" placeholder="Escribe tu token"></label><button class="btn" type="submit">Emitir voto</button></form>
    <?php endif; ?></article>
<?php endforeach; ?>
</div>
<?php require __DIR__ . '/includes/footer.php'; ?>
