package unze.ptf.routevision_final.repository;

import unze.ptf.routevision_final.config.DatabaseConfig;
import unze.ptf.routevision_final.model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.*;

// DAO klasa za pristup tabeli 'admin'. Sadrži metode za dohvat,
// spremanje i mapiranje Admin objekata iz baze podataka.
public class AdminDAO {
    public Admin findByEmail(String email) throws SQLException {
        String query = "SELECT * FROM admin WHERE email = ? AND aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToAdmin(rs);
            }
        }
        return null;
    }

    public Admin findById(int id) throws SQLException {
        String query = "SELECT * FROM admin WHERE id = ? AND aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToAdmin(rs);
            }
        }
        return null;
    }

    public void save(Admin admin) throws SQLException {
        String query = "INSERT INTO admin (ime, prezime, email, lozinka, broj_telefona, aktivan) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, admin.getIme());
            stmt.setString(2, admin.getPrezime());
            stmt.setString(3, admin.getEmail());
            stmt.setString(4, admin.getLozinka());
            stmt.setString(5, admin.getBroj_telefona());
            stmt.setBoolean(6, admin.isAktivan());
            stmt.executeUpdate();
        }
    }

    private Admin mapResultSetToAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setId(rs.getInt("id"));
        admin.setIme(rs.getString("ime"));
        admin.setPrezime(rs.getString("prezime"));
        admin.setEmail(rs.getString("email"));
        admin.setLozinka(rs.getString("lozinka"));
        admin.setBroj_telefona(rs.getString("broj_telefona"));
        admin.setAktivan(rs.getBoolean("aktivan"));
        admin.setDatum_kreiranja(rs.getTimestamp("datum_kreiranja").toLocalDateTime());
        return admin;
    }
}
