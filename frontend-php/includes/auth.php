<?php
require_once __DIR__ . '/../config.php';
require_once __DIR__ . '/ApiClient.php';

if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

/**
 * Retorna los datos del usuario autenticado o null.
 */
function current_user()
{
    return $_SESSION['user'] ?? null;
}

/**
 * Verifica si hay una sesión activa.
 */
function is_logged_in()
{
    return !empty($_SESSION['user']);
}

/**
 * Verifica si el usuario actual tiene rol de Administrador.
 */
function is_admin()
{
    $rol = $_SESSION['rol'] ?? ($_SESSION['user']['rol'] ?? '');
    return strtoupper($rol) === 'ADMIN';
}

/**
 * Exige rol de Administrador. Si no lo tiene, redirige a login.
 */
function require_admin()
{
    if (!is_admin()) {
        header('Location: ' . (file_exists('login.php') ? 'login.php' : '../login.php') . '?msg=admin_required');
        exit;
    }
}

/**
 * Exige iniciar sesión. Si no está autenticado, redirige a login.
 */
function require_login()
{
    if (!is_logged_in()) {
        header('Location: ' . (file_exists('login.php') ? 'login.php' : '../login.php') . '?msg=login_required');
        exit;
    }
}

/**
 * Intenta autenticar un usuario por correo institucional y contraseña.
 */
function login($correo, $password)
{
    $res = api()->post('/auth/login', [
        'correo' => trim($correo),
        'password' => $password,
    ]);

    if ($res['ok'] && !empty($res['data'])) {
        $usuario = $res['data'];
        $_SESSION['user'] = $usuario;
        $_SESSION['rol'] = $usuario['rol'] ?? 'VOTANTE';
        $_SESSION['usuario_id'] = $usuario['id'] ?? null;
    }

    return $res;
}

/**
 * Cierra la sesión actual.
 */
function logout()
{
    $_SESSION = [];
    if (ini_get("session.use_cookies")) {
        $params = session_get_cookie_params();
        setcookie(session_name(), '', time() - 42000,
            $params["path"], $params["domain"],
            $params["secure"], $params["httponly"]
        );
    }
    session_destroy();
}
