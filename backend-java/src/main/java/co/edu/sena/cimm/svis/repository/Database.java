package co.edu.sena.cimm.svis.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Proveedor único del pool de conexiones HikariCP para SVIS.
 * Configuración con prioridad:
 *   1. Variables de entorno (SVIS_DB_URL, SVIS_DB_USER, SVIS_DB_PASSWORD)
 *   2. Archivo db.properties en el classpath
 *   3. Valores por defecto (localhost:3306/svis_db)
 */
public final class Database {

    private static volatile DataSource dataSource;

    private Database() {
    }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (Database.class) {
                if (dataSource == null) {
                    dataSource = construir();
                }
            }
        }
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    private static DataSource construir() {
        Properties p = cargarPropiedades();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[SVIS Database] Advertencia: Driver com.mysql.cj.jdbc.Driver no encontrado en classpath");
        }

        HikariConfig cfg = new HikariConfig();
        cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");
        cfg.setJdbcUrl(valor(p, "db.url", "SVIS_DB_URL",
                "jdbc:mysql://localhost:3306/svis_db?serverTimezone=America/Bogota&useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8"));
        cfg.setUsername(valor(p, "db.user", "SVIS_DB_USER", "root"));
        cfg.setPassword(valor(p, "db.password", "SVIS_DB_PASSWORD", ""));
        cfg.setMaximumPoolSize(Integer.parseInt(valor(p, "db.poolSize", "SVIS_DB_POOL", "10")));
        cfg.setPoolName("svis-hikari-pool");
        cfg.setConnectionTimeout(10000);
        cfg.setIdleTimeout(60000);
        cfg.setMaxLifetime(1800000);

        return new HikariDataSource(cfg);
    }

    private static Properties cargarPropiedades() {
        Properties p = new Properties();
        try (InputStream in = Database.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                p.load(in);
            }
        } catch (IOException e) {
            // Se usarán variables de entorno o valores por defecto
        }
        return p;
    }

    private static String valor(Properties p, String clave, String envKey, String porDefecto) {
        String v = System.getenv(envKey);
        if (v == null || v.trim().isEmpty()) {
            v = p.getProperty(clave);
        }
        if (v == null || v.trim().isEmpty()) {
            v = porDefecto;
        }
        return v;
    }
}
