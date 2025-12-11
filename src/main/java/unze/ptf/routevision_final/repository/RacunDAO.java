package unze.ptf.routevision_final.repository;


import unze.ptf.routevision_final.config.DatabaseConfig;
import unze.ptf.routevision_final.model.Fakture;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RacunDAO {
    public Fakture findById(int id) throws SQLException {
        String query = "SELECT * FROM racun WHERE id = ? AND aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapResultSetToRacun(rs);
        }
        return null;
    }

    public List<Fakture> findAll() throws SQLException {
        List<Fakture> racuni = new ArrayList<>();
        String query = "SELECT * FROM racun WHERE aktivan = TRUE ORDER BY datum_izdavanja DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) racuni.add(mapResultSetToRacun(rs));
        }
        return racuni;
    }

    public void save(Fakture fakture) throws SQLException {
        String query = "INSERT INTO fakture (broj_racuna, putovanje_id, klijent_id, datum_izdavanja, vrsta_usluge, cijena_po_km, broj_km, iznos_usluge, porez, ukupan_iznos, status_placanja, aktivan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, fakture.getBroj_fakture());
            stmt.setInt(2, fakture.getTura_id());
            stmt.setInt(3, fakture.getKlijent_id());
            stmt.setDate(4, fakture.getDatum_izdavanja() != null ? Date.valueOf(fakture.getDatum_izdavanja()) : null);
            stmt.setString(5, fakture.getVrsta_usluge());
            stmt.setDouble(6, fakture.getCijena_po_km());
            stmt.setInt(7, fakture.getBroj_km());
            stmt.setDouble(8, fakture.getIznos_usluge());
            stmt.setDouble(9, fakture.getPorez());
            stmt.setDouble(10, fakture.getUkupan_iznos());
            stmt.setString(11, fakture.getStatus_placanja());
            stmt.setBoolean(12, fakture.isAktivan());
            stmt.executeUpdate();
        }
    }

    public void update(Fakture fakture) throws SQLException {
        String query = "UPDATE fakture SET status_placanja = ?, nacin_placanja = ?, datum_placanja = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, fakture.getStatus_placanja());
            stmt.setString(2, fakture.getNacin_placanja());
            stmt.setDate(3, fakture.getDatum_placanja() != null ? Date.valueOf(fakture.getDatum_placanja()) : null);
            stmt.setInt(4, fakture.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String query = "UPDATE racun SET aktivan = FALSE WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Fakture mapResultSetToRacun(ResultSet rs) throws SQLException {
        Fakture fakture = new Fakture();
        fakture.setId(rs.getInt("id"));
        fakture.setBroj_fakture(rs.getString("broj_racuna"));
        fakture.setTura_id(rs.getInt("putovanje_id"));
        fakture.setKlijent_id(rs.getInt("klijent_id"));
        fakture.setDatum_izdavanja(rs.getDate("datum_izdavanja") != null ? rs.getDate("datum_izdavanja").toLocalDate() : null);
        fakture.setDatum_dospjeća(rs.getDate("datum_dospjeća") != null ? rs.getDate("datum_dospjeća").toLocalDate() : null);
        fakture.setVrsta_usluge(rs.getString("vrsta_usluge"));
        fakture.setCijena_po_km(rs.getDouble("cijena_po_km"));
        fakture.setBroj_km(rs.getInt("broj_km"));
        fakture.setIznos_usluge(rs.getDouble("iznos_usluge"));
        fakture.setPorez(rs.getDouble("porez"));
        fakture.setUkupan_iznos(rs.getDouble("ukupan_iznos"));
        fakture.setStatus_placanja(rs.getString("status_placanja"));
        fakture.setNacin_placanja(rs.getString("nacin_placanja"));
        fakture.setDatum_placanja(rs.getDate("datum_placanja") != null ? rs.getDate("datum_placanja").toLocalDate() : null);
        fakture.setNapomena(rs.getString("napomena"));
        fakture.setDatoteka_path(rs.getString("datoteka_path"));
        fakture.setAktivan(rs.getBoolean("aktivan"));
        fakture.setDatum_kreiranja(rs.getTimestamp("datum_kreiranja") != null ? rs.getTimestamp("datum_kreiranja").toLocalDateTime() : null);
        return fakture;
    }
}
