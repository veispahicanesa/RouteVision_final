package unze.ptf.routevision_final.service;

import org.mindrot.jbcrypt.BCrypt;
import unze.ptf.routevision_final.model.Admin;
import unze.ptf.routevision_final.model.Vozac;
import unze.ptf.routevision_final.repository.AdminDAO;
import unze.ptf.routevision_final.repository.VozacDAO;

import java.sql.SQLException;
/*
 * AuthService - Servis za autentifikaciju i registraciju korisnika.
 * Podržava logovanje i registraciju za Admina i Vozača.
 * Lozinke se hash-uju pomoću BCrypt metode.
 */
public class AuthService {
    private AdminDAO adminDAO = new AdminDAO();
    private VozacDAO vozacDAO = new VozacDAO();

    public Object authenticate(String email, String password, String role) throws SQLException {
        if ("Admin".equals(role)) {
            Admin admin = adminDAO.findByEmail(email);
            if (admin != null && SecurityService.verifyPassword(password, admin.getLozinka())) {
                return admin;
            }
        } else if ("Vozač".equals(role)) {
            Vozac vozac = vozacDAO.findByEmail(email);
            if (vozac != null && SecurityService.verifyPassword(password, vozac.getLozinka())) {
                return vozac;
            }
        }
        return null;
    }

    public void registerAdmin(String ime, String prezime, String email, String password, String broj_telefona) throws SQLException {
        Admin admin = new Admin(ime, prezime, email, SecurityService.hashPassword(password), broj_telefona);
        adminDAO.save(admin);
    }

    public void registerVozac(String ime, String prezime, String email, String password, String broj_vozacke_dozvole) throws SQLException {

        Vozac vozac = new Vozac();


        vozac.setIme(ime);
        vozac.setPrezime(prezime);
        vozac.setEmail(email);
        vozac.setLozinka(SecurityService.hashPassword(password));
        vozac.setBroj_vozacke_dozvole(broj_vozacke_dozvole);


        vozac.setPlata(0.0);
        vozac.setMarka_kamiona("N/A");
        vozac.setTip_goriva("Dizel");
        vozac.setKategorija_dozvole("B"); // ili neka druga početna kategorija
        vozac.setAktivan(true);
        vozac.setDatum_zaposlenja(java.time.LocalDate.now()); // Popravljeno: LocalDate.now()
        vozac.setDatum_kreiranja(java.time.LocalDateTime.now());


        vozacDAO.save(vozac);
    }

    public static class SecurityService {
        private static final int LOG_ROUNDS = 12;

        public static String hashPassword(String password) {
            return BCrypt.hashpw(password, BCrypt.gensalt(LOG_ROUNDS));
        }

        public static boolean verifyPassword(String password, String hashedPassword) {
            return BCrypt.checkpw(password, hashedPassword);
        }
    }
}
