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
 * Verifica si hay una sesion iniciada.
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
    $u = current_user();
    return $u && ($u['rol'] ?? '') === 'ADMIN';
}

/**
 * Exige rol de Administrador. Si no lo tiene, redirige a login.
 */
function require_admin()
{
    if (!is_admin()) {
        header('Location: login.php?msg=admin_required');
        exit;
    }
}

/**
 * Exige iniciar sesion. Si no esta autenticado, redirige a login.
 */
function require_login()
{
    if (!is_logged_in()) {
        header('Location: login.php?msg=login_required');
        exit;
    }
}

/**
 * Intenta autenticar un usuario por nombre de usuario o documento.
 */
function login($identificador, $password)
{
    return api()->post('/auth/login', [
        'usuario' => $identificador,
        'password' => $password,
    ]);
}

/**
 * Cierra la sesion actual.
 */
function logout()
{
    $_SESSION['user'] = null;
    $_SESSION['access_token'] = null;
    unset($_SESSION['user']);
    unset($_SESSION['access_token']);
    session_destroy();
}

/**
 * Registra un nuevo usuario con rol USUARIO.
 */
function registrar_usuario($username, $password, $nombre, $documento, $email, $telefono)
{
    return api()->post('/auth/registro', [
        'username' => $username,
        'password' => $password,
        'nombre' => $nombre,
        'documento' => $documento,
        'email' => $email,
        'telefono' => $telefono,
    ]);
}
