package unze.ptf.routevision_final.repository;

import unze.ptf.routevision_final.config.DatabaseConfig;
import unze.ptf.routevision_final.model.Tura;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TuraDAO {

    public List<Tura> findAll() throws SQLException {
        List<Tura> ture = new ArrayList<>();

        // SQL izmijenjen na LEFT JOIN
        String query = "SELECT t.*, k.naziv_firme " +
                "FROM tura t " +
                "LEFT JOIN narudzba n ON t.narudzba_id = n.id " +
                "LEFT JOIN klijent k ON n.klijent_id = k.id " +
                "WHERE t.aktivan = TRUE";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Tura t = mapResultSetToTura(rs);

                // Uzimamo naziv firme, ako je NULL (nema narudžbe), stavljamo tekst
                String firma = rs.getString("naziv_firme");
                t.setNapomena(firma != null ? firma : "Nema klijenta");

                ture.add(t);
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

    public void save(Tura tura) throws SQLException {
        String query = "INSERT INTO tura (broj_ture, vozac_id, kamion_id, narudzba_id, datum_pocetka, " +
                "vrijeme_pocetka, lokacija_pocetka, lokacija_kraja, status, napomena, aktivan, kreirao_admin_id, kreirao_vozac_id, datum_kreiranja) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, tura.getBroj_tura());
            stmt.setInt(2, tura.getVozac_id());
            stmt.setInt(3, tura.getKamion_id());

            if (tura.getNarudzba_id() != 0) {
                stmt.setInt(4, tura.getNarudzba_id());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }

            stmt.setDate(5, tura.getDatum_pocetka() != null ? Date.valueOf(tura.getDatum_pocetka()) : Date.valueOf(java.time.LocalDate.now()));
            stmt.setTime(6, tura.getVrijeme_pocetka() != null ? Time.valueOf(tura.getVrijeme_pocetka()) : Time.valueOf(java.time.LocalTime.now()));
            stmt.setString(7, tura.getLokacija_pocetka());
            stmt.setString(8, tura.getLokacija_kraja());
            stmt.setString(9, tura.getStatus() != null ? tura.getStatus() : "U toku");
            stmt.setString(10, tura.getNapomena());
            stmt.setBoolean(11, true);

            if (tura.getKreirao_admin_id() != null && !tura.getKreirao_admin_id().isEmpty()) {
                stmt.setInt(12, Integer.parseInt(tura.getKreirao_admin_id()));
            } else {
                stmt.setNull(12, java.sql.Types.INTEGER);
            }

            stmt.setString(13, tura.getKreirao_vozac_id());

            stmt.executeUpdate();
        }
    }


    public void update(Tura tura) throws SQLException {
        // SQL upit koji uključuje SVA polja potrebna za završetak ture
        String query = "UPDATE tura SET status = ?, prijedeni_kilometri = ?, spent_fuel = ?, fuel_used = ?, " +
                "prosjecna_brzina = ?, datum_kraja = ?, vrijeme_kraja = ?, " +
                "lokacija_pocetka = ?, lokacija_kraja = ?, vrijeme_pocetka = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, tura.getStatus());
            stmt.setInt(2, tura.getPrijedeni_kilometri());
            stmt.setDouble(3, tura.getSpent_fuel());
            stmt.setDouble(4, tura.getFuel_used());
            stmt.setInt(5, tura.getProsjecna_brzina());
            stmt.setDate(6, tura.getDatum_kraja() != null ? Date.valueOf(tura.getDatum_kraja()) : null);
            stmt.setTime(7, tura.getVrijeme_kraja() != null ? Time.valueOf(tura.getVrijeme_kraja()) : null);
            stmt.setString(8, tura.getLokacija_pocetka());
            stmt.setString(9, tura.getLokacija_kraja());
            stmt.setTime(10, tura.getVrijeme_pocetka() != null ? Time.valueOf(tura.getVrijeme_pocetka()) : null);

            // ID mora biti na zadnjem mjestu (11. upitnik)
            stmt.setInt(11, tura.getId());

            int rows = stmt.executeUpdate();
            System.out.println("SQL IZVRŠEN: Izmijenjeno redova: " + rows + " za ID: " + tura.getId() +
                    " | Gorivo: " + tura.getSpent_fuel() + " L");

            if (rows == 0) {
                throw new SQLException("Update nije uspio, tura sa ID " + tura.getId() + " nije pronađena!");
            }
        }
    }


    public void delete(int id) throws SQLException {
        // Fizičko brisanje reda iz tabele tura
        String query = "DELETE FROM tura WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }


    private Tura mapResultSetToTura(ResultSet rs) throws SQLException {
        Tura t = new Tura();
        t.setId(rs.getInt("id"));


        t.setBroj_tura(rs.getString("broj_ture"));

        t.setVozac_id(rs.getInt("vozac_id"));
        t.setKamion_id(rs.getInt("kamion_id"));

        t.setNarudzba_id(rs.getInt("narudzba_id"));

        t.setDatum_pocetka(rs.getDate("datum_pocetka") != null ? rs.getDate("datum_pocetka").toLocalDate() : null);
        t.setVrijeme_pocetka(rs.getTime("vrijeme_pocetka") != null ? rs.getTime("vrijeme_pocetka").toLocalTime() : null);

        t.setDatum_kraja(rs.getDate("datum_kraja") != null ? rs.getDate("datum_kraja").toLocalDate() : null);
        t.setVrijeme_kraja(rs.getTime("vrijeme_kraja") != null ? rs.getTime("vrijeme_kraja").toLocalTime() : null);

        t.setLokacija_pocetka(rs.getString("lokacija_pocetka"));
        t.setLokacija_kraja(rs.getString("lokacija_kraja"));
        t.setPrijedeni_kilometri(rs.getInt("prijedeni_kilometri"));
        t.setProsjecna_brzina(rs.getInt("prosjecna_brzina"));
        t.setNapomena(rs.getString("napomena"));
        t.setSpent_fuel(rs.getDouble("spent_fuel"));
        t.setFuel_used(rs.getDouble("fuel_used"));
        t.setStatus(rs.getString("status"));
        t.setAktivan(rs.getBoolean("aktivan"));
        t.setKreirao_admin_id(rs.getString("kreirao_admin_id"));
        t.setKreirao_vozac_id(rs.getString("kreirao_vozac_id"));

        if (rs.getTimestamp("datum_kreiranja") != null) {
            t.setDatum_kreiranja(rs.getTimestamp("datum_kreiranja").toLocalDateTime());
        }

        return t;
    }
    // Metoda za prebrojavanje aktivnih tura (za KPI - Prosjek po turi)
    public int countAktivneTure() throws SQLException {
        String query = "SELECT COUNT(*) FROM tura WHERE aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // Metoda za ukupnu kilometražu (za KPI - Trošak po kilometru)
    public int getUkupniKilometri() throws SQLException {
        String query = "SELECT SUM(prijedeni_kilometri) FROM tura WHERE aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    // Za Tačku 1: Ukupna potrošnja goriva
    public double getUkupnaPotrosnjaGoriva() throws SQLException {
        String query = "SELECT SUM(fuel_used) FROM tura WHERE aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }

    // Za Tačku 1: Broj različitih kamiona koji su bili u pokretu
    public int getBrojAktivnihVozila() throws SQLException {
        String query = "SELECT COUNT(DISTINCT kamion_id) FROM tura WHERE aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<String> getTopKlijenti() throws SQLException {
        List<String> klijenti = new ArrayList<>();

        // SQL upit prilagođen tvojoj šemi (fakture + klijent)
        String query = "SELECT k.naziv_firme, SUM(f.ukupan_iznos) as ukupno " +
                "FROM fakture f " +
                "JOIN klijent k ON f.klijent_id = k.id " +
                "GROUP BY k.naziv_firme " +
                "ORDER BY ukupno DESC LIMIT 3";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String klijent = rs.getString("naziv_firme");
                double iznos = rs.getDouble("ukupno");
                klijenti.add(klijent + ": " + String.format("%.2f", iznos) + " km");
            }
        }
        return klijenti;
    }
    public void hardDelete(int id) throws SQLException {
        String query = "DELETE FROM tura WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}