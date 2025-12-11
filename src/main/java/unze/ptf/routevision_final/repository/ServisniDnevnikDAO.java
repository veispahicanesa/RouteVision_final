package unze.ptf.routevision_final.repository;

import unze.ptf.routevision_final.config.DatabaseConfig;
import unze.ptf.routevision_final.model.ServisniDnevnik;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServisniDnevnikDAO {
    public ServisniDnevnik findById(int id) throws SQLException {
        String query = "SELECT * FROM servisni_dnevnik WHERE id = ? AND aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapResultSetToServisniDnevnik(rs);
        }
        return null;
    }

    public List<ServisniDnevnik> findAll() throws SQLException {
        List<ServisniDnevnik> servisi = new ArrayList<>();
        String query = "SELECT * FROM servisni_dnevnik WHERE aktivan = TRUE ORDER BY datum_servisa DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) servisi.add(mapResultSetToServisniDnevnik(rs));
        }
        return servisi;
    }

    public List<ServisniDnevnik> findByKamionId(int kamionId) throws SQLException {
        List<ServisniDnevnik> servisi = new ArrayList<>();
        String query = "SELECT * FROM servisni_dnevnik WHERE kamion_id = ? AND aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, kamionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) servisi.add(mapResultSetToServisniDnevnik(rs));
        }
        return servisi;
    }

    public void save(ServisniDnevnik servisi) throws SQLException {
        String query = "INSERT INTO servisni_dnevnik (kamion_id, vozac_id, datum_servisa, vrsta_servisa, opisServisa, km_na_servisu, troskovi, serviser_naziv, napomena, aktivan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, servisi.getKamion_id());
            stmt.setObject(2, servisi.getVozac_id());
            stmt.setDate(3, Date.valueOf(servisi.getDatum_servisa()));
            stmt.setString(4, servisi.getVrsta_servisa());
            stmt.setString(5, servisi.getOpisServisa());
            stmt.setInt(6, servisi.getKm_na_servisu());
            stmt.setDouble(7, servisi.getTroskovi());
            stmt.setString(8, servisi.getServiser_naziv());
            stmt.setString(9, servisi.getNapomena());
            stmt.setBoolean(10, servisi.isAktivan());
            stmt.executeUpdate();
        }
    }

    public void update(ServisniDnevnik servisi) throws SQLException {
        String query = "UPDATE servisni_dnevnik SET vrsta_servisa = ?, opisServisa = ?, troskovi = ?, serviser_naziv = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, servisi.getVrsta_servisa());
            stmt.setString(2, servisi.getOpisServisa());
            stmt.setDouble(3, servisi.getTroskovi());
            stmt.setString(4, servisi.getServiser_naziv());
            stmt.setInt(5, servisi.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String query = "UPDATE servisni_dnevnik SET aktivan = FALSE WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private ServisniDnevnik mapResultSetToServisniDnevnik(ResultSet rs) throws SQLException {
        ServisniDnevnik dnevnik = new ServisniDnevnik();
        dnevnik.setId(rs.getInt("id"));
        dnevnik.setKamion_id(rs.getInt("kamion_id"));
        dnevnik.setVozac_id(rs.getObject("vozac_id") != null ? rs.getInt("vozac_id") : null);
        dnevnik.setDatum_servisa(rs.getDate("datum_servisa").toLocalDate());
        dnevnik.setVrsta_servisa(rs.getString("vrsta_servisa"));
        dnevnik.setOpisServisa(rs.getString("opisServisa"));
        dnevnik.setKm_na_servisu(rs.getInt("km_na_servisu"));
        dnevnik.setTroskovi(rs.getDouble("troskovi"));
        dnevnik.setServiser_naziv(rs.getString("serviser_naziv"));
        dnevnik.setNapomena(rs.getString("napomena"));
        dnevnik.setDatoteka_path(rs.getString("datoteka_path"));
        dnevnik.setAktivan(rs.getBoolean("aktivan"));
        dnevnik.setDatum_kreiranja(rs.getTimestamp("datum_kreiranja") != null ? rs.getTimestamp("datum_kreiranja").toLocalDateTime() : null);
        return dnevnik;
    }
}
