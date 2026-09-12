<?php
/**
 * Configuración del frontend PHP - SVIS.
 * API_BASE_URL apunta al backend Java desplegado en Tomcat.
 */
ini_set('default_charset', 'UTF-8');
mb_internal_encoding('UTF-8');
if (!headers_sent()) {
    header('Content-Type: text/html; charset=UTF-8');
}
define('API_BASE_URL', getenv('API_BASE_URL') ?: 'http://localhost:8080/svis-backend/api');
