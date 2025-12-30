package unze.ptf.routevision_final.repository;

import unze.ptf.routevision_final.config.DatabaseConfig;
import unze.ptf.routevision_final.model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

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
        String query = "INSERT INTO admin (ime, prezime, email, lozinka, broj_telefona, aktivan,plata,datum_kreiranja,datum_zaposlenja) VALUES (?, ?, ?, ?, ?, ?,?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, admin.getIme());
            stmt.setString(2, admin.getPrezime());
            stmt.setString(3, admin.getEmail());
            stmt.setString(4, admin.getLozinka());
            stmt.setString(5, admin.getBroj_telefona());
            stmt.setBoolean(6, admin.isAktivan());
            stmt.setDouble(7, admin.getPlata());
            stmt.setTimestamp(8, Timestamp.valueOf(admin.getDatum_kreiranja()));
            stmt.setTimestamp(9, Timestamp.valueOf(admin.getDatum_zaposlenja()));
            stmt.executeUpdate();
        }
    }
    // anesa dodala
    public void updatePassword(int adminId, String novaHashiranaLozinka) throws SQLException {
        String query = "UPDATE admin SET lozinka = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, novaHashiranaLozinka);
            stmt.setInt(2, adminId);
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
        if (rs.getTimestamp("datum_kreiranja") != null) {
            admin.setDatum_kreiranja(rs.getTimestamp("datum_kreiranja").toLocalDateTime());
        }
        if (rs.getTimestamp("datum_zaposlenja") != null) {
            admin.setDatum_zaposlenja(rs.getTimestamp("datum_zaposlenja").toLocalDateTime());
        }

        admin.setPlata(rs.getDouble("plata"));;
        return admin;
    }
    //dodala Anesa

    public void update(Admin admin) throws SQLException {
        String query = "UPDATE admin SET ime = ?, prezime = ?, broj_telefona = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, admin.getIme());
            stmt.setString(2, admin.getPrezime());
            stmt.setString(3, admin.getBroj_telefona());
            stmt.setInt(4, admin.getId());

            stmt.executeUpdate();
        }
    }

    //anesa doddala

    public List<Admin> findAll() throws SQLException {
        List<Admin> admini = new ArrayList<>();
        String query = "SELECT * FROM admin WHERE aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                admini.add(mapResultSetToAdmin(rs));
            }
        }
        return admini;
    }

    public void delete(int id) throws SQLException {
        // Radimo soft delete (samo gasimo korisnika, ne brišemo red iz baze skroz)
        String query = "UPDATE admin SET aktivan = FALSE WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
