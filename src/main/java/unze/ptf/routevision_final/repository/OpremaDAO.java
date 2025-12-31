package unze.ptf.routevision_final.repository;

import unze.ptf.routevision_final.config.DatabaseConfig;
import unze.ptf.routevision_final.model.Oprema;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OpremaDAO {
    public Oprema findById(int id) throws SQLException {
        String query = "SELECT * FROM oprema WHERE id = ? AND aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapResultSetToOprema(rs);
        }
        return null;
    }

    public List<Oprema> findAll() throws SQLException {
        List<Oprema> opreme = new ArrayList<>();
        String query = "SELECT * FROM oprema WHERE aktivan = TRUE ORDER BY naziv";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) opreme.add(mapResultSetToOprema(rs));
        }
        return opreme;
    }

    public List<Oprema> findForVozac(int vozacId) throws SQLException {
        List<Oprema> lista = new ArrayList<>();
        String query = "SELECT o.* FROM oprema o JOIN kamion k ON o.kamion_id = k.id WHERE k.vozac_id = ? AND o.aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, vozacId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapResultSetToOprema(rs));
            }
        }
        return lista;
    }

    public void save(Oprema o) throws SQLException {
        String query = "INSERT INTO oprema (naziv, vrsta, kapacitet, stanje, napomena, datum_nabavke, datum_zadnje_provjere, aktivan) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, o.getNaziv());
            stmt.setString(2, o.getVrsta());
            stmt.setDouble(3, o.getKapacitet());
            stmt.setString(4, o.getStanje());
            stmt.setString(5, o.getNapomena());
            stmt.setDate(6, o.getDatum_nabavke() != null ? Date.valueOf(o.getDatum_nabavke()) : null);
            stmt.setDate(7, o.getDatum_zadnje_provjere() != null ? Date.valueOf(o.getDatum_zadnje_provjere()) : null);
            stmt.setBoolean(8, true);
            stmt.executeUpdate();
        }
    }

    public void update(Oprema o) throws SQLException {
        String query = "UPDATE oprema SET naziv = ?, vrsta = ?, kapacitet = ?, stanje = ?, napomena = ?, datum_nabavke = ?, datum_zadnje_provjere = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, o.getNaziv());
            stmt.setString(2, o.getVrsta());
            stmt.setDouble(3, o.getKapacitet());
            stmt.setString(4, o.getStanje());
            stmt.setString(5, o.getNapomena());
            stmt.setDate(6, o.getDatum_nabavke() != null ? Date.valueOf(o.getDatum_nabavke()) : null);
            stmt.setDate(7, o.getDatum_zadnje_provjere() != null ? Date.valueOf(o.getDatum_zadnje_provjere()) : null);
            stmt.setInt(8, o.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String query = "UPDATE oprema SET aktivan = FALSE WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Oprema mapResultSetToOprema(ResultSet rs) throws SQLException {
        Oprema o = new Oprema();
        o.setId(rs.getInt("id"));
        o.setNaziv(rs.getString("naziv"));
        o.setVrsta(rs.getString("vrsta"));
        o.setKapacitet(rs.getDouble("kapacitet"));
        o.setStanje(rs.getString("stanje"));
        o.setAktivan(rs.getBoolean("aktivan"));
        o.setNapomena(rs.getString("napomena"));

        if (rs.getDate("datum_nabavke") != null) {
            o.setDatum_nabavke(rs.getDate("datum_nabavke").toLocalDate());
        }
        if (rs.getDate("datum_zadnje_provjere") != null) {
            o.setDatum_zadnje_provjere(rs.getDate("datum_zadnje_provjere").toLocalDate());
        }
        return o;
    }
}