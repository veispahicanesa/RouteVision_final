package unze.ptf.routevision_final.controller;


public class SessionManager {
    private static SessionManager instance;
    private Object currentUser;
    private String userRole;
    private int userId;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(Object user, String role, int id) {
        this.currentUser = user;
        this.userRole = role;
        this.userId = id;
    }

    public Object getCurrentUser() {
        return currentUser;
    }

    public String getUserRole() {
        return userRole;
    }

    public int getUserId() {
        return userId;
    }

    public void logout() {
        this.currentUser = null;
        this.userRole = null;
        this.userId = 0;
    }
}
