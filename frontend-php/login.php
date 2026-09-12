<?php
require_once __DIR__ . '/config.php';
require_once __DIR__ . '/includes/ApiClient.php';
require_once __DIR__ . '/includes/auth.php';

if (is_logged_in()) {
    header('Location: ' . (is_admin() ? 'admin/dashboard.php' : 'votar.php'));
    exit;
}

$error = null;
$mensaje = null;

if (isset($_GET['msg'])) {
    if ($_GET['msg'] === 'admin_required') {
        $error = 'Acceso restringido: Se requieren privilegios de Administrador.';
    } elseif ($_GET['msg'] === 'login_required') {
        $error = 'Por favor inicia sesión para acceder al sistema electoral.';
    }
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $correo = trim($_POST['correo'] ?? '');
    $password = $_POST['password'] ?? '';

    if ($correo === '' || $password === '') {
        $error = 'Por favor ingresa tu correo institucional y contraseña.';
    } else {
        $res = login($correo, $password);
        if ($res['ok']) {
            if (is_admin()) {
                header('Location: admin/dashboard.php');
            } else {
                header('Location: votar.php');
            }
            exit;
        } else {
            $error = ($res['data']['mensaje'] ?? null) ?: ($res['error'] ?? 'Credenciales no válidas en la plataforma institucional.');
        }
    }
}

$titulo = 'Iniciar Sesión';
require __DIR__ . '/includes/header.php';
?>

<div style="max-width: 520px; margin: 2rem auto;">
    <div class="card" style="border-top: 5px solid var(--primary); padding: 2.8rem 2.4rem;">
        <div style="text-align: center; margin-bottom: 2rem;">
            <span class="eyebrow" style="justify-content: center;">PORTAL ELECTORAL INSTITUCIONAL</span>
            <h1 style="font-size: 2rem; margin-top: 0.3rem;">Acceso Seguro a SVIS</h1>
            <p class="muted" style="margin-top: 0.4rem; font-size: 0.94rem;">
                Ingresa con tus credenciales asignadas para sufragar de forma confidencial o administrar consultas democráticas.
            </p>
        </div>

        <?php if ($mensaje): ?>
            <div class="alert ok"><?= h($mensaje) ?></div>
        <?php endif; ?>

        <?php if ($error): ?>
            <div class="alert error">
                <strong>Error de autenticación:</strong> <?= h($error) ?>
            </div>
        <?php endif; ?>

        <form method="post" class="form" id="loginForm" autocomplete="off">
            <label>Correo Electrónico Institucional
                <input type="text" id="inputCorreo" name="correo" required autofocus placeholder="correo@sena.edu.co o @soy.sena.edu.co" value="<?= h($_POST['correo'] ?? '') ?>">
            </label>

            <label>Contraseña de Acceso
                <input type="password" id="inputPassword" name="password" required placeholder="Tu contraseña asignada">
            </label>

            <button class="btn" type="submit" style="width: 100%; margin-top: 0.5rem; padding: 0.95rem; font-size: 1.02rem;">
                🔐 Entrar a la Plataforma
            </button>
        </form>

        <div style="margin-top: 2.4rem; background: #f8fafc; border: 1.5px dashed #cbd5e1; border-radius: var(--radius-md); padding: 1.4rem;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.9rem;">
                <span style="font-weight: 800; font-size: 0.8rem; color: var(--primary); text-transform: uppercase; letter-spacing: 0.6px;">
                    ⚡ Credenciales de Acceso Rápido:
                </span>
                <small class="muted">Clic para autocompletar</small>
            </div>

            <div style="display: flex; flex-direction: column; gap: 0.65rem;">
                <!-- Administradora 1: Andrea Martínez Cruz -->
                <div class="quick-auth-card" onclick="autofill('andrea.martinez@sena.edu.co', 'admin123')">
                    <div class="quick-auth-avatar admin">AM</div>
                    <div class="quick-auth-details">
                        <span class="quick-auth-name">Andrea Mart&iacute;nez Cruz</span>
                        <span class="quick-auth-email">andrea.martinez@sena.edu.co</span>
                    </div>
                    <span class="badge info" style="font-size: 0.65rem;">ADMIN</span>
                </div>

                <!-- Administradora 2: Doris Yasmín López Chocontá -->
                <div class="quick-auth-card" onclick="autofill('doris.lopez@sena.edu.co', 'admin123')">
                    <div class="quick-auth-avatar admin">DL</div>
                    <div class="quick-auth-details">
                        <span class="quick-auth-name">Doris Yasm&iacute;n L&oacute;pez Chocont&aacute;</span>
                        <span class="quick-auth-email">doris.lopez@sena.edu.co</span>
                    </div>
                    <span class="badge info" style="font-size: 0.65rem;">ADMIN</span>
                </div>

                <!-- Aprendiz Votante 1: Carlos Andrés Mendoza -->
                <div class="quick-auth-card" onclick="autofill('carlos.mendoza@soy.sena.edu.co', 'aprendiz123')">
                    <div class="quick-auth-avatar voter">CM</div>
                    <div class="quick-auth-details">
                        <span class="quick-auth-name">Carlos Andr&eacute;s Mendoza</span>
                        <span class="quick-auth-email">carlos.mendoza@soy.sena.edu.co</span>
                    </div>
                    <span class="badge ok" style="font-size: 0.65rem;">VOTANTE</span>
                </div>

                <!-- Aprendiz Votante 2: María Fernanda Gómez -->
                <div class="quick-auth-card" onclick="autofill('maria.gomez@soy.sena.edu.co', 'aprendiz123')">
                    <div class="quick-auth-avatar voter">MG</div>
                    <div class="quick-auth-details">
                        <span class="quick-auth-name">Mar&iacute;a Fernanda G&oacute;mez</span>
                        <span class="quick-auth-email">maria.gomez@soy.sena.edu.co</span>
                    </div>
                    <span class="badge ok" style="font-size: 0.65rem;">VOTANTE</span>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
function autofill(correo, pass) {
    document.getElementById('inputCorreo').value = correo;
    document.getElementById('inputPassword').value = pass;
    document.getElementById('inputCorreo').focus();
}
</script>

<?php require __DIR__ . '/includes/footer.php'; ?>
