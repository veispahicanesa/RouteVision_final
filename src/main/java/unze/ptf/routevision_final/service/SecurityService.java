package unze.ptf.routevision_final.service;

import org.mindrot.jbcrypt.BCrypt;
/*
 * SecurityService - Servis za sigurnu obradu lozinki.
 * Omogućava hash-ovanje lozinki i provjeru lozinke protiv hash-a koristeći BCrypt.
 */
public class SecurityService {
    private static final int LOG_ROUNDS = 12;

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(LOG_ROUNDS));
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}