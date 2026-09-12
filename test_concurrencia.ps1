# =====================================================================
# SVIS - PRUEBA DE CONCURRENCIA Y CONTROL DE CONDICIÓN DE CARRERA (REGLA 3)
# Desarrollado por: Andrea Martínez Cruz & Doris Yasmín López Chocontá
# =====================================================================

$endpoint = "http://localhost:8080/svis-backend/api/votos/emitir"

Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host "  SVIS - PRUEBA DE ESTRES Y CONCURRENCIA ACID (REGLA 3)          " -ForegroundColor Yellow
Write-Host "=================================================================" -ForegroundColor Cyan

# 1. Asegurar un token disponible para la prueba en MySQL
$tokenPrueba = "OTP-TEST-CONCURRENCIA-" + (Get-Random -Minimum 1000 -Maximum 9999)
$encuestaId = 1
$opcionId = 1

Write-Host "[1/4] Preparando Token OTP de prueba en MySQL (sistemavotaciones)..." -ForegroundColor White
$sqlSetup = "USE sistemavotaciones; INSERT INTO tokens (encuesta_id, usuario_id, token, estado, fecha_expiracion) VALUES ($encuestaId, 2, '$tokenPrueba', 'DISPONIBLE', DATE_ADD(NOW(), INTERVAL 1 HOUR)) ON DUPLICATE KEY UPDATE token = '$tokenPrueba', estado = 'DISPONIBLE', fecha_uso = NULL;"
$sqlSetup | mysql -u root

Write-Host "  -> Token asignado: $tokenPrueba" -ForegroundColor Green
Write-Host "  -> Opcion a votar: ID $opcionId" -ForegroundColor Green

# 2. Preparar cuerpo JSON
$payload = @{
    token = $tokenPrueba
    opcionId = $opcionId
} | ConvertTo-Json

# 3. Disparar dos peticiones en paralelo usando Start-Job
Write-Host ""
Write-Host "[2/4] Disparando 2 peticiones HTTP POST simultaneas con el mismo token OTP..." -ForegroundColor White

$jobBlock = {
    param($url, $body)
    try {
        $res = Invoke-WebRequest -Uri $url -Method Post -Body $body -ContentType "application/json; charset=utf-8" -UseBasicParsing
        return [PSCustomObject]@{
            Status = [int]$res.StatusCode
            Body   = $res.Content
        }
    } catch [System.Net.WebException] {
        $webEx = $_.Exception
        $code = 500
        $bodyText = $webEx.Message
        if ($webEx.Response) {
            $code = [int]$webEx.Response.StatusCode
            $reader = New-Object System.IO.StreamReader($webEx.Response.GetResponseStream())
            $bodyText = $reader.ReadToEnd()
            $reader.Close()
        }
        return [PSCustomObject]@{
            Status = $code
            Body   = $bodyText
        }
    } catch {
        return [PSCustomObject]@{
            Status = 500
            Body   = $_.Exception.Message
        }
    }
}

$job1 = Start-Job -ScriptBlock $jobBlock -ArgumentList $endpoint, $payload
$job2 = Start-Job -ScriptBlock $jobBlock -ArgumentList $endpoint, $payload

$null = Wait-Job $job1, $job2

$res1 = Receive-Job $job1
$res2 = Receive-Job $job2

Remove-Job $job1, $job2 -Force

# 4. Mostrar Resultados
Write-Host ""
Write-Host "[3/4] RESULTADOS OBTENIDOS DEL BACKEND JAVA:" -ForegroundColor White
Write-Host "-----------------------------------------------------------------"

$color1 = if ($res1.Status -eq 200) { "Green" } else { "Yellow" }
Write-Host "Peticion 1 (Pestana A): Status HTTP = $($res1.Status)" -ForegroundColor $color1
Write-Host "  Respuesta: $($res1.Body)" -ForegroundColor Gray

Write-Host ""
$color2 = if ($res2.Status -eq 200) { "Green" } else { "Yellow" }
Write-Host "Peticion 2 (Pestana B): Status HTTP = $($res2.Status)" -ForegroundColor $color2
Write-Host "  Respuesta: $($res2.Body)" -ForegroundColor Gray

Write-Host "-----------------------------------------------------------------"

# 5. Evaluación de Aprobación
$codigos = @($res1.Status, $res2.Status)
$unExito = ($codigos -contains 200)
$unConflicto = ($codigos -contains 409)

Write-Host ""
Write-Host "[4/4] DICTAMEN DE EVALUACION TECNICA:" -ForegroundColor White
if ($unExito -and $unConflicto) {
    Write-Host ">>> PRUEBA SUPERADA CON EXITO (APROBADO) <<<" -ForegroundColor Green
    Write-Host "El motor transaccional JDBC con SELECT ... FOR UPDATE bloqueo la fila" -ForegroundColor Green
    Write-Host "correctamente. Una peticion obtuvo HTTP 200 OK y la concurrente fue" -ForegroundColor Green
    Write-Host "rechazada inmediatamente con HTTP 409 Conflict (REGLA 3 e Integridad ACID)." -ForegroundColor Green
} else {
    Write-Host ">>> PRUEBA NO SUPERADA: Se obtuvieron codigos [$($codigos -join ', ')] <<<" -ForegroundColor Red
}
Write-Host "=================================================================" -ForegroundColor Cyan
