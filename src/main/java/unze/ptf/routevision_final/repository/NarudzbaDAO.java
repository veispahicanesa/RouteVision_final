package unze.ptf.routevision_final.repository;

import unze.ptf.routevision_final.config.DatabaseConfig;
import unze.ptf.routevision_final.model.Narudzba;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/*
 * NarudzbaDAO - CRUD klasa za tabelu Narudzba.
 * Omogućava kreiranje, čitanje, ažuriranje i logičko brisanje narudžbi u bazi.
 */
public class NarudzbaDAO {

    public Narudzba findById(int id) throws SQLException {
        String query = "SELECT * FROM narudzba WHERE id = ? AND aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapResultSetToNarudzba(rs);
        }
        return null;
    }

    public List<Narudzba> findAll() throws SQLException {
        List<Narudzba> narudzbe = new ArrayList<>();
        String query = "SELECT n.*, k.naziv_firme " +
                "FROM narudzba n " +
                "JOIN klijent k ON n.klijent_id = k.id " +
                "WHERE n.aktivan = TRUE ORDER BY n.datum_narudzbe DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // 1. Mapiramo osnovne podatke iz RS u objekt
                Narudzba n = mapResultSetToNarudzba(rs);

                // 2. Ručno dodajemo naziv firme koji je došao preko JOIN-a
                // Ovo radimo UNUTAR while petlje dok je ResultSet još na tom redu
                n.setNazivKlijenta(rs.getString("naziv_firme"));

                // 3. Dodajemo potpuno popunjen objekt u listu
                narudzbe.add(n);
            }
        }
        return narudzbe;
    }

    public List<Narudzba> findByKlijentId(int klijentId) throws SQLException {
        List<Narudzba> narudzbe = new ArrayList<>();
        String query = "SELECT * FROM narudza WHERE klijent_id = ? AND aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, klijentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) narudzbe.add(mapResultSetToNarudzba(rs));
        }
        return narudzbe;
    }

    public void save(Narudzba narudzba) throws SQLException {
        String query = "INSERT INTO narudzba (broj_narudzbe, klijent_id, datum_narudzbe, datum_isporuke, vrsta_robe, kolicina, jedinica_mjere, lokacija_preuzimanja, lokacija_dostave, napomena, status, aktivan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, narudzba.getBroj_narudzbe());
            stmt.setInt(2, narudzba.getKlijent_id());
            stmt.setDate(3, narudzba.getDatum_narudzbe() != null ? Date.valueOf(narudzba.getDatum_narudzbe()) : null);
            stmt.setDate(4, narudzba.getDatum_isporuke() != null ? Date.valueOf(narudzba.getDatum_isporuke()) : null);
            stmt.setString(5, narudzba.getVrsta_robe());
            stmt.setDouble(6, narudzba.getKolicina());
            stmt.setString(7, narudzba.getJedinica_mjere());
            stmt.setString(8, narudzba.getLokacija_preuzimanja());
            stmt.setString(9, narudzba.getLokacija_dostave());
            stmt.setString(10, narudzba.getNapomena());
            stmt.setString(11, narudzba.getStatus());
            stmt.setBoolean(12, narudzba.isAktivan());

            stmt.executeUpdate();
        }
    }

    public void update(Narudzba narudzba) throws SQLException {
        String query = "UPDATE narudzba SET vrsta_robe = ?, kolicina = ?, jedinica_mjere = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, narudzba.getVrsta_robe());
            stmt.setDouble(2, narudzba.getKolicina());
            stmt.setString(3, narudzba.getJedinica_mjere());
            stmt.setString(4, narudzba.getStatus());
            stmt.setInt(5, narudzba.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String query = "UPDATE narudzba SET aktivan = FALSE WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Narudzba mapResultSetToNarudzba(ResultSet rs) throws SQLException {
        Narudzba narudzba = new Narudzba();
        narudzba.setId(rs.getInt("id"));
        narudzba.setBroj_narudzbe(rs.getString("broj_narudzbe"));
        narudzba.setKlijent_id(rs.getInt("klijent_id"));
        narudzba.setDatum_narudzbe(rs.getDate("datum_narudzbe") != null ? rs.getDate("datum_narudzbe").toLocalDate() : null);
        narudzba.setDatum_isporuke(rs.getDate("datum_isporuke") != null ? rs.getDate("datum_isporuke").toLocalDate() : null);
        narudzba.setVrsta_robe(rs.getString("vrsta_robe"));
        narudzba.setKolicina(rs.getDouble("kolicina"));
        narudzba.setJedinica_mjere(rs.getString("jedinica_mjere"));
        narudzba.setLokacija_preuzimanja(rs.getString("lokacija_preuzimanja"));
        narudzba.setLokacija_dostave(rs.getString("lokacija_dostave"));
        narudzba.setNapomena(rs.getString("napomena"));
        narudzba.setStatus(rs.getString("status"));
        narudzba.setAktivan(rs.getBoolean("aktivan"));
        narudzba.setDatum_kreiranja(rs.getTimestamp("datum_kreiranja") != null ? rs.getTimestamp("datum_kreiranja").toLocalDateTime() : null);
        return narudzba;
    }
}
