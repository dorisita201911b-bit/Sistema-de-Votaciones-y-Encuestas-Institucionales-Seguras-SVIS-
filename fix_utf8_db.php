<?php
/**
 * Script para corregir codificación UTF-8 en MySQL sistemavotaciones
 */
$pdo = new PDO('mysql:host=localhost;dbname=sistemavotaciones;charset=utf8mb4', 'root', '', [
    PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
    PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci"
]);

$queries = [
    "ALTER DATABASE sistemavotaciones CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",
    "ALTER TABLE usuarios CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",
    "ALTER TABLE encuestas CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",
    "ALTER TABLE opciones CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",
    "ALTER TABLE tokens CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",
    "ALTER TABLE votos CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",

    "UPDATE usuarios SET nombre = 'Andrea Martínez Cruz' WHERE id = 1",
    "UPDATE usuarios SET nombre = 'Carlos Andrés Mendoza Rivas' WHERE id = 2",
    "UPDATE usuarios SET nombre = 'María Fernanda Gómez Castro' WHERE id = 3",
    "UPDATE usuarios SET nombre = 'Juan David Rodríguez Mora' WHERE id = 4",
    "UPDATE usuarios SET nombre = 'Laura Valentina Peña Silva' WHERE id = 5",
    "UPDATE usuarios SET nombre = 'Doris Yasmín López Chocontá' WHERE id = 6",

    "UPDATE encuestas SET titulo = 'Elección de Representante de Aprendices ADSO 2026', descripcion = 'Jornada democrática institucional para elegir el vocero principal de la ficha ADSO ante el comité académico.' WHERE id = 1",

    "UPDATE opciones SET nombre = 'Candidato 1: Daniel Ospina - Lista A (Semilleros e Innovación)' WHERE id = 1",
    "UPDATE opciones SET nombre = 'Candidato 2: Sofía Herrera - Lista B (Tutorías y Bienestar)' WHERE id = 2",
    "UPDATE opciones SET nombre = 'Voto en Blanco Institucional' WHERE id = 3"
];

foreach ($queries as $q) {
    $pdo->exec($q);
    echo "Ejecutado: " . substr($q, 0, 50) . "...\n";
}

echo "\n--- VERIFICACIÓN DE USUARIOS ---\n";
$stmt = $pdo->query("SELECT id, nombre, correo FROM usuarios ORDER BY id");
while ($r = $stmt->fetch(PDO::FETCH_ASSOC)) {
    echo "ID: {$r['id']} | Nombre: {$r['nombre']} | Correo: {$r['correo']}\n";
}

echo "\n--- VERIFICACIÓN DE ENCUESTAS ---\n";
$stmt = $pdo->query("SELECT id, titulo FROM encuestas");
while ($r = $stmt->fetch(PDO::FETCH_ASSOC)) {
    echo "ID: {$r['id']} | Titulo: {$r['titulo']}\n";
}
