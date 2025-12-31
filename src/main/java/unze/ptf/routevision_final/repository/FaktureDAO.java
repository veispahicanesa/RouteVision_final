package unze.ptf.routevision_final.repository;

import unze.ptf.routevision_final.config.DatabaseConfig;
import unze.ptf.routevision_final.model.Fakture;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FaktureDAO {

    public List<Fakture> findAll() throws SQLException {
        List<Fakture> lista = new ArrayList<>();
        String query = "SELECT *, `datum_dospjeća` AS datum_dospjeca FROM fakture WHERE aktivan = TRUE ORDER BY datum_izdavanja DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapResultSetToFakture(rs));
            }
        }
        return lista;
    }

    public List<Fakture> findForVozac(int vozacId) throws SQLException {
        List<Fakture> lista = new ArrayList<>();
        String query = "SELECT f.*, f.`datum_dospjeća` AS datum_dospjeca FROM fakture f " +
                "JOIN tura t ON f.tura_id = t.id " +
                "WHERE t.vozac_id = ? AND f.aktivan = TRUE " +
                "ORDER BY f.datum_izdavanja DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, vozacId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapResultSetToFakture(rs));
            }
        }
        return lista;
    }


    public void save(Fakture f) throws SQLException {
        String query = "INSERT INTO fakture (broj_fakture, tura_id, klijent_id, datum_izdavanja, odobrio_admin_id, " +
                "`datum_dospjeća`, vrsta_usluge, cijena_po_km, broj_km, iznos_usluge, porez, ukupan_iznos, " +
                "status_placanja, nacin_placanja, datum_placanja, napomena, datoteka_path, aktivan) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, f.getBroj_fakture());
            stmt.setInt(2, f.getTura_id());
            stmt.setInt(3, f.getKlijent_id());
            stmt.setDate(4, f.getDatum_izdavanja() != null ? Date.valueOf(f.getDatum_izdavanja()) : null);
            stmt.setInt(5, f.getOdobrio_admin_id());
            stmt.setDate(6, f.getDatum_dospjeca() != null ? Date.valueOf(f.getDatum_dospjeca()) : null);
            stmt.setString(7, f.getVrsta_usluge());
            stmt.setDouble(8, f.getCijena_po_km());
            stmt.setInt(9, f.getBroj_km());
            stmt.setDouble(10, f.getIznos_usluge());
            stmt.setDouble(11, f.getPorez());
            stmt.setDouble(12, f.getUkupan_iznos());
            stmt.setString(13, f.getStatus_placanja());
            stmt.setString(14, f.getNacin_placanja());
            stmt.setDate(15, f.getDatum_placanja() != null ? Date.valueOf(f.getDatum_placanja()) : null);
            stmt.setString(16, f.getNapomena());
            stmt.setString(17, f.getDatoteka_path());
            stmt.setBoolean(18, true); // aktivan

            stmt.executeUpdate();
        }
    }


    public void update(Fakture f) throws SQLException {
        String query = "UPDATE fakture SET broj_fakture=?, tura_id=?, klijent_id=?, datum_izdavanja=?, odobrio_admin_id=?, " +
                "`datum_dospjeća`=?, vrsta_usluge=?, cijena_po_km=?, broj_km=?, iznos_usluge=?, porez=?, ukupan_iznos=?, " +
                "status_placanja=?, nacin_placanja=?, datum_placanja=?, napomena=?, datoteka_path=? WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, f.getBroj_fakture());
            stmt.setInt(2, f.getTura_id());
            stmt.setInt(3, f.getKlijent_id());
            stmt.setDate(4, f.getDatum_izdavanja() != null ? Date.valueOf(f.getDatum_izdavanja()) : null);
            stmt.setInt(5, f.getOdobrio_admin_id());
            stmt.setDate(6, f.getDatum_dospjeca() != null ? Date.valueOf(f.getDatum_dospjeca()) : null);
            stmt.setString(7, f.getVrsta_usluge());
            stmt.setDouble(8, f.getCijena_po_km());
            stmt.setInt(9, f.getBroj_km());
            stmt.setDouble(10, f.getIznos_usluge());
            stmt.setDouble(11, f.getPorez());
            stmt.setDouble(12, f.getUkupan_iznos());
            stmt.setString(13, f.getStatus_placanja());
            stmt.setString(14, f.getNacin_placanja());
            stmt.setDate(15, f.getDatum_placanja() != null ? Date.valueOf(f.getDatum_placanja()) : null);
            stmt.setString(16, f.getNapomena());
            stmt.setString(17, f.getDatoteka_path());
            stmt.setInt(18, f.getId()); // ID za WHERE uslov

            stmt.executeUpdate();
        }
    }

    public void updateStatus(int id, String status) throws SQLException {
        String query = "UPDATE fakture SET status_placanja = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String query = "UPDATE fakture SET aktivan = FALSE WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Fakture mapResultSetToFakture(ResultSet rs) throws SQLException {
        Fakture f = new Fakture();
        f.setId(rs.getInt("id"));
        f.setBroj_fakture(rs.getString("broj_fakture"));
        f.setTura_id(rs.getInt("tura_id"));
        f.setKlijent_id(rs.getInt("klijent_id"));

        if (rs.getDate("datum_izdavanja") != null)
            f.setDatum_izdavanja(rs.getDate("datum_izdavanja").toLocalDate());

        if (rs.getDate("datum_dospjeca") != null)
            f.setDatum_dospjeca(rs.getDate("datum_dospjeca").toLocalDate());

        if (rs.getDate("datum_placanja") != null)
            f.setDatum_placanja(rs.getDate("datum_placanja").toLocalDate());

        f.setOdobrio_admin_id(rs.getInt("odobrio_admin_id"));
        f.setVrsta_usluge(rs.getString("vrsta_usluge"));
        f.setCijena_po_km(rs.getDouble("cijena_po_km"));
        f.setBroj_km(rs.getInt("broj_km"));
        f.setIznos_usluge(rs.getDouble("iznos_usluge"));
        f.setPorez(rs.getDouble("porez"));
        f.setUkupan_iznos(rs.getDouble("ukupan_iznos"));
        f.setStatus_placanja(rs.getString("status_placanja"));
        f.setNacin_placanja(rs.getString("nacin_placanja"));
        f.setNapomena(rs.getString("napomena"));
        f.setDatoteka_path(rs.getString("datoteka_path"));
        f.setAktivan(rs.getBoolean("aktivan"));

        Timestamp ts = rs.getTimestamp("datum_kreiranja");
        if (ts != null) f.setDatum_kreiranja(ts.toLocalDateTime());

        return f;
    }
}