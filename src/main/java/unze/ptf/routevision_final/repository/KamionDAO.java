package unze.ptf.routevision_final.repository;

import unze.ptf.routevision_final.config.DatabaseConfig;
import unze.ptf.routevision_final.model.Kamion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KamionDAO {

    public Kamion findById(int id) throws SQLException {
        String query = "SELECT * FROM kamion WHERE id = ? AND aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapResultSetToKamion(rs);
        }
        return null;
    }

    public List<Kamion> findAll() throws SQLException {
        List<Kamion> kamioni = new ArrayList<>();
        String query = "SELECT k.*, v.ime AS ime_vozaca, v.prezime AS prezime_vozaca " +
                "FROM kamion k " +
                "LEFT JOIN vozac v ON k.zaduzeni_vozac_id = v.id " +
                "WHERE k.aktivan = TRUE ORDER BY k.registarska_tablica";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) kamioni.add(mapResultSetToKamion(rs));
        }
        return kamioni;
    }

    public List<Kamion> findByVozacId(int vozacId) throws SQLException {
        List<Kamion> kamioni = new ArrayList<>();
        // SQL upit koji filtrira kamione prema ID-u vozača koji ga je zadužio
        String query = "SELECT k.*, v.ime AS ime_vozaca, v.prezime AS prezime_vozaca " +
                "FROM kamion k " +
                "LEFT JOIN vozac v ON k.zaduzeni_vozac_id = v.id " +
                "WHERE k.zaduzeni_vozac_id = ? AND k.aktivan = TRUE";


        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, vozacId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // mapResultSetToKamion je metoda koja pretvara red iz baze u Kamion objekat
                // Na taj način dobijaš SVE podatke (marku, model, tablice, nosivost, itd.)
                kamioni.add(mapResultSetToKamion(rs));
            }
        }
        return kamioni;
    }
    public void save(Kamion k) throws SQLException {
        String query = "INSERT INTO kamion (registarska_tablica, marka, model, godina_proizvodnje, " +
                "kapacitet_tone, stanje_kilometra, datum_registracije, zaduzeni_vozac_id, aktivan, datum_kreiranja) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, k.getRegistarska_tablica());
            stmt.setString(2, k.getMarka());
            stmt.setString(3, k.getModel());
            stmt.setInt(4, k.getGodina_proizvodnje());
            stmt.setDouble(5, k.getKapacitet_tone());
            stmt.setInt(6, k.getStanje_kilometra());

            // 7. Datum registracije
            if (k.getDatum_registracije() != null) {
                stmt.setDate(7, java.sql.Date.valueOf(k.getDatum_registracije()));
            } else {
                stmt.setNull(7, java.sql.Types.DATE);
            }

            // 8. ID Vozača (setObject je sigurnije za null vrijednosti)
            stmt.setObject(8, k.getZaduzeni_vozac_id());

            // 9. Aktivan status
            stmt.setBoolean(9, true);

            stmt.executeUpdate();
        }
    }
    public void update(Kamion kamion) throws SQLException {
        // ISPRAVLJENO: Promijenjeno 'vozac_id' u 'zaduzeni_vozac_id' u UPDATE upitu
        String query = "UPDATE kamion SET marka = ?, model = ?, registarska_tablica = ?, godina_proizvodnje = ?, kapacitet_tone = ?, stanje_kilometra = ?, zaduzeni_vozac_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, kamion.getMarka());
            stmt.setString(2, kamion.getModel());
            stmt.setString(3, kamion.getRegistarska_tablica());
            stmt.setInt(4, kamion.getGodina_proizvodnje());
            stmt.setDouble(5, kamion.getKapacitet_tone());
            stmt.setInt(6, kamion.getStanje_kilometra());
            stmt.setObject(7, kamion.getZaduzeni_vozac_id());
            stmt.setInt(8, kamion.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String query = "UPDATE kamion SET aktivan = FALSE WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Kamion mapResultSetToKamion(ResultSet rs) throws SQLException {
        Kamion k = new Kamion();
        k.setId(rs.getInt("id"));
        k.setRegistarska_tablica(rs.getString("registarska_tablica"));
        k.setMarka(rs.getString("marka"));
        k.setModel(rs.getString("model"));
        k.setGodina_proizvodnje(rs.getInt("godina_proizvodnje"));
        k.setKapacitet_tone(rs.getDouble("kapacitet_tone"));
        k.setStanje_kilometra(rs.getInt("stanje_kilometra"));

        // Datumi
        Date regDate = rs.getDate("datum_registracije");
        if (regDate != null) k.setDatum_registracije(regDate.toLocalDate());

        Date pregledDate = rs.getDate("datum_zakljucnog_pregleda");
        if (pregledDate != null) k.setDatum_zakljucnog_pregleda(pregledDate.toLocalDate());

        // Vozač ID (sigurno rukovanje null vrijednošću)
        int vId = rs.getInt("zaduzeni_vozac_id");
        if (!rs.wasNull()) {
            k.setZaduzeni_vozac_id(vId);
        } else {
            k.setZaduzeni_vozac_id(null);
        }

        // IME I PREZIME IZ JOIN-a (OVO JE KLJUČNO)
        k.setIme_vozaca(rs.getString("ime_vozaca") != null ? rs.getString("ime_vozaca") : "");
        k.setPrezime_vozaca(rs.getString("prezime_vozaca") != null ? rs.getString("prezime_vozaca") : "");

        k.setAktivan(rs.getBoolean("aktivan"));
        return k;
    }
}