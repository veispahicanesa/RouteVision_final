package unze.ptf.routevision_final.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import unze.ptf.routevision_final.model.Admin;
import unze.ptf.routevision_final.model.Vozac;
import unze.ptf.routevision_final.service.AuthService;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    private AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        // Postavljanje inicijalne vrijednosti ComboBox-a ako nije u FXML-u
        if (roleCombo.getValue() == null) {
            roleCombo.setValue("Admin");
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleCombo.getValue();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Molimo popunite sva polja!");
            return;
        }

        try {
            Object user = authService.authenticate(email, password, role);
            if (user != null) {
                if (user instanceof Admin) {
                    Admin admin = (Admin) user;
                    SessionManager.getInstance().setCurrentUser(admin, "Admin", admin.getId());
                } else if (user instanceof Vozac) {
                    Vozac vozac = (Vozac) user;
                    SessionManager.getInstance().setCurrentUser(vozac, "Vozač", vozac.getId());
                }

                // Pronađi deo koda koji otvara Dashboard nakon uspešne prijave
                try {
                    // Putanja mora odgovarati novoj strukturi u resources
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/unze/ptf/view/DashboardView.fxml"));

                    Parent root = loader.load(); // Ovo je linija 58 koja je bacala grešku

                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("RouteVision - Dashboard");
                    stage.show();

                    // Zatvaranje Login prozora
                    ((Stage) loginButton.getScene().getWindow()).close();

                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Greška", "Nije moguće učitati glavni prozor.");
                }
                ((Stage) emailField.getScene().getWindow()).close();
            } else {
                errorLabel.setText("Pogrešan email ili lozinka!");
            }
        } catch (Exception ex) {
            errorLabel.setText("Greška pri povezivanju na bazu!");
            ex.printStackTrace();
        }
    }

    private void showAlert(String greška, String s) {
    }
}