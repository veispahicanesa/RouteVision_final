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
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapResultSetToOprema(rs));
        }
        return lista;
    }

    public void save(Oprema o) throws SQLException {
        String query = "INSERT INTO oprema (naziv, vrsta, kamion_id, kapacitet, stanje, napomena, aktivan) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, o.getNaziv());
            stmt.setString(2, o.getVrsta());
            stmt.setObject(3, o.getKamion_id());
            stmt.setDouble(4, o.getKapacitet());
            stmt.setString(5, o.getStanje());
            stmt.setString(6, o.getNapomena());
            stmt.setBoolean(7, true);
            stmt.executeUpdate();
        }
    }

    public void update(Oprema o) throws SQLException {
        String query = "UPDATE oprema SET naziv = ?, vrsta = ?, stanje = ?, napomena = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, o.getNaziv());
            stmt.setString(2, o.getVrsta());
            stmt.setString(3, o.getStanje());
            stmt.setString(4, o.getNapomena());
            stmt.setInt(5, o.getId());
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
        o.setKamion_id(rs.getObject("kamion_id") != null ? rs.getInt("kamion_id") : null);
        o.setKapacitet(rs.getDouble("kapacitet"));
        o.setStanje(rs.getString("stanje"));
        o.setAktivan(rs.getBoolean("aktivan"));
        return o;
    }
}