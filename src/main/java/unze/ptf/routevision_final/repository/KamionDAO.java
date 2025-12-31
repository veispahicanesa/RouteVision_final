package unze.ptf.routevision_final.repository;

import unze.ptf.routevision_final.config.DatabaseConfig;
import unze.ptf.routevision_final.model.Kamion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KamionDAO {


    public List<Kamion> findAll() throws SQLException {
        List<Kamion> kamioni = new ArrayList<>();
        String query = "SELECT * FROM kamion WHERE aktivan = TRUE ORDER BY id ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) kamioni.add(mapResultSetToKamion(rs));
        }
        return kamioni;
    }

    public List<Kamion> findByVozacId(int vozacId) throws SQLException {
        List<Kamion> kamioni = new ArrayList<>();
        // SQL upit je sada jednostavniji: tražimo samo u tabeli kamion
        String query = "SELECT * FROM kamion WHERE zaduzeni_vozac_id = ? AND aktivan = TRUE";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, vozacId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Koristimo isti mapResultSetToKamion koji je sada očišćen
                kamioni.add(mapResultSetToKamion(rs));
            }
        }
        return kamioni;
    }
    public void save(Kamion k) throws SQLException {
        String query = "INSERT INTO kamion (registarska_tablica, marka, model, godina_proizvodnje, " +
                "kapacitet_tone, stanje_kilometra, datum_registracije,ime_vozaca, prezime_vozaca, aktivan, datum_kreiranja) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?,?, NOW())";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, k.getRegistarska_tablica());
            stmt.setString(2, k.getMarka());
            stmt.setString(3, k.getModel());
            stmt.setInt(4, k.getGodina_proizvodnje());
            stmt.setDouble(5, k.getKapacitet_tone());
            stmt.setInt(6, k.getStanje_kilometra());
            if (k.getDatum_registracije() != null) {
                stmt.setDate(7, java.sql.Date.valueOf(k.getDatum_registracije()));
            } else {
                stmt.setNull(7, java.sql.Types.DATE);
            }

            stmt.setString(8, k.getIme_vozaca());
            stmt.setString(9, k.getPrezime_vozaca());
            stmt.setBoolean(10, true);

            stmt.executeUpdate();
        }
    }
    public void update(Kamion kamion) throws SQLException {
        String query = "UPDATE kamion SET marka = ?, model = ?, registarska_tablica = ?, " +
                "godina_proizvodnje = ?, kapacitet_tone = ?, stanje_kilometra = ?,  " +
                "ime_vozaca = ?, prezime_vozaca = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, kamion.getMarka());
            stmt.setString(2, kamion.getModel());
            stmt.setString(3, kamion.getRegistarska_tablica());
            stmt.setInt(4, kamion.getGodina_proizvodnje());
            stmt.setDouble(5, kamion.getKapacitet_tone());
            stmt.setInt(6, kamion.getStanje_kilometra());
            stmt.setString(7, kamion.getIme_vozaca());
            stmt.setString(8, kamion.getPrezime_vozaca());
            stmt.setInt(9, kamion.getId());
            stmt.executeUpdate();
        }
    }
    public void delete(int id) throws SQLException {
        String query = "UPDATE kamion SET aktivan = FALSE, zaduzeni_vozac_id = NULL, " +
                "ime_vozaca = NULL, prezime_vozaca = NULL WHERE id = ?";
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
        k.setIme_vozaca(rs.getString("ime_vozaca"));
        k.setPrezime_vozaca(rs.getString("prezime_vozaca"));

        Date regDate = rs.getDate("datum_registracije");
        if (regDate != null) {
            k.setDatum_registracije(regDate.toLocalDate());
        }

        int vId = rs.getInt("zaduzeni_vozac_id");
        k.setZaduzeni_vozac_id(rs.wasNull() ? null : vId);

        k.setAktivan(rs.getBoolean("aktivan"));

        return k;
    }
    public void updateVozacNaKamionu(int vozacId, String ime, String prezime, Integer noviKamionId) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            // 1. Prvo očistimo ime ovog vozača sa bilo kojeg kamiona koji je ranije dužio
            String clearQuery = "UPDATE kamion SET ime_vozaca = NULL, prezime_vozaca = NULL, zaduzeni_vozac_id = NULL " +
                    "WHERE zaduzeni_vozac_id = ?";
            try (PreparedStatement clearStmt = conn.prepareStatement(clearQuery)) {
                clearStmt.setInt(1, vozacId);
                clearStmt.executeUpdate();
            }


            if (noviKamionId != null && noviKamionId > 0) {
                String updateQuery = "UPDATE kamion SET ime_vozaca = ?, prezime_vozaca = ?, zaduzeni_vozac_id = ? WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, ime);
                    updateStmt.setString(2, prezime);
                    updateStmt.setInt(3, vozacId);
                    updateStmt.setInt(4, noviKamionId);
                    updateStmt.executeUpdate();
                }
            }
        }
    }
}