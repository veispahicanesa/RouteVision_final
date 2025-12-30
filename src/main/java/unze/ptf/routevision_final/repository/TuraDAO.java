package unze.ptf.routevision_final.repository;

import unze.ptf.routevision_final.config.DatabaseConfig;
import unze.ptf.routevision_final.model.Tura;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TuraDAO {

    // 1. Pronalaženje svih tura (za ADMINA)
    public List<Tura> findAll() throws SQLException {
        List<Tura> ture = new ArrayList<>();
        String query = "SELECT * FROM tura WHERE aktivan = TRUE ORDER BY datum_kreiranja DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ture.add(mapResultSetToTura(rs));
            }
        }
        return ture;
    }

    // 2. Pronalaženje tura specifičnih za vozača (za VOZAČA)
    public List<Tura> findByVozacId(int vozacId) throws SQLException {
        List<Tura> ture = new ArrayList<>();
        String query = "SELECT * FROM tura WHERE vozac_id = ? AND aktivan = TRUE ORDER BY datum_kreiranja DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, vozacId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ture.add(mapResultSetToTura(rs));
                }
            }
        }
        return ture;
    }

    // 3. Spremanje nove ture u bazu
    public void save(Tura tura) throws SQLException {
        String query = "INSERT INTO tura (broj_ture, vozac_id, kamion_id, narudzba_id, datum_pocetka, " +
                "vrijeme_pocetka, lokacija_pocetka, lokacija_kraja, status, aktivan,kreirao_admin_id, kreirao_vozac_id, datum_kreiranja) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?, NOW())";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, tura.getBroj_tura());
            stmt.setInt(2, tura.getVozac_id());
            stmt.setInt(3, tura.getKamion_id());
            stmt.setInt(4, tura.getNarudzba_id());
            stmt.setDate(5, tura.getDatum_pocetka() != null ? Date.valueOf(tura.getDatum_pocetka()) : Date.valueOf(java.time.LocalDate.now()));
            stmt.setTime(6, tura.getVrijeme_pocetka() != null ? Time.valueOf(tura.getVrijeme_pocetka()) : Time.valueOf(java.time.LocalTime.now()));
            stmt.setString(7, tura.getLokacija_pocetka());
            stmt.setString(8, tura.getLokacija_kraja());
            stmt.setString(9, tura.getStatus() != null ? tura.getStatus() : "U toku");
            stmt.setBoolean(10, true);
            stmt.setString(11, tura.getKreirao_admin_id());
            stmt.setString(12, tura.getKreirao_vozac_id());

            stmt.executeUpdate();
        }
    }

    // 4. Ažuriranje postojeće ture (Promjena statusa i KM)
    public void update(Tura tura) throws SQLException {
        String query = "UPDATE tura SET status = ?, prijedeni_kilometri = ?, datum_kraja = ?, vrijeme_kraja = ? spent_fuel = ?, fuel_used = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, tura.getStatus());
            stmt.setInt(2, tura.getPrijedeni_kilometri());
            stmt.setDate(3, tura.getDatum_kraja() != null ? Date.valueOf(tura.getDatum_kraja()) : Date.valueOf(java.time.LocalDate.now()));
            stmt.setTime(4, tura.getVrijeme_kraja() != null ? Time.valueOf(tura.getVrijeme_kraja()) : Time.valueOf(java.time.LocalTime.now()));
            stmt.setDouble(5, tura.getSpent_fuel());
            stmt.setDouble(6, tura.getFuel_used());
            stmt.setInt(7, tura.getId());

            stmt.executeUpdate();
        }
    }

    // 5. Brisanje (Soft delete - samo postavljamo aktivan na false)
    public void delete(int id) throws SQLException {
        String query = "UPDATE tura SET aktivan = FALSE WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // 6. MAPIRANJE REZULTATA (Ovdje je bila greška sa narudzba_id)
    private Tura mapResultSetToTura(ResultSet rs) throws SQLException {
        Tura t = new Tura();
        t.setId(rs.getInt("id"));

        // Pazi: u bazi je 'broj_ture', u modelu možda broj_tura. Koristimo String iz baze.
        t.setBroj_tura(rs.getString("broj_ture"));

        t.setVozac_id(rs.getInt("vozac_id"));
        t.setKamion_id(rs.getInt("kamion_id"));

        // ISPRAVLJENO: Koristimo 'narudzba_id' sa 'z' jer je tako u SQL skripti
        t.setNarudzba_id(rs.getInt("narudzba_id"));

        t.setDatum_pocetka(rs.getDate("datum_pocetka") != null ? rs.getDate("datum_pocetka").toLocalDate() : null);
        t.setVrijeme_pocetka(rs.getTime("vrijeme_pocetka") != null ? rs.getTime("vrijeme_pocetka").toLocalTime() : null);

        t.setDatum_kraja(rs.getDate("datum_kraja") != null ? rs.getDate("datum_kraja").toLocalDate() : null);
        t.setVrijeme_kraja(rs.getTime("vrijeme_kraja") != null ? rs.getTime("vrijeme_kraja").toLocalTime() : null);

        t.setLokacija_pocetka(rs.getString("lokacija_pocetka"));
        t.setLokacija_kraja(rs.getString("lokacija_kraja"));
        t.setPrijedeni_kilometri(rs.getInt("prijedeni_kilometri"));
        t.setSpent_fuel(rs.getDouble("spent_fuel"));
        t.setFuel_used(rs.getDouble("fuel_used"));
        t.setStatus(rs.getString("status"));
        t.setAktivan(rs.getBoolean("aktivan"));
        t.setKreirao_admin_id(rs.getString("kreirao_admin_id"));
        t.setKreirao_vozac_id(rs.getString("kreirao_vozac_id"));

        return t;
    }
}