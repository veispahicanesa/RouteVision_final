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
            if (rs.next()) {
                return mapResultSetToOprema(rs);
            }
        }
        return null;
    }

    public List<Oprema> findAll() throws SQLException {
        List<Oprema> opreme = new ArrayList<>();
        String query = "SELECT * FROM oprema WHERE aktivan = TRUE ORDER BY naziv";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                opreme.add(mapResultSetToOprema(rs));
            }
        }
        return opreme;
    }

    public List<Oprema> findByKamionId(int kamionId) throws SQLException {
        List<Oprema> opreme = new ArrayList<>();
        String query = "SELECT * FROM oprema WHERE kamion_id = ? AND aktivan = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, kamionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                opreme.add(mapResultSetToOprema(rs));
            }
        }
        return opreme;
    }

    public void save(Oprema oprema) throws SQLException {
        String query = "INSERT INTO oprema (naziv, vrsta, kamion_id, kapacitet, stanje, datum_nabavke, datum_zadnje_provjere, napomena, aktivan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, oprema.getNaziv());
            stmt.setString(2, oprema.getVrsta());
            stmt.setObject(3, oprema.getKamion_id());
            stmt.setDouble(4, oprema.getKapacitet());
            stmt.setString(5, oprema.getStanje());
            stmt.setDate(6, oprema.getDatum_nabavke() != null ? Date.valueOf(oprema.getDatum_nabavke()) : null);
            stmt.setDate(7, oprema.getDatum_zadnje_provjere() != null ? Date.valueOf(oprema.getDatum_zadnje_provjere()) : null);
            stmt.setString(8, oprema.getNapomena());
            stmt.setBoolean(9, oprema.isAktivan());
            stmt.executeUpdate();
        }
    }

    public void update(Oprema oprema) throws SQLException {
        String query = "UPDATE oprema SET naziv = ?, vrsta = ?, kamion_id = ?, kapacitet = ?, stanje = ?, datum_nabavke = ?, datum_zadnje_provjere = ?, napomena = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, oprema.getNaziv());
            stmt.setString(2, oprema.getVrsta());
            stmt.setObject(3, oprema.getKamion_id());
            stmt.setDouble(4, oprema.getKapacitet());
            stmt.setString(5, oprema.getStanje());
            stmt.setDate(6, oprema.getDatum_nabavke() != null ? Date.valueOf(oprema.getDatum_nabavke()) : null);
            stmt.setDate(7, oprema.getDatum_zadnje_provjere() != null ? Date.valueOf(oprema.getDatum_zadnje_provjere()) : null);
            stmt.setString(8, oprema.getNapomena());
            stmt.setInt(9, oprema.getId());
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
        Oprema oprema = new Oprema();
        oprema.setId(rs.getInt("id"));
        oprema.setNaziv(rs.getString("naziv"));
        oprema.setVrsta(rs.getString("vrsta"));
        oprema.setKamion_id(rs.getObject("kamion_id") != null ? rs.getInt("kamion_id") : null);
        oprema.setKapacitet(rs.getDouble("kapacitet"));
        oprema.setStanje(rs.getString("stanje"));
        oprema.setDatum_nabavke(rs.getDate("datum_nabavke") != null ? rs.getDate("datum_nabavke").toLocalDate() : null);
        oprema.setDatum_zadnje_provjere(rs.getDate("datum_zadnje_provjere") != null ? rs.getDate("datum_zadnje_provjere").toLocalDate() : null);
        oprema.setNapomena(rs.getString("napomena"));
        oprema.setAktivan(rs.getBoolean("aktivan"));
        oprema.setDatum_kreiranja(rs.getTimestamp("datum_kreiranja") != null ? rs.getTimestamp("datum_kreiranja").toLocalDateTime() : null);
        return oprema;
    }
}