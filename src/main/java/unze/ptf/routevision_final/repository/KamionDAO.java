package unze.ptf.routevision_final.repository;
import unze.ptf.routevision_final.config.DatabaseConfig;
import unze.ptf.routevision_final.model.Kamion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/*
 * KamionDAO - klasa koja upravlja CRUD operacijama nad tabelom kamion u bazi.
 * Omogućava pronalazak, dodavanje, ažuriranje i brisanje kamiona.
 */
public class KamionDAO {
    public Kamion findById(int id) throws SQLException {
        String query = "SELECT * FROM kamion WHERE id = ? AND aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToKamion(rs);
            }
        }
        return null;
    }

    public List<Kamion> findAll() throws SQLException {
        List<Kamion> kamioni = new ArrayList<>();
        String query = "SELECT * FROM kamion WHERE aktivan = TRUE ORDER BY registarska_tablica";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                kamioni.add(mapResultSetToKamion(rs));
            }
        }
        return kamioni;
    }

    public List<Kamion> findByVozacId(int vozacId) throws SQLException {
        List<Kamion> kamioni = new ArrayList<>();
        String query = "SELECT * FROM kamion WHERE vozac_id = ? AND aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, vozacId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                kamioni.add(mapResultSetToKamion(rs));
            }
        }
        return kamioni;
    }

    public void save(Kamion kamion) throws SQLException {
        String query = "INSERT INTO kamion (registarska_tablica, marka, model, godina_proizvodnje, kapacitet_tone, vrsta_voza, stanje_kilometra, datum_registracije, datum_zakljucnog_pregleda, vozac_id, aktivan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
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
            stmt.setBoolean(11, kamion.isAktivan());
            stmt.executeUpdate();
        }
    }

    public void update(Kamion kamion) throws SQLException {
        String query = "UPDATE kamion SET marka = ?, model = ?, godina_proizvodnje = ?, kapacitet_tone = ?, vrsta_voza = ?, stanje_kilometra = ?, datum_registracije = ?, datum_zakljucnog_pregleda = ?, vozac_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, kamion.getMarka());
            stmt.setString(2, kamion.getModel());
            stmt.setInt(3, kamion.getGodina_proizvodnje());
            stmt.setDouble(4, kamion.getKapacitet_tone());
            stmt.setString(5, kamion.getVrsta_voza());
            stmt.setInt(6, kamion.getStanje_kilometra());
            stmt.setDate(7, kamion.getDatum_registracije() != null ? Date.valueOf(kamion.getDatum_registracije()) : null);
            stmt.setDate(8, kamion.getDatum_zakljucnog_pregleda() != null ? Date.valueOf(kamion.getDatum_zakljucnog_pregleda()) : null);
            stmt.setObject(9, kamion.getVozac_id());
            stmt.setInt(10, kamion.getId());
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
        Kamion kamion = new Kamion();
        kamion.setId(rs.getInt("id"));
        kamion.setRegistarska_tablica(rs.getString("registarska_tablica"));
        kamion.setMarka(rs.getString("marka"));
        kamion.setModel(rs.getString("model"));
        kamion.setGodina_proizvodnje(rs.getInt("godina_proizvodnje"));
        kamion.setKapacitet_tone(rs.getDouble("kapacitet_tone"));
        kamion.setVrsta_voza(rs.getString("vrsta_voza"));
        kamion.setStanje_kilometra(rs.getInt("stanje_kilometra"));
        kamion.setDatum_registracije(rs.getDate("datum_registracije") != null ? rs.getDate("datum_registracije").toLocalDate() : null);
        kamion.setDatum_zakljucnog_pregleda(rs.getDate("datum_zakljucnog_pregleda") != null ? rs.getDate("datum_zakljucnog_pregleda").toLocalDate() : null);
        kamion.setVozac_id(rs.getObject("vozac_id") != null ? rs.getInt("vozac_id") : null);
        kamion.setAktivna_slika(rs.getString("aktivna_slika"));
        kamion.setAktivan(rs.getBoolean("aktivan"));
        kamion.setDatum_kreiranja(rs.getTimestamp("datum_kreiranja") != null ? rs.getTimestamp("datum_kreiranja").toLocalDateTime() : null);
        return kamion;
    }
}