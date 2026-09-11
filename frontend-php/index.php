<?php
require_once __DIR__ . '/config.php';
require_once __DIR__ . '/includes/ApiClient.php';
require_once __DIR__ . '/includes/auth.php';

if (is_logged_in()) {
    header('Location: ' . (is_admin() ? 'admin/dashboard.php' : 'votar.php'));
    exit;
}

$titulo = 'Eventos disponibles';
$res = api()->get('/encuestas/activas');

require __DIR__ . '/includes/header.php';
?>
<h1>Consultas institucionales activas</h1>
<p class="muted">Inicia sesión para ejercer tu voto con el OTP asignado.</p>

<?php if (!$res['ok']): ?>
    <div class="alert error"><?= h($res['error'] ?? 'No se pudieron cargar los eventos') ?></div>
<?php elseif (empty($res['data'])): ?>
    <p>No hay encuestas activas por el momento.</p>
<?php else: ?>
    <div class="grid">
        <?php foreach (($res['data']['encuestas'] ?? $res['data']) as $e): ?>
            <article class="card">
                <h3><?= h($e['titulo'] ?? $e['nombre'] ?? 'Encuesta') ?></h3>
                <p class="muted"><?= h($e['descripcion'] ?? '') ?></p>
                <ul class="meta">
                    <li>Estado: <span class="badge ok">ACTIVA</span></li>
                    <li>Cierre: <?= h(fmtFecha($e['fechaCierre'] ?? $e['cierre'] ?? null)) ?></li>
                </ul>
                <a class="btn" href="login.php">Iniciar sesión para votar</a>
            </article>
        <?php endforeach; ?>
    </div>
<?php endif; ?>
<?php require __DIR__ . '/includes/footer.php'; ?>
