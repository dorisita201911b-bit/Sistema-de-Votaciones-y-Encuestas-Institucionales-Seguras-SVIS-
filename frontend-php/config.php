<?php
/**
 * Configuracion del frontend PHP.
 * API_BASE_URL apunta al backend Java desplegado en Tomcat.
 */
define('API_BASE_URL', getenv('API_BASE_URL') ?: 'http://localhost:8080/eventos-api/api');
