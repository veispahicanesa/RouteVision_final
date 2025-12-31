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

        String query = "SELECT sd.*, k.registarska_tablica AS reg_kamiona, v.ime AS ime_v, v.prezime AS prez_v " +
                "FROM servisni_dnevnik sd " +
                "LEFT JOIN kamion k ON sd.kamion_id = k.id " +
                "LEFT JOIN vozac v ON sd.vozac_id = v.id " +
                "WHERE sd.aktivan = TRUE ORDER BY sd.datum_servisa DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                servisi.add(mapResultSetToServisniDnevnik(rs));
            }
        }
        return servisi;
    }

    public List<ServisniDnevnik> findForVozac(int userId) throws SQLException {
        List<ServisniDnevnik> servisi = new ArrayList<>();

        // OVDJE JE KLJUČ: Mora postojati "AND sd.vozac_id = ?"
        String query = "SELECT sd.*, k.registarska_tablica AS reg_kamiona, v.ime AS ime_v, v.prezime AS prez_v " +
                "FROM servisni_dnevnik sd " +
                "LEFT JOIN kamion k ON sd.kamion_id = k.id " +
                "LEFT JOIN vozac v ON sd.vozac_id = v.id " +
                "WHERE sd.aktivan = TRUE AND sd.vozac_id = ? " + // Ovaj upit filtrira!
                "ORDER BY sd.datum_servisa DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Ova linija dodeljuje broj 3 (tvoj ID) umjesto upitnika
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                servisi.add(mapResultSetToServisniDnevnik(rs));
            }
        }
        return servisi;
    }
    public void save(ServisniDnevnik s) throws SQLException {
        String query = "INSERT INTO servisni_dnevnik (kamion_id, vozac_id, datum_servisa, vrsta_servisa, opisServisa, kreirao_korisnik, nadlezni_admin_id, km_na_servisu, troskovi, serviser_naziv, napomena, aktivan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, s.getKamion_id());
            stmt.setObject(2, s.getVozac_id());
            stmt.setDate(3, Date.valueOf(s.getDatum_servisa()));
            stmt.setString(4, s.getVrsta_servisa());
            stmt.setString(5, s.getOpisServisa());
            stmt.setString(6, s.getKreirao_korisnik());
            stmt.setObject(7, s.getNadlezni_admin_id());
            stmt.setInt(8, s.getKm_na_servisu());
            stmt.setDouble(9, s.getTroskovi());
            stmt.setString(10, s.getServiser_naziv());
            stmt.setString(11, s.getNapomena());
            stmt.setBoolean(12, true);
            stmt.executeUpdate();
        }
    }

    public void update(ServisniDnevnik s) throws SQLException {
        String query = "UPDATE servisni_dnevnik SET kamion_id = ?, vozac_id = ?, datum_servisa = ?, " +
                "vrsta_servisa = ?, opisServisa = ?, km_na_servisu = ?, troskovi = ?, " +
                "serviser_naziv = ?, napomena = ?, kreirao_korisnik = ?, nadlezni_admin_id = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, s.getKamion_id());
            stmt.setObject(2, s.getVozac_id()); // Koristimo setObject jer može biti NULL
            stmt.setDate(3, Date.valueOf(s.getDatum_servisa()));
            stmt.setString(4, s.getVrsta_servisa());
            stmt.setString(5, s.getOpisServisa());
            stmt.setInt(6, s.getKm_na_servisu());
            stmt.setDouble(7, s.getTroskovi());
            stmt.setString(8, s.getServiser_naziv());
            stmt.setString(9, s.getNapomena());
            stmt.setString(10, s.getKreirao_korisnik());
            stmt.setObject(11, s.getNadlezni_admin_id());
            stmt.setInt(12, s.getId()); // ID ide zadnji za WHERE klauzulu

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
        ServisniDnevnik d = new ServisniDnevnik();
        d.setId(rs.getInt("id"));
        d.setKamion_id(rs.getInt("kamion_id"));
        d.setVozac_id(rs.getInt("vozac_id"));
        d.setDatum_servisa(rs.getDate("datum_servisa").toLocalDate());
        d.setVrsta_servisa(rs.getString("vrsta_servisa"));

        // OVO JE KLJUČNO: Provjeri da li se naziv podudara sa SQL šemom
        d.setOpisServisa(rs.getString("opisServisa")); //
        d.setKm_na_servisu(rs.getInt("km_na_servisu"));
        d.setTroskovi(rs.getDouble("troskovi"));
        d.setServiser_naziv(rs.getString("serviser_naziv"));
        d.setKreirao_korisnik(rs.getString("kreirao_korisnik")); //

        // Podaci iz JOIN-a
        d.setRegistracijaKamiona(rs.getString("reg_kamiona"));
        String ime = rs.getString("ime_v");
        String prezime = rs.getString("prez_v");
        d.setImeVozaca((ime != null ? ime : "") + " " + (prezime != null ? prezime : ""));

        return d;
    }
}