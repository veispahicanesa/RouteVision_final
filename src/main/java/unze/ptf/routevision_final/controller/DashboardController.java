package unze.ptf.routevision_final.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import unze.ptf.routevision_final.model.Admin;
import unze.ptf.routevision_final.model.Vozac;

import java.io.IOException;

public class DashboardController {

    @FXML private BorderPane rootLayout;
    @FXML private VBox sideMenu;
    @FXML private VBox contentArea;
    @FXML private Label userLabel;

    private SessionManager sessionManager = SessionManager.getInstance();

    @FXML
    public void initialize() {
        setupUserHeader();
        createDynamicMenu();
    }

    private void setupUserHeader() {
        String userRole = sessionManager.getUserRole();
        String userName = "";

        if ("Admin".equals(userRole)) {
            Admin admin = (Admin) sessionManager.getCurrentUser();
            userName = admin.getIme() + " " + admin.getPrezime();
        } else {
            Vozac vozac = (Vozac) sessionManager.getCurrentUser();
            userName = vozac.getIme() + " " + vozac.getPrezime();
        }
        userLabel.setText("Korisnik: " + userName + " (" + userRole + ")");
    }

    private void createDynamicMenu() {
        String userRole = sessionManager.getUserRole();
        sideMenu.getChildren().clear();

        if ("Admin".equals(userRole)) {
            sideMenu.getChildren().addAll(
                    createMenuBtn("Profil", e -> showProfile()),
                    createMenuBtn("Vozači", e -> showVozaci()),
                    createMenuBtn("Kamioni", e -> showKamioni()),
                    createMenuBtn("Oprema", e -> showOprema()),
                    createMenuBtn("Klijenti", e -> showKlijenti()),
                    createMenuBtn("Fakture", e -> showFakture()),
                    createMenuBtn("Servisni Dnevnik", e -> showServisniDnevnik()),
                    createMenuBtn("Ture", e -> showTura()) // Popravljeno gniježđenje
            );
        } else {
            sideMenu.getChildren().addAll(
                    createMenuBtn("Moj Profil", e -> showProfile()),
                    createMenuBtn("Moje Ture", e -> showTura()),
                    createMenuBtn("Servisni Dnevnik", e -> showServisniDnevnik()),
                    createMenuBtn("Moj Kamion", e -> showKamioni()),
                    createMenuBtn("Oprema", e -> showOprema()),
                    createMenuBtn("Fakture/Računi", e -> showFakture())
            );
        }
    }
    private Button createMenuBtn(String text, javafx.event.EventHandler<javafx.event.ActionEvent> event) {
        Button btn = new Button(text);
        btn.getStyleClass().add("menu-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(event);
        return btn;
    }

    // --- METODE ZA UCITAVANJE VIEW-OVA ---

    private void loadView(String fxmlFile) {
        try {
            // Putanja koju koristimo (mora početi sa /)
            String path = "/unze/ptf/view/" + fxmlFile;

            java.net.URL resource = getClass().getResource(path);

            // TEST: Ako je resource null, ispiši tačno šta fali
            if (resource == null) {
                System.err.println("KRITIČNA GREŠKA: Ne mogu pronaći fajl na putanji: " + path);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            VBox view = loader.load();
            contentArea.getChildren().setAll(view);

        } catch (IOException e) {
            System.err.println("Greška pri parsiranju FXML fajla: " + fxmlFile);
            e.printStackTrace();
        }
    }

    private void showProfile() { loadView("ProfileView.fxml"); }

    private void showVozaci() { loadView("VozacManagementView.fxml"); }

    private void showKamioni() {
        // Pazi: na tvojoj slici piše "Managment" (bez e)
        loadView("KamionManagmentView.fxml");
    }

    private void showOprema() { loadView("OpremaManagementView.fxml"); }

    private void showKlijenti() { loadView("KlijentManagementView.fxml"); }

    private void showFakture() { loadView("FaktureManagementView.fxml"); }

    private void showServisniDnevnik() { loadView("ServisniDnevnikManagementView.fxml"); }

    private void showTura() { loadView("TuraView.fxml"); }
    @FXML
    private void handleLogout() {
        try {
            sessionManager.logout();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unze/ptf/view/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            ((Stage) rootLayout.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}