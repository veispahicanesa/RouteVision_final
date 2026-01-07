package unze.ptf.routevision_final.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import unze.ptf.routevision_final.HelloApplication;
import unze.ptf.routevision_final.model.Admin;
import unze.ptf.routevision_final.model.Vozac;
import unze.ptf.routevision_final.repository.FaktureDAO;
import unze.ptf.routevision_final.repository.TuraDAO;
import unze.ptf.routevision_final.service.PdfService;
import java.io.File;

import java.io.IOException;
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
        handleHome();
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
        contentArea.getChildren().clear();
        contentArea.setSpacing(20);
        contentArea.setPadding(new javafx.geometry.Insets(20));

        SessionManager session = SessionManager.getInstance();
        String userRole = session.getUserRole();

        Label welcome = new Label("Kontrolna tabla - HN Logistics");
        welcome.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        contentArea.getChildren().add(welcome);

        if ("Admin".equals(userRole)) {
            FaktureDAO dao = new FaktureDAO();
            double prihodi = dao.getUkupniPrihodi();
            double troskovi = dao.getTroskoviServisa() + dao.getTroskoviPlata();
            double profit = prihodi - troskovi;

            javafx.scene.layout.HBox mainLayout = new javafx.scene.layout.HBox(50); // Veći razmak (50)
            mainLayout.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            mainLayout.setPadding(new javafx.geometry.Insets(10, 0, 10, 0));

            javafx.scene.layout.VBox cardsSide = new javafx.scene.layout.VBox(20);
            cardsSide.getChildren().addAll(
                    createStatCard("UKUPNI PRIHODI", String.format("%.2f KM", prihodi), "#27ae60"),
                    createStatCard("UKUPNI TROŠKOVI", String.format("%.2f KM", troskovi), "#e67e22"),
                    createStatCard("NETO PROFIT", String.format("%.2f KM", profit), "#2980b9")
            );

            // GRAFIKON
            PieChart pieChart = new PieChart();
            PieChart.Data slicePrihod = new PieChart.Data("Prihodi", prihodi);
            PieChart.Data sliceTrosak = new PieChart.Data("Troškovi", troskovi);
            pieChart.getData().addAll(slicePrihod, sliceTrosak);
            pieChart.setPrefSize(500, 400);
            mainLayout.getChildren().addAll(cardsSide, pieChart);
            javafx.scene.layout.HBox.setHgrow(pieChart, javafx.scene.layout.Priority.ALWAYS);
            // DUGME
            Button btnExport = new Button("📥 GENERIŠI FINANSIJSKI IZVJEŠTAJ (PDF)");
            btnExport.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #f39c12, #d35400);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 15px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 12 30;" +
                            "-fx-background-radius: 30;" + // Zaobljeno dugme
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);" +
                            "-fx-cursor: hand;"
            );

            // Hover efekat za dugme
            btnExport.setOnMouseEntered(e -> btnExport.setStyle(btnExport.getStyle() + "-fx-background-color: linear-gradient(to bottom, #e67e22, #a04000);"));
            btnExport.setOnMouseExited(e -> btnExport.setStyle(btnExport.getStyle() + "-fx-background-color: linear-gradient(to bottom, #f39c12, #d35400);"));

            btnExport.setOnAction(e -> generateAdminPDF());
            contentArea.getChildren().addAll(mainLayout, btnExport);

            // BOJENJE (Jedan Platform.runLater je dovoljan)
            javafx.application.Platform.runLater(() -> {
                if (slicePrihod.getNode() != null) {
                    slicePrihod.getNode().setStyle("-fx-pie-color: #27ae60;");
                }
                if (sliceTrosak.getNode() != null) {
                    sliceTrosak.getNode().setStyle("-fx-pie-color: #e67e22;");
                }
                // Bojenje legende
                for (javafx.scene.Node node : pieChart.lookupAll(".chart-legend-item-symbol")) {
                    if (node.getStyleClass().contains("data0")) node.setStyle("-fx-background-color: #27ae60;");
                    if (node.getStyleClass().contains("data1")) node.setStyle("-fx-background-color: #e67e22;");
                }
            });

        } else {
            // --- POČETNA STRANA ZA VOZAČA ---
            Vozac trenutniVozac = (Vozac) sessionManager.getCurrentUser();
            TuraDAO turaDAO = new TuraDAO();

            // 1. Povlačenje svježih podataka isključivo za ulogovanog vozača
            int stvaranBrojTura = 0;
            try {
                // Ovo filtrira ture u bazi prema ID-u vozača iz sesije
                stvaranBrojTura = turaDAO.findByVozacId(trenutniVozac.getId()).size();
            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean darkAktiviran = SessionManager.isDarkMode();
            String dynamicTextColor = darkAktiviran ? "#FFFFFF" : "#2c3e50";

            // 2. Naslov i dobrodošlica
            VBox welcomePanel = new VBox(10);
            Label lblMain = new Label("Dobrodošli nazad, " + trenutniVozac.getIme() + "!");
            lblMain.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: -fx-m-text;");
            welcomePanel.getChildren().add(lblMain);

            // 3. Statističke kartice - KORISTIMO stvaranBrojTura
            javafx.scene.layout.HBox driverCards = new javafx.scene.layout.HBox(20);
            driverCards.getChildren().addAll(
                    createStatCard("MOJE TURE", String.valueOf(stvaranBrojTura), "#8e44ad"),
                    createStatCard("STATUS VOZILA", "ISPRAVNO", "#27ae60"),
                    createStatCard("AKTIVNA PLATA", String.format("%.2f KM", trenutniVozac.getPlata()), "#2980b9")
            );

            // 4. Progress Bar sekcija - KORISTIMO stvaranBrojTura
            VBox progressBox = new VBox(8);
            progressBox.setPadding(new javafx.geometry.Insets(10, 0, 10, 0));

            int cilj = 10;
            double procenat = (double) stvaranBrojTura / cilj;

            Label lblProgress = new Label();
            ProgressBar pb = new ProgressBar();
            pb.setPrefWidth(600);

            if (stvaranBrojTura >= cilj) {
                lblProgress.setText("ČESTITAMO! Bonus ostvaren (" + stvaranBrojTura + "/" + cilj + " tura)");
                lblProgress.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60; -fx-font-size: 14px;");
                pb.setProgress(1.0);
                pb.setStyle("-fx-accent: #27ae60;");
            } else {
                lblProgress.setText("Napredak do bonusa: još " + (cilj - stvaranBrojTura) + " ture do cilja");
                lblProgress.setStyle("-fx-font-weight: bold; -fx-text-fill: " + dynamicTextColor + "; -fx-font-size: 14px;");
                pb.setProgress(procenat);
                pb.setStyle("-fx-accent: #6c5ce7;");
            }
            progressBox.getChildren().addAll(lblProgress, pb);

            // 5. Action Panel
            VBox actionPanel = new VBox(20);
            actionPanel.setAlignment(javafx.geometry.Pos.CENTER);
            actionPanel.setPadding(new javafx.geometry.Insets(30));
            actionPanel.setMaxWidth(800);
            actionPanel.getStyleClass().add("action-card");

            Label lblQuestion = new Label("Trebate službeni dokument?");
            lblQuestion.setStyle("-fx-text-fill: -fx-m-text; -fx-font-size: 16px; -fx-font-weight: bold;");

            Label lblInfo = new Label("Generišite PDF karton sa vašim podacima.");
            lblInfo.setStyle("-fx-text-fill: -fx-mut-text; -fx-font-size: 13px;");

            Button btnPdf = new Button("PREUZMI MOJ KARTON (PDF)");
            btnPdf.getStyleClass().add("btn-pdf-premium");
            btnPdf.setOnAction(e -> handleGenerateMyDriverPDF(trenutniVozac));

            actionPanel.getChildren().addAll(lblQuestion, lblInfo, btnPdf);

            // 6. Finalno dodavanje u ContentArea
            contentArea.getChildren().clear();
            contentArea.getChildren().addAll(welcomePanel, driverCards, progressBox, actionPanel);
        }
    }

    // 3. DODAJ OVU METODU NA DNO KLASE (Rješenje za drugu grešku)
    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(5);
        card.setPadding(new javafx.geometry.Insets(15));
        card.setMinWidth(200);
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10;");

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        Label lblValue = new Label(value);
        lblValue.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        card.getChildren().addAll(lblTitle, lblValue);
        return card;
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
        loadView("KamionManagmentView.fxml");
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


    @FXML
    private void toggleTheme() {
        isDark = !isDark;
        SessionManager.getInstance().setDarkMode(isDark);

        // 1. Primijeni stil na glavni prozor
        Parent root = rootLayout.getScene().getRoot();
        if (isDark) {
            if (!root.getStyleClass().contains("dark-mode")) {
                root.getStyleClass().add("dark-mode");
            }
            btnTheme.setText("SVIJETLI MOD");
        } else {
            root.getStyleClass().remove("dark-mode");
            btnTheme.setText("TAMNI MOD");
        }


    }

    private void generateAdminPDF() {
        try {
            FaktureDAO faktureDAO = new FaktureDAO();
            TuraDAO turaDAO = new TuraDAO();
            PdfService pdfService = new PdfService();
            SessionManager session = SessionManager.getInstance();

            // 1. Finansijski podaci
            double prihodi = faktureDAO.getUkupniPrihodi();
            double servis = faktureDAO.getTroskoviServisa();
            double plate = faktureDAO.getTroskoviPlata();
            double ukupniTroskovi = servis + plate;
            double profit = prihodi - ukupniTroskovi;
            double varijabilni = servis;

            // 2. PDV Proračun
            double stopaPDV = 0.17;
            double osnovica = prihodi / (1 + stopaPDV);
            double iznosPDV = prihodi - osnovica;

            // 3. Operativni KPI podaci
            int brojTura = turaDAO.countAktivneTure();
            int ukupniKm = turaDAO.getUkupniKilometri();
            double ukupnoGorivo = turaDAO.getUkupnaPotrosnjaGoriva();
            int brojVozila = turaDAO.getBrojAktivnihVozila();

            double avgPrihod = (brojTura > 0) ? prihodi / brojTura : 0;
            double trosakPoKm = (ukupniKm > 0) ? ukupniTroskovi / ukupniKm : 0;
            double faliDoNule = (profit < 0) ? Math.abs(profit) : 0;
            double prosjekPotrosnja = (ukupniKm > 0) ? (ukupnoGorivo / ukupniKm) * 100 : 0;
            double prihodPoKm = (ukupniKm > 0) ? prihodi / ukupniKm : 0;

            // 4. Priprema stilova i tekstova
            List<String> topKlijenti = turaDAO.getTopKlijenti();
            String klijentiTekst = topKlijenti.isEmpty() ? "Nema podataka" : String.join("<br/>", topKlijenti);

            // Definisanje stila za bedž (RJEŠAVA TVOJU GREŠKU U HTML-u)
            String stilBoje = "style='background-color: " + (profit >= 0 ? "#27ae60" : "#c0392b") + "; color: #ffffff; padding: 5px 12px; border-radius: 10px; font-weight: bold;'";
            String statusTekst = (profit >= 0) ? "POZITIVAN BILANS" : "GUBITAK POSLOVANJA";

            // 5. Analiza poslovanja
            String objasnjenje = (profit > 0)
                    ? "Poslovanje je stabilno. Prosječan prihod po turi iznosi " + String.format("%.2f", avgPrihod) + " KM."
                    : "Upozorenje: Potrebno je generisati još " + String.format("%.2f", faliDoNule) + " KM prihoda.";

            // 6. Podaci o korisniku i vrijeme
            String privremenoIme = "Sistem";
            Object currentUser = session.getCurrentUser();
            if (currentUser instanceof unze.ptf.routevision_final.model.Admin) {
                unze.ptf.routevision_final.model.Admin admin = (unze.ptf.routevision_final.model.Admin) currentUser;
                privremenoIme = admin.getIme() + " " + admin.getPrezime();
            }
            final String imeZaAlert = privremenoIme;
            String tacnoVrijeme = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss"));

            String qrData = "Logistics_ID_" + tacnoVrijeme.replace(" ", "_") + "_" + prihodi;
            String qrCodeBase64 = getQRCodeBase64(qrData);


            String html = pdfService.loadHtmlTemplate("admin_report.html");

// Kreiramo CIJELI stil atribut kao string da IntelliJ ne bi javljao grešku u HTML-u
            String stilProfita = "style='text-align: right; font-weight: bold; color: " + (profit < 0 ? "#c0392b" : "#27ae60") + ";'";

            html = html.replace("{{stil_boje}}", stilBoje)
                    .replace("{{datum}}", java.time.LocalDate.now().toString())
                    .replace("{{prihodi}}", String.format("%.2f", prihodi))
                    .replace("{{osnovica}}", String.format("%.2f", osnovica))
                    .replace("{{iznos_pdv}}", String.format("%.2f", iznosPDV))
                    .replace("{{varijabilni_troskovi}}", String.format("%.2f", varijabilni))
                    .replace("{{troskovi_plate}}", String.format("%.2f", plate))
                    .replace("{{troskovi_servis}}", String.format("%.2f", servis))
                    .replace("{{profit}}", String.format("%.2f", profit))
                    .replace("{{stil_profita}}", stilProfita) // OVO ZAMJENJUJE CIJELI STYLE TAG
                    .replace("{{prihod_po_km}}", String.format("%.2f", prihodPoKm))
                    .replace("{{aktivna_vozila}}", String.valueOf(brojVozila))
                    .replace("{{ukupni_km}}", String.valueOf(ukupniKm))
                    .replace("{{potrosnja}}", String.format("%.2f", prosjekPotrosnja))
                    .replace("{{top_klijenti}}", klijentiTekst)
                    .replace("{{avg_prihod}}", String.format("%.2f", avgPrihod))
                    .replace("{{trosak_po_km}}", String.format("%.2f", trosakPoKm))
                    .replace("{{objasnjenje}}", objasnjenje)
                    .replace("{{status_tekst}}", statusTekst)
                    .replace("{{korisnik}}", imeZaAlert)
                    .replace("{{vrijeme}}", tacnoVrijeme)
                    .replace("{{qr_code_base64}}", qrCodeBase64);

// 8. Generisanje PDF-a ... (ostatak koda)

            // 8. Generisanje PDF-a
            String putanjaNaDesktop = System.getProperty("user.home") + java.io.File.separator + "Desktop" + java.io.File.separator + "Izvjestaj_Logistika.pdf";
            pdfService.generateReport(putanjaNaDesktop, html);

            javafx.application.Platform.runLater(() -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Izvještaj Spreman");
                alert.setHeaderText("Zvanični dokument generisan");
                alert.setContentText("PDF se nalazi na Desktopu.");
                alert.showAndWait();
            });

        } catch (Exception e) {
            System.err.println("Greška pri generisanju PDF-a: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private String getQRCodeBase64(String text) {
        try {
            // Koristimo javni API za generisanje, ali ga čitamo kroz Javu
            String url = "https://api.qrserver.com/v1/create-qr-code/?size=100x100&data=" + text;
            java.net.URL imageUrl = new java.net.URL(url);
            java.io.InputStream is = imageUrl.openStream();
            byte[] bytes = is.readAllBytes();
            return "data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            return "";
        }
    }
    private void handleGenerateMyDriverPDF(Vozac vozac) {
        try {
            PdfService pdfService = new PdfService();
            String html = pdfService.loadHtmlTemplate("vozac_karton.html");
            String tacnoVrijeme = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss"));
            String qrPodaci = "RouteVision - Službena Potvrda\n" +
                    "Vozač: " + vozac.getIme() + " " + vozac.getPrezime() + "\n" +
                    "ID Dozvole: " + vozac.getBroj_vozacke_dozvole() + "\n" +
                    "Kategorija: " + vozac.getKategorija_dozvole() + "\n" +
                    "Status: Aktivan zaposlenik";
            String qrCodeBase = getQRCodeBase64(qrPodaci);
            String napomena = (vozac.getBroj_dovrsenih_tura() >= 10)
                    ? "KVALIFIKOVAN ZA MJESEČNI BONUS"
                    : "REDOVAN RADNI STATUS";

            html = html.replace("{{ime_prezime}}", vozac.getIme() + " " + vozac.getPrezime())
                    .replace("{{email}}", vozac.getEmail())
                    .replace("{{telefon}}", vozac.getBroj_telefona() != null ? vozac.getBroj_telefona() : "N/A")
                    .replace("{{dozvola}}", vozac.getBroj_vozacke_dozvole())
                    .replace("{{kategorija}}", vozac.getKategorija_dozvole() != null ? vozac.getKategorija_dozvole() : "N/A")
                    .replace("{{plata}}", String.format("%.2f KM", vozac.getPlata()))
                    .replace("{{ture}}", String.valueOf(vozac.getBroj_dovrsenih_tura()))
                    .replace("{{qr_code_base64}}", qrCodeBase)
                    .replace("{{napomena}}", napomena)
                    .replace("{{vrijeme}}", tacnoVrijeme);

            String putanja = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Moj_Izvjestaj.pdf";
            pdfService.generateReport(putanja, html);

            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Uspjeh");
            alert.setHeaderText(null);
            alert.setContentText("Vaš karton je generisan na Desktopu!");
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}