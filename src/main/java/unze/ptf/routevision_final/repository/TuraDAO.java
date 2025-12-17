package unze.ptf.routevision_final.repository;
import unze.ptf.routevision_final.config.DatabaseConfig;
import unze.ptf.routevision_final.model.Tura;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/*
 * TuraDAO - CRUD klasa za tabelu Tura/Putovanje.
 * Omogućava kreiranje, čitanje, ažuriranje i logičko brisanje tura u bazi.
 */
public class TuraDAO {
    public Tura findById(int id) throws SQLException {
        String query = "SELECT * FROM putovanje WHERE id = ? AND aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapResultSetToPutovanje(rs);
        }
        return null;
    }

    public List<Tura> findAll() throws SQLException {
        List<Tura> tura = new ArrayList<>();
        String query = "SELECT * FROM tura WHERE aktivan = TRUE ORDER BY datum_pocetka DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) tura.add(mapResultSetToPutovanje(rs));
        }
        return tura;
    }

    public List<Tura> findByVozacId(int vozacId) throws SQLException {
        List<Tura> tura = new ArrayList<>();
        String query = "SELECT * FROM tura WHERE vozac_id = ? AND aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, vozacId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) tura.add(mapResultSetToPutovanje(rs));
        }
        return tura;
    }


    public void save(Tura tura) throws SQLException {
        String query = "INSERT INTO tura (broj_tura, vozac_id, kamion_id, narudba_id, datum_pocetka, vrijeme_pocetka, datum_kraja, vrijeme_kraja, lokacija_pocetka, lokacija_kraja, prijedeni_kilometri, prosjecna_brzina, spent_fuel, fuel_used, napomena, status, aktivan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, tura.getBroj_tura());
            stmt.setInt(2, tura.getVozac_id());
            stmt.setInt(3, tura.getKamion_id());
            stmt.setInt(4, tura.getNarudba_id());
            stmt.setDate(5, tura.getDatum_pocetka() != null ? Date.valueOf(tura.getDatum_pocetka()) : null);
            stmt.setTime(6, tura.getVrijeme_pocetka() != null ? Time.valueOf(tura.getVrijeme_pocetka()) : null);
            stmt.setDate(7, tura.getDatum_kraja() != null ? Date.valueOf(tura.getDatum_kraja()) : null);
            stmt.setTime(8, tura.getVrijeme_kraja() != null ? Time.valueOf(tura.getVrijeme_kraja()) : null);
            stmt.setString(9, tura.getLokacija_pocetka());
            stmt.setString(10, tura.getLokacija_kraja());
            stmt.setInt(11, tura.getPrijedeni_kilometri());
            stmt.setInt(12, tura.getProsjecna_brzina());
            stmt.setDouble(13, tura.getSpent_fuel());
            stmt.setDouble(14, tura.getFuel_used());
            stmt.setString(15, tura.getNapomena());
            stmt.setString(16, tura.getStatus());
            stmt.setBoolean(17, tura.isAktivan());
            stmt.executeUpdate();
        }
    }

    public void update(Tura tura) throws SQLException {
        String query = "UPDATE tura SET prijedeni_kilometri = ?, prosjecna_brzina = ?, spent_fuel = ?, fuel_used = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, tura.getPrijedeni_kilometri());
            stmt.setInt(2, tura.getProsjecna_brzina());
            stmt.setDouble(3, tura.getSpent_fuel());
            stmt.setDouble(4, tura.getFuel_used());
            stmt.setString(5, tura.getStatus());
            stmt.setInt(6, tura.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String query = "UPDATE tura SET aktivan = FALSE WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Tura mapResultSetToPutovanje(ResultSet rs) throws SQLException {
        Tura tura = new Tura();
        tura.setId(rs.getInt("id"));
        tura.setBroj_tura(rs.getString("broj_tura"));
        tura.setVozac_id(rs.getInt("vozac_id"));
        tura.setKamion_id(rs.getInt("kamion_id"));
        tura.setNarudba_id(rs.getInt("narudba_id"));
        tura.setDatum_pocetka(rs.getDate("datum_pocetka") != null ? rs.getDate("datum_pocetka").toLocalDate() : null);
        tura.setVrijeme_pocetka(rs.getTime("vrijeme_pocetka") != null ? rs.getTime("vrijeme_pocetka").toLocalTime() : null);
        tura.setDatum_kraja(rs.getDate("datum_kraja") != null ? rs.getDate("datum_kraja").toLocalDate() : null);
        tura.setVrijeme_kraja(rs.getTime("vrijeme_kraja") != null ? rs.getTime("vrijeme_kraja").toLocalTime() : null);
        tura.setLokacija_pocetka(rs.getString("lokacija_pocetka"));
        tura.setLokacija_kraja(rs.getString("lokacija_kraja"));
        tura.setPrijedeni_kilometri(rs.getInt("prijedeni_kilometri"));
        tura.setProsjecna_brzina(rs.getInt("prosjecna_brzina"));
        tura.setSpent_fuel(rs.getDouble("spent_fuel"));
        tura.setFuel_used(rs.getDouble("fuel_used"));
        tura.setNapomena(rs.getString("napomena"));
        tura.setStatus(rs.getString("status"));
        tura.setAktivan(rs.getBoolean("aktivan"));
        tura.setDatum_kreiranja(rs.getTimestamp("datum_kreiranja") != null ? rs.getTimestamp("datum_kreiranja").toLocalDateTime() : null);
        return tura;
    }
}
