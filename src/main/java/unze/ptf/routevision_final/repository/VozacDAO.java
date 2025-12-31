package unze.ptf.routevision_final.repository;

import unze.ptf.routevision_final.config.DatabaseConfig;
import unze.ptf.routevision_final.model.Vozac;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VozacDAO {
    public Vozac findByEmail(String email) throws SQLException {
        String query = "SELECT * FROM vozac WHERE email = ? AND aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapResultSetToVozac(rs);
        }
        return null;
    }

    public Vozac findById(int id) throws SQLException {
        String query = "SELECT * FROM vozac WHERE id = ? AND aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapResultSetToVozac(rs);
        }
        return null;
    }


    public void updateAssignment(int vozacId, Integer kamionId, Integer opremaId) throws SQLException {
        String query = "UPDATE vozac SET kamion_id = ?, oprema_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            if (kamionId != null) stmt.setInt(1, kamionId); else stmt.setNull(1, java.sql.Types.INTEGER);
            if (opremaId != null) stmt.setInt(2, opremaId); else stmt.setNull(2, java.sql.Types.INTEGER);
            stmt.setInt(3, vozacId);
            stmt.executeUpdate();
        }
    }
    public List<Vozac> findAll() throws SQLException {
        List<Vozac> vozaci = new ArrayList<>();
        String query = "SELECT * FROM vozac WHERE aktivan = TRUE ORDER BY id ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) vozaci.add(mapResultSetToVozac(rs));
        }
        return vozaci;
    }

    public void save(Vozac v) throws SQLException {
        String query = "INSERT INTO vozac (ime, prezime, email, lozinka, broj_telefona, " +
                "marka_kamiona, trenutna_kilometraza, tip_goriva, broj_vozacke_dozvole, " +
                "kategorija_dozvole, datum_zaposlenja, plata, broj_dovrsenih_tura, " +
                "aktivan, kamion_id, oprema_id, datum_kreiranja) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, v.getIme());
            stmt.setString(2, v.getPrezime());
            stmt.setString(3, v.getEmail());
            stmt.setString(4, v.getLozinka());
            stmt.setString(5, v.getBroj_telefona());
            stmt.setString(6, v.getMarka_kamiona());
            stmt.setInt(7, v.getTrenutna_kilometraza());
            stmt.setString(8, v.getTip_goriva());
            stmt.setString(9, v.getBroj_vozacke_dozvole());
            stmt.setString(10, v.getKategorija_dozvole());


            stmt.setDate(11, v.getDatum_zaposlenja() != null ? Date.valueOf(v.getDatum_zaposlenja()) : Date.valueOf(java.time.LocalDate.now()));

            stmt.setDouble(12, v.getPlata());
            stmt.setInt(13, v.getBroj_dovrsenih_tura());
            stmt.setBoolean(14, true);

            // Strani ključevi
            if (v.getKamionId() != null) stmt.setInt(15, v.getKamionId()); else stmt.setNull(15, java.sql.Types.INTEGER);
            if (v.getOpremaId() != null) stmt.setInt(16, v.getOpremaId()); else stmt.setNull(16, java.sql.Types.INTEGER);

            stmt.executeUpdate();
        }
    }

    public void update(Vozac v) throws SQLException {
        // SQL koji pokriva sve: od imena do plate i kamiona
        String query = "UPDATE vozac SET ime=?, prezime=?, email=?, broj_telefona=?, " +
                "broj_vozacke_dozvole=?, kategorija_dozvole=?, plata=?, kamion_id=?, " +
                "marka_kamiona=?, broj_dovrsenih_tura=? WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, v.getIme());
            stmt.setString(2, v.getPrezime());
            stmt.setString(3, v.getEmail());
            stmt.setString(4, v.getBroj_telefona());
            stmt.setString(5, v.getBroj_vozacke_dozvole());
            stmt.setString(6, v.getKategorija_dozvole());
            stmt.setDouble(7, v.getPlata());

            if (v.getKamionId() != null) stmt.setInt(8, v.getKamionId());
            else stmt.setNull(8, java.sql.Types.INTEGER);

            stmt.setString(9, v.getMarka_kamiona());
            stmt.setInt(10, v.getBroj_dovrsenih_tura());
            stmt.setInt(11, v.getId());

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
        Vozac v = new Vozac();
        v.setId(rs.getInt("id"));
        v.setIme(rs.getString("ime"));
        v.setPrezime(rs.getString("prezime"));
        v.setEmail(rs.getString("email"));
        v.setLozinka(rs.getString("lozinka"));
        v.setBroj_telefona(rs.getString("broj_telefona"));
        v.setMarka_kamiona(rs.getString("marka_kamiona"));
        v.setTrenutna_kilometraza(rs.getInt("trenutna_kilometraza"));
        v.setTip_goriva(rs.getString("tip_goriva"));
        v.setBroj_vozacke_dozvole(rs.getString("broj_vozacke_dozvole"));
        v.setKategorija_dozvole(rs.getString("kategorija_dozvole"));
        if (rs.getDate("datum_zaposlenja") != null) {
            v.setDatum_zaposlenja(rs.getDate("datum_zaposlenja").toLocalDate());
        }

        v.setPlata(rs.getDouble("plata"));
        v.setBroj_dovrsenih_tura(rs.getInt("broj_dovrsenih_tura"));

        // Čitanje ID-ova kamiona i opreme (koristimo getObject da bi dobili null ako je prazno)
        v.setKamionId(rs.getObject("kamion_id") != null ? rs.getInt("kamion_id") : null);
        v.setOpremaId(rs.getObject("oprema_id") != null ? rs.getInt("oprema_id") : null);

        v.setAktivan(rs.getBoolean("aktivan"));
        if (rs.getTimestamp("datum_kreiranja") != null) {
            v.setDatum_kreiranja(rs.getTimestamp("datum_kreiranja").toLocalDateTime());
        }
        return v;
    }
    public void updatePassword(int id, String hashedLozinka) throws SQLException {
        String query = "UPDATE vozac SET lozinka = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, hashedLozinka);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    // Popravljena update metoda koja čuva i podatke o kamionu
    public void updateVozacComplete(Vozac v) throws SQLException {
        String query = "UPDATE vozac SET ime=?, prezime=?, email=?, broj_telefona=?, " +
                "broj_vozacke_dozvole=?, kategorija_dozvole=?, plata=?, kamion_id=?, " +
                "marka_kamiona=?, trenutna_kilometraza=?, tip_goriva=?, broj_dovrsenih_tura=? WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, v.getIme());
            stmt.setString(2, v.getPrezime());
            stmt.setString(3, v.getEmail());
            stmt.setString(4, v.getBroj_telefona());
            stmt.setString(5, v.getBroj_vozacke_dozvole());
            stmt.setString(6, v.getKategorija_dozvole());
            stmt.setDouble(7, v.getPlata());

            if (v.getKamionId() != null) stmt.setInt(8, v.getKamionId());
            else stmt.setNull(8, java.sql.Types.INTEGER);

            stmt.setString(9, v.getMarka_kamiona());
            stmt.setInt(10, v.getTrenutna_kilometraza()); // DODANO
            stmt.setString(11, v.getTip_goriva());       // DODANO
            stmt.setInt(12, v.getBroj_dovrsenih_tura());
            stmt.setInt(13, v.getId());

            stmt.executeUpdate();
        }
    }
}