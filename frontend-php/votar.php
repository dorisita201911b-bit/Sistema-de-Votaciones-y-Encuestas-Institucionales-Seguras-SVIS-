<?php
require_once __DIR__ . '/config.php';
require_once __DIR__ . '/includes/ApiClient.php';
require_once __DIR__ . '/includes/auth.php';

require_login();
$user = current_user();
$userId = $user['id'] ?? 0;

$titulo = 'Emisión del Sufragio';
$msg = null;
$err = null;
$comprobante = null;

// Procesar emisión de voto
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $opcionId = (int) ($_POST['opcionId'] ?? 0);
    $token = trim($_POST['token'] ?? $_POST['otp'] ?? '');
    $encuestaId = (int) ($_POST['encuestaId'] ?? 0);

    if ($opcionId <= 0) {
        $err = 'Por favor selecciona un candidato u opción de la lista.';
    } elseif ($token === '') {
        $err = 'No se encontró un Token OTP asignado válido para procesar.';
    } else {
        $res = api()->post('/votos/emitir', [
            'token' => $token,
            'opcionId' => $opcionId,
            'encuestaId' => $encuestaId,
        ]);

        if ($res['ok']) {
            $data = $res['data'] ?? [];
            $comprobante = $data['hashRecibo'] ?? $data['comprobanteHash'] ?? $data['hash'] ?? null;
            $msg = $data['mensaje'] ?? '¡Sufragio registrado y contabilizado con éxito!';
        } else {
            if (($res['status'] ?? 0) === 409) {
                // REGLA 3: Detección y bloqueo de condición de carrera
                $err = '409 Conflict: El token OTP ya ha sido utilizado previamente o se detectó una emisión concurrente en simultáneo. Transacción abortada para garantizar voto único.';
            } else {
                $err = ($res['data']['mensaje'] ?? null) ?: ($res['error'] ?? 'Error procesando el voto en el servidor.');
            }
        }
    }
}

// Consultar encuestas activas
$activasRes = api()->get('/encuestas/activas');
$encuestas = $activasRes['ok'] ? ($activasRes['data']['encuestas'] ?? $activasRes['data'] ?? []) : [];
if (!$activasRes['ok'] && !$err) {
    $err = $activasRes['error'] ?? 'No se pudieron sincronizar las encuestas activas.';
}

require __DIR__ . '/includes/header.php';
?>

<div class="page-heading">
    <div>
        <span class="eyebrow">EJERCICIO DEL SUFRAGIO SEGURO</span>
        <h1>Consultas Electorales Asignadas</h1>
        <p class="muted">
            Bienvenido(a), <strong><?= h($user['nombre'] ?? $user['correo']) ?></strong> (<code><?= h($user['correo']) ?></code>).
            Cada aprendiz registrado tiene derecho a exactamente un (1) voto por consulta mediante Token OTP.
        </p>
    </div>
    <span class="badge ok" style="padding: 0.5rem 1.1rem; font-size: 0.85rem;">
        🎓 ESTUDIANTE / VOTANTE
    </span>
</div>

<?php if ($msg): ?>
    <div class="alert ok">
        <div style="display: flex; align-items: center; gap: 0.6rem;">
            <span style="font-size: 1.5rem;">🎉</span>
            <strong style="font-size: 1.1rem;"><?= h($msg) ?></strong>
        </div>
        <?php if ($comprobante): ?>
            <div class="receipt-box">
                <div class="receipt-header">
                    <span>🛡️ Comprobante Digital Anónimo (Hash Criptográfico SHA-256)</span>
                </div>
                <span class="receipt-hash" id="hashReceipt"><?= h($comprobante) ?></span>
                <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.6rem; margin-top: 0.8rem;">
                    <small style="color: #065f46; font-weight: 600;">
                        ✓ Sufragio contabilizado bajo transacción atómica ACID · Token OTP quemado de manera irreversible.
                    </small>
                    <button type="button" class="btn small ghost" onclick="copiarComprobante()">
                        📋 Copiar Comprobante
                    </button>
                </div>
            </div>
        <?php endif; ?>
    </div>
<?php endif; ?>

<?php if ($err): ?>
    <div class="alert error">
        <div style="display: flex; align-items: center; gap: 0.6rem;">
            <span style="font-size: 1.5rem;">⚠️</span>
            <strong style="font-size: 1.08rem;">Aviso del Sistema Electoral:</strong>
        </div>
        <p style="margin-top: 0.4rem;"><?= h($err) ?></p>
    </div>
<?php endif; ?>

<?php if (empty($encuestas)): ?>
    <div class="card" style="text-align: center; padding: 4rem 2rem;">
        <span style="font-size: 3rem; display: block; margin-bottom: 1rem;">🗳️</span>
        <h2>No tienes consultas democráticas activas en este momento</h2>
        <p class="muted">Cuando la administración electoral genere una consulta para tu ficha o padrón, se habilitará aquí.</p>
    </div>
<?php endif; ?>

<div class="grid">
<?php foreach ($encuestas as $encuesta): ?>
    <?php
        $encId = (int) ($encuesta['id'] ?? 0);
        $opciones = $encuesta['opciones'] ?? [];

        // Consultar el estado del token del estudiante para esta encuesta
        $tokenRes = api()->get('/tokens/mi-token?encuesta_id=' . $encId . '&usuario_id=' . $userId);
        $tokenData = $tokenRes['ok'] ? ($tokenRes['data'] ?? null) : null;
        $tieneToken = !empty($tokenData);
        $estadoToken = $tokenData['estado'] ?? 'NO_ASIGNADO';
        $yaVoto = ($estadoToken === 'USADO');
        $otpValor = $tokenData['token'] ?? '';
    ?>
    <article class="card vote-card <?= $yaVoto ? 'is-locked' : '' ?>">
        <div style="display: flex; justify-content: space-between; align-items: flex-start; gap: 0.8rem;">
            <span class="badge <?= $yaVoto ? 'usado' : 'ok' ?>">
                <?= $yaVoto ? '🔒 VOTO REGISTRADO' : '🟢 SUFRAGIO DISPONIBLE' ?>
            </span>
            <small class="muted" style="font-weight: 700;">ID Consulta #<?= $encId ?></small>
        </div>

        <div>
            <h2 style="font-size: 1.35rem; margin-bottom: 0.4rem;"><?= h($encuesta['titulo'] ?? 'Encuesta') ?></h2>
            <p class="muted" style="font-size: 0.94rem;"><?= h($encuesta['descripcion'] ?? '') ?></p>
        </div>

        <?php if ($yaVoto): ?>
            <!-- Bloqueo visual estricto si el usuario ya votó -->
            <div class="locked-overlay">
                <div class="locked-icon-badge">🔒</div>
                <div class="locked-title">CONSULTA BLOQUEADA PARA ESTE USUARIO</div>
                <p class="muted" style="font-size: 0.9rem; max-width: 380px;">
                    Tu sufragio ya fue recibido y contabilizado en el servidor. Tu Token OTP exclusivo ha pasado a estado <strong>USADO</strong> y ha sido quemado atómicamente.
                </p>
                <?php if (!empty($tokenData['fechaUso'])): ?>
                    <span class="badge usado" style="margin-top: 0.6rem; font-size: 0.75rem;">
                        Emitido el: <?= h(fmtFecha($tokenData['fechaUso'])) ?>
                    </span>
                <?php endif; ?>
            </div>
        <?php elseif (!$tieneToken): ?>
            <div class="alert ok" style="background: #f0fdf4; border-color: #bbf7d0; color: #166534;">
                ℹ️ <strong>Padrón en preparación:</strong> La administración electoral aún no ha generado tu Token OTP personal para esta encuesta. Por favor solicita al administrador que genere el padrón de credenciales.
            </div>
        <?php else: ?>
            <!-- Formulario interactivo de votación -->
            <form method="post" class="form" autocomplete="off" onsubmit="return confirmarVoto(this);">
                <input type="hidden" name="encuestaId" value="<?= $encId ?>">
                <input type="hidden" name="token" value="<?= h($otpValor) ?>">

                <div class="otp-display-card">
                    <div>
                        <span class="otp-title">Tu Token OTP Asignado:</span>
                        <div class="otp-code"><?= h($otpValor) ?></div>
                    </div>
                    <span class="badge ok">DISPONIBLE</span>
                </div>

                <div>
                    <label style="margin-bottom: 0.6rem;">Selecciona tu preferencia electoral:</label>
                    <div class="options-selector">
                        <?php foreach ($opciones as $opc): ?>
                            <?php $opcId = (int) ($opc['id'] ?? 0); ?>
                            <label class="option-radio-card">
                                <input type="radio" name="opcionId" value="<?= $opcId ?>" required>
                                <div class="option-text">
                                    <?= h($opc['nombre'] ?? $opc['titulo'] ?? 'Opción') ?>
                                </div>
                            </label>
                        <?php endforeach; ?>
                    </div>
                </div>

                <button class="btn" type="submit" style="width: 100%; margin-top: 0.5rem; padding: 1rem; font-size: 1.05rem;">
                    🗳️ Confirmar y Emitir Voto
                </button>
            </form>
        <?php endif; ?>
    </article>
<?php endforeach; ?>
</div>

<script>
function confirmarVoto(form) {
    const seleccionado = form.querySelector('input[name="opcionId"]:checked');
    if (!seleccionado) {
        alert('Por favor selecciona una opción antes de continuar.');
        return false;
    }
    return confirm('¿Confirmas la emisión definitiva de tu voto? Tu Token OTP será quemado de forma inmediata e irreversible (REGLA 2).');
}

function copiarComprobante() {
    const text = document.getElementById('hashReceipt').innerText;
    navigator.clipboard.writeText(text).then(() => {
        alert('¡Comprobante copiado al portapapeles!');
    });
}
</script>

<?php require __DIR__ . '/includes/footer.php'; ?>
