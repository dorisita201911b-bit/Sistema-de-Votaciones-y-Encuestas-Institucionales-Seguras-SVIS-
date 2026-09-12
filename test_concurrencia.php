<?php
/**
 * =====================================================================
 * SVIS - PRUEBA DE CONCURRENCIA Y CONDICIÓN DE CARRERA (PHP CLI)
 * Desarrollado por: Andrea Martínez Cruz & Doris Yasmín López Chocontá
 * =====================================================================
 * Dispara 2 peticiones concurrentes en paralelo con curl_multi_exec
 * hacia el endpoint /api/votos/emitir con el MISMO token OTP.
 * =====================================================================
 */

$endpoint = 'http://localhost:8080/svis-backend/api/votos/emitir';
$tokenPrueba = 'OTP-PHP-CONCURRENCIA-' . rand(1000, 9999);
$encuestaId = 1;
$opcionId = 1;

echo "=================================================================\n";
echo "  SVIS · PRUEBA DE CONCURRENCIA ACID EN PHP (REGLA 3)           \n";
echo "=================================================================\n";

// 1. Preparar token disponible en la base de datos MySQL
$mysqli = new mysqli('127.0.0.1', 'root', '', 'sistemavotaciones');
if ($mysqli->connect_errno) {
    die("Error conectando a MySQL: " . $mysqli->connect_error . "\n");
}

$mysqli->query("INSERT INTO tokens (encuesta_id, usuario_id, token, estado, fecha_expiracion) 
                VALUES ($encuestaId, 3, '$tokenPrueba', 'DISPONIBLE', DATE_ADD(NOW(), INTERVAL 1 HOUR)) 
                ON DUPLICATE KEY UPDATE token = '$tokenPrueba', estado = 'DISPONIBLE', fecha_uso = NULL");
$mysqli->close();

echo "[1/3] Token OTP de prueba asignado: $tokenPrueba\n";
echo "[2/3] Disparando 2 peticiones concurrentes simultáneas con curl_multi...\n\n";

$payload = json_encode([
    'token' => $tokenPrueba,
    'opcionId' => $opcionId,
]);

$mh = curl_multi_init();

$ch1 = curl_init($endpoint);
curl_setopt_array($ch1, [
    CURLOPT_POST => true,
    CURLOPT_POSTFIELDS => $payload,
    CURLOPT_HTTPHEADER => ['Content-Type: application/json', 'Accept: application/json'],
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_TIMEOUT => 10,
]);
curl_multi_add_handle($mh, $ch1);

$ch2 = curl_init($endpoint);
curl_setopt_array($ch2, [
    CURLOPT_POST => true,
    CURLOPT_POSTFIELDS => $payload,
    CURLOPT_HTTPHEADER => ['Content-Type: application/json', 'Accept: application/json'],
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_TIMEOUT => 10,
]);
curl_multi_add_handle($mh, $ch2);

// Ejecutar en paralelo
$running = null;
do {
    $status = curl_multi_exec($mh, $running);
    if ($running) {
        curl_multi_select($mh);
    }
} while ($running > 0 && $status === CURLM_OK);

$code1 = curl_getinfo($ch1, CURLINFO_HTTP_CODE);
$resp1 = curl_multi_getcontent($ch1);

$code2 = curl_getinfo($ch2, CURLINFO_HTTP_CODE);
$resp2 = curl_multi_getcontent($ch2);

curl_multi_remove_handle($mh, $ch1);
curl_multi_remove_handle($mh, $ch2);
curl_multi_close($mh);

curl_close($ch1);
curl_close($ch2);

echo "Petición 1: HTTP $code1\n";
echo "  Respuesta: $resp1\n\n";

echo "Petición 2: HTTP $code2\n";
echo "  Respuesta: $resp2\n\n";

echo "-----------------------------------------------------------------\n";
echo "[3/3] DICTAMEN DE EVALUACIÓN:\n";
$codigos = [$code1, $code2];
if (in_array(200, $codigos) && in_array(409, $codigos)) {
    echo ">>> PRUEBA SUPERADA CON ÉXITO (APROBADO) <<<\n";
    echo "Una petición obtuvo 200 OK y la concurrente obtuvo 409 Conflict.\n";
    echo "Control de concurrencia pesimista (SELECT FOR UPDATE) funcionando al 100%.\n";
} else {
    echo ">>> PRUEBA NO SUPERADA: Códigos recibidos: " . implode(', ', $codigos) . " <<<\n";
}
echo "=================================================================\n";
