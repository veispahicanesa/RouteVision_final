package unze.ptf.routevision_final.repository;

import unze.ptf.routevision_final.config.DatabaseConfig;
import unze.ptf.routevision_final.model.Kamion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KamionDAO {
    public Kamion findById(int id) throws SQLException {
        String query = "SELECT * FROM kamion WHERE id = ? AND aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapResultSetToKamion(rs);
        }
        return null;
    }

    public List<Kamion> findAll() throws SQLException {
        List<Kamion> kamioni = new ArrayList<>();
        String query = "SELECT * FROM kamion WHERE aktivan = TRUE ORDER BY registarska_tablica";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) kamioni.add(mapResultSetToKamion(rs));
        }
        return kamioni;
    }

    public List<Kamion> findByVozacId(int vozacId) throws SQLException {
        List<Kamion> kamioni = new ArrayList<>();
        // Upit koji traži kamion koji je dodijeljen vozaču u tabeli vozac
        String query = "SELECT k.* FROM kamion k " +
                "JOIN vozac v ON v.kamion_id = k.id " +
                "WHERE v.id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, vozacId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Kamion k = new Kamion();
                k.setId(rs.getInt("id"));
                k.setRegistarska_tablica(rs.getString("registarska_tablica"));
                k.setMarka(rs.getString("marka"));
                k.setModel(rs.getString("model"));
                k.setStanje_kilometra(rs.getInt("stanje_kilometra"));
                kamioni.add(k);
            }
        }
        return kamioni;
    }
    public void save(Kamion kamion) throws SQLException {
        String query = "INSERT INTO kamion (registarska_tablica, marka, model, godina_proizvodnje, kapacitet_tone, vrsta_voza, stanje_kilometra, datum_registracije, datum_zakljucnog_pregleda, vozac_id, aktivan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, kamion.getRegistarska_tablica());
            stmt.setString(2, kamion.getMarka());
            stmt.setString(3, kamion.getModel());
            stmt.setInt(4, kamion.getGodina_proizvodnje());
            stmt.setDouble(5, kamion.getKapacitet_tone());
            stmt.setString(6, kamion.getVrsta_voza());
            stmt.setInt(7, kamion.getStanje_kilometra());
            stmt.setDate(8, kamion.getDatum_registracije() != null ? Date.valueOf(kamion.getDatum_registracije()) : null);
            stmt.setDate(9, kamion.getDatum_zakljucnog_pregleda() != null ? Date.valueOf(kamion.getDatum_zakljucnog_pregleda()) : null);
            stmt.setObject(10, kamion.getVozac_id());
            stmt.setBoolean(11, true);
            stmt.executeUpdate();
        }
    }

    public void update(Kamion kamion) throws SQLException {
        String query = "UPDATE kamion SET marka = ?, model = ?, godina_proizvodnje = ?, kapacitet_tone = ?, vrsta_voza = ?, stanje_kilometra = ?, vozac_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, kamion.getMarka());
            stmt.setString(2, kamion.getModel());
            stmt.setInt(3, kamion.getGodina_proizvodnje());
            stmt.setDouble(4, kamion.getKapacitet_tone());
            stmt.setString(5, kamion.getVrsta_voza());
            stmt.setInt(6, kamion.getStanje_kilometra());
            stmt.setObject(7, kamion.getVozac_id());
            stmt.setInt(8, kamion.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String query = "UPDATE kamion SET aktivan = FALSE WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Kamion mapResultSetToKamion(ResultSet rs) throws SQLException {
        Kamion k = new Kamion();
        k.setId(rs.getInt("id"));
        k.setRegistarska_tablica(rs.getString("registarska_tablica"));
        k.setMarka(rs.getString("marka"));
        k.setModel(rs.getString("model"));
        k.setGodina_proizvodnje(rs.getInt("godina_proizvodnje"));
        k.setKapacitet_tone(rs.getDouble("kapacitet_tone"));
        k.setVrsta_voza(rs.getString("vrsta_voza"));
        k.setStanje_kilometra(rs.getInt("stanje_kilometra"));
        k.setDatum_registracije(rs.getDate("datum_registracije") != null ? rs.getDate("datum_registracije").toLocalDate() : null);
        k.setVozac_id(rs.getObject("vozac_id") != null ? rs.getInt("vozac_id") : null);
        k.setAktivan(rs.getBoolean("aktivan"));
        return k;
    }
}