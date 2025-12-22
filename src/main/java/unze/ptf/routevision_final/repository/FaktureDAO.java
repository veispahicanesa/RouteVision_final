package unze.ptf.routevision_final.repository;

import unze.ptf.routevision_final.config.DatabaseConfig;
import unze.ptf.routevision_final.model.Fakture;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FaktureDAO {

    // Za Admina - vidi sve iz tabele fakture
    public List<Fakture> findAll() throws SQLException {
        List<Fakture> lista = new ArrayList<>();
        String query = "SELECT * FROM fakture WHERE aktivan = TRUE ORDER BY datum_izdavanja DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapResultSetToFakture(rs));
            }
        }
        return lista;
    }

    // Za Vozača - vidi samo fakture vezane za njegove ture (JOIN sa tura)
    public List<Fakture> findForVozac(int vozacId) throws SQLException {
        List<Fakture> lista = new ArrayList<>();
        String query = "SELECT f.* FROM fakture f " +
                "JOIN tura t ON f.tura_id = t.id " +
                "WHERE t.vozac_id = ? AND f.aktivan = TRUE " +
                "ORDER BY f.datum_izdavanja DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, vozacId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapResultSetToFakture(rs));
            }
        }
        return lista;
    }
    // Dodaj ovu metodu u svoju FaktureDAO klasu:
    public void updateStatus(int id, String status) throws SQLException {
        String query = "UPDATE fakture SET status_placanja = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }
    public void save(Fakture f) throws SQLException {
        String query = "INSERT INTO fakture (broj_fakture, tura_id, klijent_id, datum_izdavanja, datum_dospjeća, " +
                "vrsta_usluge, ukupan_iznos, status_placanja, aktivan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, f.getBroj_fakture());
            stmt.setInt(2, f.getTura_id());
            stmt.setInt(3, f.getKlijent_id());
            stmt.setDate(4, f.getDatum_izdavanja() != null ? Date.valueOf(f.getDatum_izdavanja()) : null);
            stmt.setDate(5, f.getDatum_dospjeca() != null ? Date.valueOf(f.getDatum_dospjeca()) : null);
            stmt.setString(6, f.getVrsta_usluge());
            stmt.setDouble(7, f.getUkupan_iznos());
            stmt.setString(8, f.getStatus_placanja());
            stmt.setBoolean(9, true);
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String query = "UPDATE fakture SET aktivan = FALSE WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Fakture mapResultSetToFakture(ResultSet rs) throws SQLException {
        Fakture f = new Fakture();
        f.setId(rs.getInt("id"));
        f.setBroj_fakture(rs.getString("broj_fakture"));
        f.setTura_id(rs.getInt("tura_id"));
        f.setKlijent_id(rs.getInt("klijent_id"));

        if (rs.getDate("datum_izdavanja") != null)
            f.setDatum_izdavanja(rs.getDate("datum_izdavanja").toLocalDate());

        // Pazi na 'ć' u bazi podataka: datum_dospjeća
        if (rs.getDate("datum_dospjeća") != null)
            f.setDatum_dospjeca(rs.getDate("datum_dospjeća").toLocalDate());

        f.setVrsta_usluge(rs.getString("vrsta_usluge"));
        f.setUkupan_iznos(rs.getDouble("ukupan_iznos"));
        f.setStatus_placanja(rs.getString("status_placanja"));
        f.setAktivan(rs.getBoolean("aktivan"));
        return f;
    }
}