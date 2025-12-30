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
        // SQL upit koji filtrira kamione prema ID-u vozača koji ga je zadužio
        String query = "SELECT * FROM kamion WHERE zaduzeni_vozac_id = ? AND aktivan = TRUE";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, vozacId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // mapResultSetToKamion je metoda koja pretvara red iz baze u Kamion objekat
                // Na taj način dobijaš SVE podatke (marku, model, tablice, nosivost, itd.)
                kamioni.add(mapResultSetToKamion(rs));
            }
        }
        return kamioni;
    }
    public void save(Kamion kamion) throws SQLException {
        // ISPRAVLJENO: Koristi se 'zaduzeni_vozac_id' i broj parametara je usklađen (10 upitnika)
        String query = "INSERT INTO kamion (registarska_tablica, marka, model, godina_proizvodnje, kapacitet_tone, stanje_kilometra, datum_registracije, datum_zakljucnog_pregleda, zaduzeni_vozac_id, aktivan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, kamion.getRegistarska_tablica());
            stmt.setString(2, kamion.getMarka());
            stmt.setString(3, kamion.getModel());
            stmt.setInt(4, kamion.getGodina_proizvodnje());
            stmt.setDouble(5, kamion.getKapacitet_tone());
            stmt.setInt(6, kamion.getStanje_kilometra());
            stmt.setDate(7, kamion.getDatum_registracije() != null ? Date.valueOf(kamion.getDatum_registracije()) : null);
            stmt.setDate(8, kamion.getDatum_zakljucnog_pregleda() != null ? Date.valueOf(kamion.getDatum_zakljucnog_pregleda()) : null);
            stmt.setObject(9, kamion.getZaduzeni_vozac_id());
            stmt.setBoolean(10, true);
            stmt.executeUpdate();
        }
    }

    public void update(Kamion kamion) throws SQLException {
        // ISPRAVLJENO: Promijenjeno 'vozac_id' u 'zaduzeni_vozac_id' u UPDATE upitu
        String query = "UPDATE kamion SET marka = ?, model = ?, registarska_tablica = ?, godina_proizvodnje = ?, kapacitet_tone = ?, stanje_kilometra = ?, zaduzeni_vozac_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, kamion.getMarka());
            stmt.setString(2, kamion.getModel());
            stmt.setString(3, kamion.getRegistarska_tablica());
            stmt.setInt(4, kamion.getGodina_proizvodnje());
            stmt.setDouble(5, kamion.getKapacitet_tone());
            stmt.setInt(6, kamion.getStanje_kilometra());
            stmt.setObject(7, kamion.getZaduzeni_vozac_id());
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
        k.setStanje_kilometra(rs.getInt("stanje_kilometra"));

        if (rs.getDate("datum_registracije") != null) {
            k.setDatum_registracije(rs.getDate("datum_registracije").toLocalDate());
        }
        if (rs.getDate("datum_zakljucnog_pregleda") != null) {
            k.setDatum_zakljucnog_pregleda(rs.getDate("datum_zakljucnog_pregleda").toLocalDate());
        }

        // Koristi se zaduzeni_vozac_id kao u tvojoj bazi
        Object vId = rs.getObject("zaduzeni_vozac_id");
        if (vId != null) {
            k.setZaduzeni_vozac_id((Integer) vId);
        }

        k.setIme_vozaca(rs.getString("ime_vozaca"));
        k.setPrezime_vozaca(rs.getString("prezime_vozaca"));
        k.setAktivan(rs.getBoolean("aktivan"));
        return k;
    }
}