package unze.ptf.routevision_final.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Konekcija sa cloud bazom podataka (Aiven MySQL) – brzo rješenje za test/demo
public class DatabaseConfig {

    // URL baze – SSL uključeno, ali certifikat se ne provjerava
    private static final String URL =
            "jdbc:mysql://mysql-routevision-routevisiondb.l.aivencloud.com:15856/AivenCloud"
                    + "?useSSL=true"
                    + "&verifyServerCertificate=false"; // ⚠️ ne provjerava certifikat, ali radi za demo

    private static final String USER = "avnadmin";
    private static final String PASSWORD = "";

    // Učitavanje MySQL JDBC drajvera
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    // Metoda koja vraća konekciju na bazu
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
