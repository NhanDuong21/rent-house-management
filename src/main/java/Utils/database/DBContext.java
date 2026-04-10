package Utils.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBContext {

    protected Connection connection;

    public DBContext() {
        String host = getEnv("DB_HOST", "localhost");
        String port = getEnv("DB_PORT", "1433");
        String dbName = getEnv("DB_NAME", "RentHouse");
        String user = getEnv("DB_USER", "sa");
        String password = getEnv("DB_PASSWORD", "12345678");

        String url = "jdbc:sqlserver://" + host + ":" + port + ";"
                + "databaseName=" + dbName + ";"
                + "user=" + user + ";"
                + "password=" + password + ";"
                + "encrypt=true;"
                + "trustServerCertificate=true;";

        try {
            System.out.println("[DB] Connecting to SQL Server host=" + host + " port=" + port + " db=" + dbName);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url);
            System.out.println("[DB] Connected successfully");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("SQL Server JDBC driver not found.", e);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to SQL Server: " + e.getMessage(), e);
        }
    }

    private String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    public Connection getConnection() {
        return this.connection;
    }
}