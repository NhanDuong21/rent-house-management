package Utils.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Author: Duong Thien Nhan - CE190741 Created on: 2026-01-12
 */
public class DBContext {

    protected Connection connection;

    public DBContext() {
        String url = "jdbc:sqlserver://localhost:1433;"
                + "databaseName=RentHouse;"
                + "user=sa;"
                + "password=123456;"
                + "encrypt=true;"
                + "trustServerCertificate=true;";
        try {
            // Ensure SQL Server driver is loaded when the container doesn't auto-load it
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQL Server JDBC driver not found. Ensure mssql-jdbc is on the classpath.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to SQL Server. Check URL, credentials, and DB availability.", e);
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

}
