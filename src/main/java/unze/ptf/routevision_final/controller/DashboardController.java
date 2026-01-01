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
    private void handleDownloadReport() {
        try {
            // 1. Identifikacija ulogovanog korisnika
            Object ulogovani = unze.ptf.routevision_final.controller.SessionManager.getInstance().getCurrentUser();
            String imeKorisnika = "Sistemski Generisano";
            if (ulogovani instanceof unze.ptf.routevision_final.model.Admin a) {
                imeKorisnika = a.getIme() + " " + a.getPrezime();
            } else if (ulogovani instanceof unze.ptf.routevision_final.model.Vozac v) {
                imeKorisnika = v.getIme() + " " + v.getPrezime();
            }

            // 2. Podaci
            var vDAO = new unze.ptf.routevision_final.repository.VozacDAO();
            var tDAO = new unze.ptf.routevision_final.repository.TuraDAO();
            var sDAO = new unze.ptf.routevision_final.repository.ServisniDnevnikDAO();
            var fDAO = new unze.ptf.routevision_final.repository.FaktureDAO();

            var vozaci = vDAO.findAll();
            var ture = tDAO.findAll();
            var servisi = sDAO.findAll();
            var fakture = fDAO.findAll();

            // 3. Proračuni
            double ukupnoPrihodi = fakture.stream().mapToDouble(f -> f.getUkupan_iznos()).sum();
            double ukupnoServis = servisi.stream().mapToDouble(s -> s.getTroskovi()).sum();
            double dobit = ukupnoPrihodi - ukupnoServis;
            double prosjekPlata = vozaci.stream().mapToDouble(v -> v.getPlata()).average().orElse(0);
            long aktivneTure = ture.stream().filter(t -> "U TOKU".equalsIgnoreCase(t.getStatus())).count();

            // 4. HTML Šablon
            String htmlContent = """
            <!DOCTYPE html>
            <html lang="bs">
            <head>
                <meta charset="UTF-8" />
                <style>
                    body { font-family: 'Arial', sans-serif; margin: 0; padding: 0; color: #2c3e50; }
                    .top-bar { background: #1a2a6c; color: white; padding: 10px; text-align: center; font-size: 10px; letter-spacing: 2px; }
                    .header { background: linear-gradient(to right, #1e3c72, #2a5298); color: white; padding: 40px; text-align: center; border-bottom: 5px solid #d4af37; }
                    .header h1 { margin: 0; font-size: 28px; text-transform: uppercase; }
                    .user-info { background: #f8f9fa; padding: 10px 30px; font-size: 11px; text-align: right; border-bottom: 1px solid #ddd; }
                    .container { padding: 30px; }
                    
                    .kpi-row { display: table; width: 100%; border-spacing: 15px 0; margin-bottom: 30px; }
                    .kpi-card { display: table-cell; background: #fff; border: 1px solid #ddd; padding: 15px; border-radius: 8px; text-align: center; }
                    .kpi-val { display: block; font-size: 20px; font-weight: bold; color: #1e3c72; }
                    .kpi-label { font-size: 9px; color: #7f8c8d; text-transform: uppercase; }
                    
                    .section-title { font-size: 15px; font-weight: bold; color: #1e3c72; border-left: 5px solid #d4af37; padding-left: 10px; margin-top: 35px; margin-bottom: 10px; }
                    
                    table { width: 100%; border-collapse: collapse; margin-top: 10px; }
                    th { background: #1e3c72; color: white; padding: 10px; font-size: 10px; text-align: left; }
                    td { border-bottom: 1px solid #eee; padding: 8px; font-size: 10px; }
                    tr:nth-child(even) { background: #f9f9f9; }
                    
                    .badge { padding: 3px 8px; border-radius: 15px; font-size: 8px; font-weight: bold; color: white; }
                    .bg-ok { background: #27ae60; }
                    .bg-warn { background: #f39c12; }
                    .bg-err { background: #e74c3c; }
                    
                    .footer { margin-top: 50px; text-align: center; font-size: 9px; color: #aaa; border-top: 1px solid #eee; padding-top: 15px; }
                </style>
            </head>
            <body>
                <div class="top-bar">POVJERLJIVI LOGISTI#KI PODACI - ROUTEVISION FINAL</div>
                <div class="header">
                    <h1>MASTER IZVJEŠTAJ POSLOVANJA</h1>
                    <p style="opacity: 0.8; font-size: 12px;">Sveobuhvatni pregled transporta, ljudstva i finansija</p>
                </div>
                <div class="user-info">
                    Izvještaj generisao: <strong>{{KORISNIK}}</strong> | Datum: {{VRIJEME}}
                </div>

                <div class="container">
                    <div class="kpi-row">
                        <div class="kpi-card">
                            <span class="kpi-val">{{DOBIT}} KM</span>
                            <span class="kpi-label">Čista Dobit</span>
                        </div>
                        <div class="kpi-card">
                            <span class="kpi-val">{{PROSJEK}} KM</span>
                            <span class="kpi-label">Prosjek Plata</span>
                        </div>
                        <div class="kpi-card">
                            <span class="kpi-val">{{AKTIVNE}}</span>
                            <span class="kpi-label">Tura u toku</span>
                        </div>
                    </div>

                    <div class="section-title">LOGISTIČKI PREGLED: TRANSPORTNE TURE</div>
                    {{TABELA_TURE}}

                    <div class="section-title">FINANSIJSKI PREGLED: FAKTURE</div>
                    {{TABELA_FAKTURE}}

                    <div class="section-title">LJUDSKI RESURSI I ODRŽAVANJE</div>
                    {{TABELA_VOZACI}}

                    <div class="footer">
                        Dokument je digitalno generisan i punova#an bez pe#ata. &#169; 2026 HN Logistics.
                    </div>
                </div>
            </body>
            </html>
            """;

            // 5. Zamjena placeholdera
            htmlContent = htmlContent.replace("{{KORISNIK}}", imeKorisnika)
                    .replace("{{VRIJEME}}", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
                    .replace("{{DOBIT}}", String.format("%.2f", dobit))
                    .replace("{{PROSJEK}}", String.format("%.2f", prosjekPlata))
                    .replace("{{AKTIVNE}}", String.valueOf(aktivneTure))
                    .replace("{{TABELA_TURE}}", buildTureSve(ture))
                    .replace("{{TABELA_FAKTURE}}", buildFaktureSve(fakture))
                    .replace("{{TABELA_VOZACI}}", buildVozaciSve(vozaci));

            // 6. Snimanje
            FileChooser fc = new FileChooser();
            fc.setInitialFileName("HN_Logistics_Master_Report.pdf");
            java.io.File file = fc.showSaveDialog(rootLayout.getScene().getWindow());

            if (file != null) {
                new unze.ptf.routevision_final.service.PdfService().exportHtmlToPdf(htmlContent, file.getAbsolutePath());
                java.awt.Desktop.getDesktop().open(file);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

// --- POMOĆNE METODE ZA TABELE ---

    private String buildTureSve(java.util.List<unze.ptf.routevision_final.model.Tura> lista) {
        if (lista.isEmpty()) return "<p>Nema zabilježenih tura.</p>";
        StringBuilder sb = new StringBuilder("<table><thead><tr><th>Relacija (Od - Do)</th><th>Kilometraža</th><th>Vrijeme Polaska</th><th>Status</th></tr></thead><tbody>");
        for (var t : lista) {
            String status = t.getStatus() != null ? t.getStatus() : "NEPOZNATO";
            String klasa = status.equalsIgnoreCase("ZAVRŠENO") ? "bg-ok" : (status.equalsIgnoreCase("U TOKU") ? "bg-warn" : "bg-err");

            sb.append("<tr><td>").append(t.getLokacija_pocetka()).append(" - ").append(t.getLokacija_kraja()).append("</td>")
                    .append("<td>").append(t.getVrijeme_pocetka()).append("</td>")
                    .append("<td><span class='badge ").append(klasa).append("'>").append(status).append("</span></td></tr>");
        }
        return sb.append("</tbody></table>").toString();
    }

    private String buildVozaciSve(java.util.List<unze.ptf.routevision_final.model.Vozac> lista) {
        StringBuilder sb = new StringBuilder("<table><thead><tr><th>Ime i prezime</th><th>Telefon</th><th>Plata</th></tr></thead><tbody>");
        for (var v : lista) {
            sb.append("<tr><td>").append(v.getIme()).append(" ").append(v.getPrezime()).append("</td>")
                    .append("<td>").append(String.format("%.2f", v.getPlata())).append(" KM</td></tr>");
        }
        return sb.append("</tbody></table>").toString();
    }

    private String buildFaktureSve(java.util.List<unze.ptf.routevision_final.model.Fakture> lista) {
        StringBuilder sb = new StringBuilder("<table><thead><tr><th>Broj Fakture</th><th>Ukupni Iznos</th><th>Status Naplate</th></tr></thead><tbody>");
        for (var f : lista) {
            String status = f.getStatus_placanja() != null ? f.getStatus_placanja() : "U OBRADI";
            String klasa = status.equalsIgnoreCase("PLAĆENO") ? "bg-ok" : "bg-err";
            sb.append("<tr><td>").append(f.getBroj_fakture()).append("</td>")
                    .append("<td><strong>").append(String.format("%.2f", f.getUkupan_iznos())).append(" KM</strong></td>")
                    .append("<td><span class='badge ").append(klasa).append("'>").append(status).append("</span></td></tr>");
        }
        return sb.append("</tbody></table>").toString();
    }
}