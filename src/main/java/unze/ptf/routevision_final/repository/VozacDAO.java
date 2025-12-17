package unze.ptf.routevision_final.repository;

import unze.ptf.routevision_final.config.DatabaseConfig;
import unze.ptf.routevision_final.model.Vozac;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/*
 * VozacDAO - CRUD klasa za tabelu Vozac.
 * Omogućava kreiranje, čitanje, ažuriranje i logičko brisanje vozača u bazi.
 */
public class VozacDAO {
    public Vozac findByEmail(String email) throws SQLException {
        String query = "SELECT * FROM vozac WHERE email = ? AND aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToVozac(rs);
            }
        }
        return null;
    }

    public Vozac findById(int id) throws SQLException {
        String query = "SELECT * FROM vozac WHERE id = ? AND aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToVozac(rs);
            }
        }
        return null;
    }

    public List<Vozac> findAll() throws SQLException {
        List<Vozac> vozaci = new ArrayList<>();
        String query = "SELECT * FROM vozac WHERE aktivan = TRUE ORDER BY prezime, ime";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                vozaci.add(mapResultSetToVozac(rs));
            }
        }
        return vozaci;
    }

    public void save(Vozac vozac) throws SQLException {
        String query = "INSERT INTO vozac (ime, prezime, email, lozinka, broj_vozacke_dozvole, aktivan) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, vozac.getIme());
            stmt.setString(2, vozac.getPrezime());
            stmt.setString(3, vozac.getEmail());
            stmt.setString(4, vozac.getLozinka());
            stmt.setString(5, vozac.getBroj_vozacke_dozvole());
            stmt.setBoolean(6, vozac.isAktivan());
            stmt.executeUpdate();
        }
    }

    public void update(Vozac vozac) throws SQLException {
        String query = "UPDATE vozac SET ime = ?, prezime = ?, broj_telefona = ?, kategorija_dozvole = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, vozac.getIme());
            stmt.setString(2, vozac.getPrezime());
            stmt.setString(3, vozac.getBroj_telefona());
            stmt.setString(4, vozac.getKategorija_dozvole());
            stmt.setInt(5, vozac.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String query = "UPDATE vozac SET aktivan = FALSE WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Vozac mapResultSetToVozac(ResultSet rs) throws SQLException {
        Vozac vozac = new Vozac();
        vozac.setId(rs.getInt("id"));
        vozac.setIme(rs.getString("ime"));
        vozac.setPrezime(rs.getString("prezime"));
        vozac.setEmail(rs.getString("email"));
        vozac.setLozinka(rs.getString("lozinka"));
        vozac.setBroj_telefona(rs.getString("broj_telefona"));
        vozac.setBroj_vozacke_dozvole(rs.getString("broj_vozacke_dozvole"));
        vozac.setKategorija_dozvole(rs.getString("kategorija_dozvole"));
        vozac.setDatum_zaposlenja(rs.getDate("datum_zaposlenja") != null ? rs.getDate("datum_zaposlenja").toLocalDate() : null);
        vozac.setPlata(rs.getDouble("plata"));
        vozac.setBroj_dovrsenih_tura(rs.getInt("broj_dovrsenih_putovanja"));
        vozac.setStanje_racuna(rs.getDouble("stanje_racuna"));
        vozac.setAktivna_slika(rs.getString("aktivna_slika"));
        vozac.setAktivan(rs.getBoolean("aktivan"));
        vozac.setDatum_kreiranja(rs.getTimestamp("datum_kreiranja").toLocalDateTime());
        return vozac;
    }
}