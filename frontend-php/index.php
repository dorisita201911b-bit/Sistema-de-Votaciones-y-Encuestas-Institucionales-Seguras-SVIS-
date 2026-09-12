<?php
require_once __DIR__ . '/config.php';
require_once __DIR__ . '/includes/ApiClient.php';
require_once __DIR__ . '/includes/auth.php';

if (is_logged_in()) {
    header('Location: ' . (is_admin() ? 'admin/dashboard.php' : 'votar.php'));
    exit;
}

$titulo = 'Democracia Institucional Segura';
$res = api()->get('/encuestas/activas');
$encuestas = $res['ok'] ? ($res['data']['encuestas'] ?? $res['data'] ?? []) : [];

require __DIR__ . '/includes/header.php';
?>

<!-- Hero Banner Principal -->
<section class="hero-banner">
    <div class="hero-content">
        <div class="hero-badge">
            <span>🛡️ Plataforma Electoral Desacoplada · ADSO 2026</span>
        </div>
        <h1 class="hero-title">
            Democracia Institucional <span>Segura, Ágil y Transparente</span>
        </h1>
        <p class="hero-description">
            Sistema automatizado para elecciones de representantes estudiantiles y consultas democráticas.
            Blindado contra votos múltiples y condiciones de carrera mediante <strong>Tokens OTP de un solo uso</strong> y <strong>bloqueo transaccional ACID</strong>.
        </p>
        <div style="display: flex; gap: 1rem; flex-wrap: wrap;">
            <a href="login.php" class="btn" style="padding: 0.95rem 2rem; font-size: 1.05rem;">
                🗳️ Iniciar Sesión para Votar
            </a>
            <a href="#garantias" class="btn ghost">
                Ver Garantías Técnicas ↓
            </a>
        </div>
    </div>
</section>

<!-- Consultas Activas -->
<div class="page-heading">
    <div>
        <span class="eyebrow">CONSULTAS DISPONIBLES</span>
        <h2>Jornadas Democráticas Habilitadas</h2>
        <p class="muted">Visualiza las consultas institucionales vigentes. Para sufragar debes ingresar con tu usuario y token OTP.</p>
    </div>
    <span class="badge ok">SISTEMA ACTIVO</span>
</div>

<?php if (!$res['ok']): ?>
    <div class="alert error">
        <strong>Aviso de conexión:</strong> <?= h($res['error'] ?? 'No se pudieron sincronizar las consultas con el servidor.') ?>
    </div>
<?php elseif (empty($encuestas)): ?>
    <div class="card" style="text-align: center; padding: 4rem 2rem;">
        <span style="font-size: 3rem; display: block; margin-bottom: 1rem;">🗳️</span>
        <h2>No hay consultas activas en este instante</h2>
        <p class="muted">El comité electoral publicará aquí las nuevas convocatorias en cuanto sean aperturadas.</p>
    </div>
<?php else: ?>
    <div class="grid">
        <?php foreach ($encuestas as $e): ?>
            <?php
                $encId = (int) ($e['id'] ?? 0);
                $opciones = $e['opciones'] ?? [];
            ?>
            <article class="card" style="display: flex; flex-direction: column; justify-content: space-between; border-top: 5px solid var(--primary);">
                <div>
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.8rem;">
                        <span class="badge ok">🟢 ACTIVA</span>
                        <small class="muted" style="font-weight: 700;">ID #<?= $encId ?></small>
                    </div>
                    <h3 style="font-size: 1.3rem; margin-bottom: 0.5rem;"><?= h($e['titulo'] ?? 'Encuesta') ?></h3>
                    <p class="muted" style="font-size: 0.92rem; margin-bottom: 1.3rem;"><?= h($e['descripcion'] ?? '') ?></p>

                    <?php if (!empty($opciones)): ?>
                        <div style="background: #f8fafc; border: 1px solid var(--border); border-radius: var(--radius-sm); padding: 1rem; margin-bottom: 1.4rem;">
                            <span style="font-size: 0.76rem; font-weight: 800; text-transform: uppercase; color: var(--primary); display: block; margin-bottom: 0.5rem; letter-spacing: 0.5px;">
                                Candidatos / Opciones en contienda (<?= count($opciones) ?>):
                            </span>
                            <ul style="list-style: none; display: flex; flex-direction: column; gap: 0.4rem; font-size: 0.88rem; color: var(--text-body);">
                                <?php foreach ($opciones as $opc): ?>
                                    <li style="display: flex; align-items: center; gap: 0.5rem;">
                                        <span style="color: var(--primary); font-size: 1.1rem;">•</span>
                                        <strong><?= h($opc['nombre'] ?? $opc['titulo'] ?? 'Opción') ?></strong>
                                    </li>
                                <?php endforeach; ?>
                            </ul>
                        </div>
                    <?php endif; ?>
                </div>

                <div style="border-top: 1px solid var(--border); padding-top: 1.2rem;">
                    <a class="btn" href="login.php" style="width: 100%;">
                        Ingresar y Votar con OTP →
                    </a>
                </div>
            </article>
        <?php endforeach; ?>
    </div>
<?php endif; ?>

<!-- Sección de Garantías Técnicas -->
<section id="garantias" class="card" style="margin-top: 3.5rem; background: #ffffff; border-top: 5px solid var(--primary);">
    <div style="text-align: center; max-width: 720px; margin: 0 auto 2.5rem auto;">
        <span class="eyebrow" style="justify-content: center;">ARQUITECTURA DE INTEGRIDAD TOTAL</span>
        <h2 style="font-size: 1.85rem; margin-top: 0.3rem;">Las 4 Reglas de Blindaje Electoral</h2>
        <p class="muted">
            Soluci&oacute;n desarrollada por <strong>Andrea Mart&iacute;nez Cruz</strong> & <strong>Doris Yasm&iacute;n L&oacute;pez Chocont&aacute;</strong> bajo los m&aacute;s rigurosos est&aacute;ndares de seguridad y concurrencia.
        </p>
    </div>

    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 1.8rem;">
        <div style="background: #f0fdf4; padding: 1.6rem; border-radius: var(--radius-md); border: 1px solid #bbf7d0;">
            <div style="font-size: 2.2rem; margin-bottom: 0.6rem;">🔑</div>
            <h3 style="font-size: 1.08rem; margin-bottom: 0.4rem; color: #065f46;">1. Unicidad Absoluta (REGLA 1)</h3>
            <p class="muted" style="font-size: 0.88rem; color: #166534;">
                Restricción <code>UNIQUE(encuesta_id, usuario_id)</code> en motor InnoDB. Es físicamente imposible generar credenciales duplicadas para un mismo aprendiz.
            </p>
        </div>

        <div style="background: #f0f9ff; padding: 1.6rem; border-radius: var(--radius-md); border: 1px solid #bae6fd;">
            <div style="font-size: 2.2rem; margin-bottom: 0.6rem;">⚡</div>
            <h3 style="font-size: 1.08rem; margin-bottom: 0.4rem; color: #0369a1;">2. Quema Atómica (REGLA 2)</h3>
            <p class="muted" style="font-size: 0.88rem; color: #075985;">
                El token pasa de <code>DISPONIBLE</code> a <code>USADO</code> en la misma transacción ACID en que se suma el voto. Si algo falla, se ejecuta Rollback completo.
            </p>
        </div>

        <div style="background: #fef2f2; padding: 1.6rem; border-radius: var(--radius-md); border: 1px solid #fecaca;">
            <div style="font-size: 2.2rem; margin-bottom: 0.6rem;">🔒</div>
            <h3 style="font-size: 1.08rem; margin-bottom: 0.4rem; color: #991b1b;">3. Control Concurrente (REGLA 3)</h3>
            <p class="muted" style="font-size: 0.88rem; color: #7f1d1d;">
                Bloqueo pesimista <code>SELECT ... FOR UPDATE</code>. Ante dos clics simultáneos con el mismo token, uno responde <strong>200 OK</strong> y el otro <strong>409 Conflict</strong>.
            </p>
        </div>

        <div style="background: #f5f3ff; padding: 1.6rem; border-radius: var(--radius-md); border: 1px solid #ddd6fe;">
            <div style="font-size: 2.2rem; margin-bottom: 0.6rem;">🛡️</div>
            <h3 style="font-size: 1.08rem; margin-bottom: 0.4rem; color: #5b21b6;">4. Secreto del Sufragio (REGLA 4)</h3>
            <p class="muted" style="font-size: 0.88rem; color: #4c1d95;">
                El contador electoral se incrementa numéricamente (+1). Ninguna tabla vincula la identidad del estudiante con la opción seleccionada.
            </p>
        </div>
    </div>
</section>

<?php require __DIR__ . '/includes/footer.php'; ?>
