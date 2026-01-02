package unze.ptf.routevision_final.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import unze.ptf.routevision_final.HelloApplication;
import unze.ptf.routevision_final.model.Admin;
import unze.ptf.routevision_final.model.Fakture;
import unze.ptf.routevision_final.model.Vozac;
import unze.ptf.routevision_final.repository.FaktureDAO;
import unze.ptf.routevision_final.repository.ServisniDnevnikDAO;
import unze.ptf.routevision_final.repository.TuraDAO;
import unze.ptf.routevision_final.repository.VozacDAO;
import unze.ptf.routevision_final.service.PdfService;
import unze.ptf.routevision_final.service.ReportService;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

public class DashboardController {

    @FXML
    private BorderPane rootLayout;
    @FXML
    private VBox sideMenu;
    @FXML
    private VBox contentArea;
    @FXML
    private Label userLabel;
    private boolean isDark = true;
    @FXML
    private Button btnTheme;
    private SessionManager sessionManager = SessionManager.getInstance();

    @FXML
    public void initialize() {
        setupUserHeader();
        createDynamicMenu();
        if (btnTheme != null) {
            btnTheme.setText(SessionManager.isDarkMode() ? "SVIJETLI MOD" : "TAMNI MOD");
        }
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

    @FXML
    private void handleHome() {
        // Čistimo trenutni sadržaj da bi se vidio početni pozdrav
        contentArea.getChildren().clear();

        // Ovdje možeš ili učitati poseban HomeView.fxml
        // loadView("HomeView.fxml");

        // Ili jednostavno dodati početne labele nazad ako nemaš poseban FXML
        Label welcome = new Label("Dobrodošli u RouteVision");
        welcome.getStyleClass().add("welcome-text"); // Dodaj ovaj stil u CSS

        Label subtext = new Label("Odaberite opciju iz menija sa lijeve strane.");
        subtext.getStyleClass().add("sub-welcome-text");

        contentArea.getChildren().addAll(welcome, subtext);
    }

    private void createDynamicMenu() {
        String userRole = sessionManager.getUserRole();
        sideMenu.getChildren().clear();
        Button homeBtn = createMenuBtn("POČETNA STRANA", e -> handleHome());
        homeBtn.getStyleClass().add("menu-button-home"); // Poseban stil za Home
        sideMenu.getChildren().add(homeBtn);
        if ("Admin".equals(userRole)) {
            sideMenu.getChildren().addAll(

                    createMenuBtn("Profil", e -> showProfile()),
                    createMenuBtn("Administratori", e -> showAdmini()),
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
        btn.setAlignment(javafx.geometry.Pos.CENTER_LEFT); // Poravnanje lijevo
        btn.setPadding(new javafx.geometry.Insets(15, 20, 15, 20));
        btn.setOnAction(event);
        return btn;
    }


    private void loadView(String fxmlFile) {
        try {
            String path = "/unze/ptf/view/" + fxmlFile;
            java.net.URL resource = getClass().getResource(path);

            if (resource == null) {
                System.err.println("KRITIČNA GREŠKA: Ne mogu pronaći fajl na putanji: " + path);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);

            // POPRAVKA: Koristimo 'Node' umjesto 'VBox' jer je Node zajednički za sve (i VBox i ScrollPane)
            javafx.scene.Node view = loader.load();

            // Ako želiš da se sadržaj raširi preko cijelog ekrana:
            if (view instanceof javafx.scene.layout.Region) {
                ((javafx.scene.layout.Region) view).setMaxWidth(Double.MAX_VALUE);
                ((javafx.scene.layout.Region) view).setMaxHeight(Double.MAX_VALUE);
            }

            contentArea.getChildren().setAll(view);

        } catch (IOException e) {
            System.err.println("Greška pri parsiranju FXML fajla: " + fxmlFile);
            e.printStackTrace();
        }
    }

    private void showProfile() {
        loadView("ProfileView.fxml");
    }

    private void showVozaci() {
        loadView("VozacManagementView.fxml");
    }

    private void showKamioni() {
        // Pazi: na tvojoj slici piše "Managment" (bez e)
        loadView("KamionManagementView.fxml");
    }

    private void showOprema() {
        loadView("OpremaManagementView.fxml");
    }

    private void showKlijenti() {
        loadView("KlijentManagementView.fxml");
    }

    private void showFakture() {
        loadView("FaktureManagementView.fxml");
    }

    private void showServisniDnevnik() {
        loadView("ServisniDnevnikManagementView.fxml");
    }

    private void showTura() {
        loadView("TuraView.fxml");
    }

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

    private void showAdmini() {
        loadView("admin-list.fxml");
    }


    //anesa
    // Dodaj ovu varijablu na vrh klase (ispod userLabel)
    @FXML
    private void toggleTheme() {
        isDark = !isDark;

        // Dohvatamo ROOT cijele scene
        Parent root = rootLayout.getScene().getRoot();

        if (isDark) {
            root.getStyleClass().add("dark-mode");
            // Također dodajemo na contentArea za svaki slučaj
            contentArea.getStyleClass().add("dark-mode");
            btnTheme.setText("SVIJETLI MOD");
        } else {
            root.getStyleClass().remove("dark-mode");
            contentArea.getStyleClass().remove("dark-mode");
            btnTheme.setText("TAMNI MOD");
        }
    }


    @FXML
    private void handleDownloadServiceReport() {
        saveAndOpenPdf("Servisni_Izvjestaj", "service");
    }

    // Za opšti/master izvještaj (Ture)
    @FXML
    private void handleDownloadReport() {
        saveAndOpenPdf("Izvjestaj_Tura", "standard");
    }

    // Za finansijski izvještaj
    @FXML
    private void handleDownloadFinancialReport() {
        saveAndOpenPdf("Finansijski_Izvjestaj", "financial");
    }

    // Pomoćna metoda da ne ponavljaš isti kod FileChooser-a
    private void saveAndOpenPdf(String defaultName, String type) {
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Sačuvaj PDF izvještaj");
            fc.setInitialFileName(defaultName + "_" + System.currentTimeMillis() + ".pdf");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

            java.io.File file = fc.showSaveDialog(rootLayout.getScene().getWindow());

            if (file != null) {
                ReportService reportService = new ReportService();

                // Ispravljena logika grananja
                if ("service".equals(type)) {
                    reportService.generateServiceReport(file.getAbsolutePath());
                } else if ("financial".equals(type)) {
                    reportService.generateFinancialReport(file.getAbsolutePath());
                } else {
                    reportService.generateReport(file.getAbsolutePath());
                }

                // Otvaranje fajla nakon generisanja
                if (java.awt.Desktop.isDesktopSupported()) {
                    java.awt.Desktop.getDesktop().open(file);
                }
            }
        } catch (Exception e) {
            System.err.println("Greška pri generisanju PDF-a: " + e.getMessage());
            e.printStackTrace();
        }
    }
}