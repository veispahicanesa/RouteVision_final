package unze.ptf.routevision_final.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
// konekcija sa bazom podataka

public class DatabaseConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/bazapodataka";
    private static final String USER = "root";
    private static final String PASSWORD = "Minela1!";
//Static blok se izvršava prilikom učitavanja klase.
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

//vraća aktivnu konekciju sa bazom podataka.
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
