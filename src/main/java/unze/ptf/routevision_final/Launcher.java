package unze.ptf.routevision_final;

import javafx.application.Application;
import unze.ptf.routevision_final.config.DatabaseConfig;
import java.sql.Connection;

public class Launcher {
    public static void main(String[] args) {

        // 1. TEST BAZE
        try (Connection conn = DatabaseConfig.getConnection()) {
            System.out.println("✔ Konekcija uspješna!");
        } catch (Exception e) {
            System.out.println(" Greška u konekciji:");
            e.printStackTrace();
        }


    }
}
