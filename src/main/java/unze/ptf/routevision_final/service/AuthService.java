package unze.ptf.routevision_final.service;

import org.mindrot.jbcrypt.BCrypt;
import unze.ptf.routevision_final.model.Admin;
import unze.ptf.routevision_final.model.Vozac;
import unze.ptf.routevision_final.repository.AdminDAO;
import unze.ptf.routevision_final.repository.VozacDAO;

import java.sql.SQLException;

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
        Vozac vozac = new Vozac(ime, prezime, email, SecurityService.hashPassword(password), broj_vozacke_dozvole);
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
