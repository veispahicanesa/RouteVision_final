package unze.ptf.routevision_final.repository;

import unze.ptf.routevision_final.config.DatabaseConfig;
import unze.ptf.routevision_final.model.Klijent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KlijentDAO {
    public Klijent findById(int id) throws SQLException {
        String query = "SELECT * FROM klijent WHERE id = ? AND aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapResultSetToKlijent(rs);
        }
        return null;
    }

    public List<Klijent> findAll() throws SQLException {
        List<Klijent> klijenti = new ArrayList<>();
        String query = "SELECT * FROM klijent WHERE aktivan = TRUE ORDER BY naziv_firme";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) klijenti.add(mapResultSetToKlijent(rs));
        }
        return klijenti;
    }

    public void save(Klijent klijent) throws SQLException {
        String query = "INSERT INTO klijent (naziv_firme, tip_klijenta, adresa, mjesto, postanskiBroj, drzava, kontakt_osoba, email, broj_telefona, broj_faksa, poreska_broj, naziv_banke, racun_broj, aktivan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, klijent.getNaziv_firme());
            stmt.setString(2, klijent.getTip_klijenta());
            stmt.setString(3, klijent.getAdresa());
            stmt.setString(4, klijent.getMjesto());
            stmt.setString(5, klijent.getPostanskiBroj());
            stmt.setString(6, klijent.getDrzava());
            stmt.setString(7, klijent.getKontakt_osoba());
            stmt.setString(8, klijent.getEmail());
            stmt.setString(9, klijent.getBroj_telefona());
            stmt.setString(10, klijent.getBroj_faksa());
            stmt.setString(11, klijent.getPoreska_broj());
            stmt.setString(12, klijent.getNaziv_banke());
            stmt.setString(13, klijent.getRacun_broj());
            stmt.setBoolean(14, klijent.isAktivan());
            stmt.executeUpdate();
        }
    }

    public void update(Klijent klijent) throws SQLException {
        String query = "UPDATE klijent SET naziv_firme = ?, tip_klijenta = ?, adresa = ?, mjesto = ?, postanskiBroj = ?, drzava = ?, kontakt_osoba = ?, email = ?, broj_telefona = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, klijent.getNaziv_firme());
            stmt.setString(2, klijent.getTip_klijenta());
            stmt.setString(3, klijent.getAdresa());
            stmt.setString(4, klijent.getMjesto());
            stmt.setString(5, klijent.getPostanskiBroj());
            stmt.setString(6, klijent.getDrzava());
            stmt.setString(7, klijent.getKontakt_osoba());
            stmt.setString(8, klijent.getEmail());
            stmt.setString(9, klijent.getBroj_telefona());
            stmt.setInt(10, klijent.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String query = "UPDATE klijent SET aktivan = FALSE WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Klijent mapResultSetToKlijent(ResultSet rs) throws SQLException {
        Klijent klijent = new Klijent();
        klijent.setId(rs.getInt("id"));
        klijent.setNaziv_firme(rs.getString("naziv_firme"));
        klijent.setTip_klijenta(rs.getString("tip_klijenta"));
        klijent.setAdresa(rs.getString("adresa"));
        klijent.setMjesto(rs.getString("mjesto"));
        klijent.setPostanskiBroj(rs.getString("postanskiBroj"));
        klijent.setDrzava(rs.getString("drzava"));
        klijent.setKontakt_osoba(rs.getString("kontakt_osoba"));
        klijent.setEmail(rs.getString("email"));
        klijent.setBroj_telefona(rs.getString("broj_telefona"));
        klijent.setBroj_faksa(rs.getString("broj_faksa"));
        klijent.setPoreska_broj(rs.getString("poreska_broj"));
        klijent.setNaziv_banke(rs.getString("naziv_banke"));
        klijent.setRacun_broj(rs.getString("racun_broj"));
        klijent.setUkupna_narudena_kolicina(rs.getDouble("ukupna_narudena_kolicina"));
        klijent.setUkupno_placeno(rs.getDouble("ukupno_placeno"));
        klijent.setAktivan(rs.getBoolean("aktivan"));
        klijent.setDatum_kreiranja(rs.getTimestamp("datum_kreiranja") != null ? rs.getTimestamp("datum_kreiranja").toLocalDateTime() : null);
        return klijent;
    }
}
