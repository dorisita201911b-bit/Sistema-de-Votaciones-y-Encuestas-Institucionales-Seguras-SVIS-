<?php
require_once __DIR__ . '/../config.php';
require_once __DIR__ . '/auth.php';
$isSubfolder = isset($adminPage) && $adminPage;
$baseUrl = $isSubfolder ? '../' : '';
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><?= isset($titulo) ? h($titulo) . ' · ' : '' ?>SVIS · Sistema Institucional de Votaciones Seguras</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@400;500;600;700;800&family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<?= $baseUrl ?>assets/css/styles.css">
</head>
<body>
<header class="topbar">
    <div class="topbar-inner">
        <div class="brand">
            <a href="<?= $baseUrl ?>index.php" class="brand-link">
                <span class="brand-logo-badge">🗳️</span>
                <div class="brand-text">
                    <span class="brand-title">SVIS <span>· Votaciones</span></span>
                    <span class="brand-sub">Democracia Institucional Segura</span>
                </div>
            </a>
        </div>

        <div class="dev-credits-pill">
            <span>✨ Autoras:</span>
            <strong>Andrea Mart&iacute;nez Cruz</strong> & <strong>Doris Yasm&iacute;n L&oacute;pez Chocont&aacute;</strong>
        </div>

        <nav class="nav-links">
            <a href="<?= $baseUrl ?>index.php" class="nav-link <?= empty($adminPage) && basename($_SERVER['PHP_SELF']) === 'index.php' ? 'active-link' : '' ?>">
                📋 Consultas
            </a>
            <?php if (is_admin()): ?>
                <a href="<?= $baseUrl ?>admin/dashboard.php" class="nav-link <?= !empty($adminPage) ? 'active-link' : '' ?>">
                    ⚙️ Panel Electoral
                </a>
            <?php elseif (is_logged_in()): ?>
                <a href="<?= $baseUrl ?>votar.php" class="nav-link <?= basename($_SERVER['PHP_SELF']) === 'votar.php' ? 'active-link' : '' ?>">
                    🗳️ Mi Votación
                </a>
            <?php endif; ?>

            <?php if (is_logged_in()): ?>
                <?php $u = current_user(); ?>
                <div class="user-badge <?= is_admin() ? 'badge-admin' : 'badge-voter' ?>">
                    <span><?= is_admin() ? '⚙️' : '🎓' ?></span>
                    <span><?= h($u['nombre'] ?? $u['correo'] ?? 'Usuario') ?></span>
                    <span class="user-role-pill"><?= h($u['rol'] ?? 'VOTANTE') ?></span>
                </div>
                <a href="<?= $baseUrl ?>logout.php" class="btn-logout" title="Cerrar sesión">Salir</a>
            <?php else: ?>
                <a href="<?= $baseUrl ?>login.php" class="btn btn-sm">Iniciar Sesión</a>
            <?php endif; ?>
        </nav>
    </div>
</header>
<main class="container">
