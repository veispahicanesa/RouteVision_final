package unze.ptf.routevision_final.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import unze.ptf.routevision_final.model.Admin;
import unze.ptf.routevision_final.model.Tura;
import unze.ptf.routevision_final.model.Vozac;
import unze.ptf.routevision_final.repository.AdminDAO;
import unze.ptf.routevision_final.repository.TuraDAO;
import unze.ptf.routevision_final.repository.VozacDAO;
import java.time.format.DateTimeFormatter;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import unze.ptf.routevision_final.model.Tura;

public class ProfileController {

    @FXML private VBox mainContainer;
    private SessionManager sessionManager = SessionManager.getInstance();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @FXML
    public void initialize() {
        String userRole = sessionManager.getUserRole();
        if ("Admin".equals(userRole)) {
            mainContainer.getChildren().add(createAdminProfile());
        } else {
            mainContainer.getChildren().add(createVozacProfile());
        }
    }

    private VBox createAdminProfile() {
        Admin admin = (Admin) sessionManager.getCurrentUser();
        VBox container = new VBox(15);

        Label titleLabel = new Label("Moj Profil - Administrator");
        titleLabel.getStyleClass().add("title-label");

        VBox profileSection = createInfoSection("Osnovne Informacije", new String[][]{
                {"Ime:", admin.getIme()},
                {"Prezime:", admin.getPrezime()},
                {"Email:", admin.getEmail()},
                {"Broj telefona:", admin.getBroj_telefona() != null ? admin.getBroj_telefona() : "N/A"},
                {"Datum kreiranja:", admin.getDatum_kreiranja() != null ? admin.getDatum_kreiranja().format(formatter) : "N/A"},
                {"Datum zaposlenja:", admin.getDatum_zaposlenja() != null ? admin.getDatum_zaposlenja().format(formatter) : "N/A"},
                {"Plata:", String.format("%.2f KM", admin.getPlata())}
        });

        Button editButton = new Button("Uredi Profil");
        editButton.getStyleClass().add("btn-edit");
        editButton.setOnAction(e -> showEditAdminDialog(admin));

        Button changePassButton = new Button("Promjena Lozinke");
        changePassButton.getStyleClass().add("btn-password");
        changePassButton.setOnAction(e -> showChangePasswordDialog(admin.getId(), "Admin"));

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.getChildren().addAll(editButton, changePassButton);

        container.getChildren().addAll(titleLabel, profileSection, buttonBox);
        return container;
    }

    // --- VOZAČ PROFIL LOGIKA ---
    private VBox createVozacProfile() {
        VozacDAO vozacDAO = new VozacDAO();
        try {
            Vozac vozac = vozacDAO.findById(sessionManager.getUserId());
            if (vozac == null) {
                vozac = (Vozac) sessionManager.getCurrentUser();
            }

            VBox container = new VBox(15);
            Label titleLabel = new Label("Moj Profil - Vozač");
            titleLabel.getStyleClass().add("title-label");

            VBox profileSection = createInfoSection("Osnovne Informacije", new String[][]{
                    {"Ime:", vozac.getIme()},
                    {"Prezime:", vozac.getPrezime()},
                    {"Email:", vozac.getEmail()},
                    {"Broj Telefona:", vozac.getBroj_telefona() != null ? vozac.getBroj_telefona() : "N/A"},
                    {"Broj Vozačke Dozvole:", vozac.getBroj_vozacke_dozvole()},
                    {"Kategorija Dozvole:", vozac.getKategorija_dozvole() != null ? vozac.getKategorija_dozvole() : "N/A"}
            });

            VBox employmentSection = createInfoSection("Informacije o Zaposlenju", new String[][]{
                    {"Datum Zaposlenja:", vozac.getDatum_zaposlenja() != null ? vozac.getDatum_zaposlenja().format(formatter) : "N/A"},
                    {"Plata:", String.format("%.2f KM", vozac.getPlata())},
                    {"Broj Završenih Putovanja:", String.valueOf(vozac.getBroj_dovrsenih_tura())}
            });

            VBox recentTripsSection = createRecentTripsSection(vozac.getId());

            Button editButton = new Button("Uredi Profil");
            editButton.getStyleClass().add("btn-edit");
            Vozac finalVozac = vozac;
            editButton.setOnAction(e -> showEditVozacDialog(finalVozac));

            Button changePassButton = new Button("Promjena Lozinke");
            changePassButton.getStyleClass().add("btn-password");
            changePassButton.setOnAction(e -> showChangePasswordDialog(finalVozac.getId(), "Vozač"));

            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER_LEFT);
            buttonBox.getChildren().addAll(editButton, changePassButton);

            container.getChildren().addAll(titleLabel, profileSection, employmentSection, recentTripsSection, buttonBox);
            return container;

        } catch (SQLException e) {
            e.printStackTrace();
            return new VBox(new Label("Greška pri učitavanju profila!"));
        }
    }

    // --- POMOĆNE METODE ZA UI ---
    private VBox createInfoSection(String title, String[][] data) {
        VBox section = new VBox(8);
        section.getStyleClass().add("info-section");
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("section-title");

        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(10);

        for (int i = 0; i < data.length; i++) {
            Label keyLabel = new Label(data[i][0]);
            keyLabel.getStyleClass().add("key-label");
            Label valueLabel = new Label(data[i][1]);
            valueLabel.getStyleClass().add("value-label");
            grid.add(keyLabel, 0, i);
            grid.add(valueLabel, 1, i);
        }
        section.getChildren().addAll(titleLabel, grid);
        return section;
    }

    private VBox createRecentTripsSection(int vozacId) {
        VBox section = new VBox(8);
        section.getStyleClass().add("info-section");

        Label title = new Label("Zadnja 3 Putovanja");
        title.getStyleClass().add("section-title"); // Ako imaš CSS za naslov
        section.getChildren().add(title);

        try {
            // 1. Pozivamo DAO da dohvati ture za ovog vozača
            TuraDAO turaDAO = new TuraDAO();
            List<Tura> zadnjeTure = turaDAO.findByVozacId(vozacId);

            if (zadnjeTure == null || zadnjeTure.isEmpty()) {
                // Ako nema tura, prikaži poruku
                section.getChildren().add(new Label("Nema nedavnih putovanja."));
            } else {
                // 2. Uzmi samo zadnje 3 (ili manje ako ih nema toliko)
                int brojPrikaza = Math.min(zadnjeTure.size(), 3);

                for (int i = 0; i < brojPrikaza; i++) {
                    Tura t = zadnjeTure.get(i);

                    // Kreiramo ljepši ispis za svaku turu
                    String tekstTure = String.format("%s: %s → %s (%s)",
                            t.getBroj_tura(),
                            t.getLokacija_pocetka(),
                            t.getLokacija_kraja(),
                            t.getStatus());

                    Label turaLabel = new Label(tekstTure);
                    turaLabel.getStyleClass().add("trip-item-label"); // Opcionalno za CSS
                    section.getChildren().add(turaLabel);
                }
            }
        } catch (SQLException e) {
            section.getChildren().add(new Label("Greška pri učitavanju putovanja."));
            e.printStackTrace();
        }

        return section;
    }
    private void refreshView() {
        mainContainer.getChildren().clear();
        initialize();
    }

    private void showEditAdminDialog(Admin admin) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Uredi Profil");
        dialog.setHeaderText("Izmjena osnovnih informacija administratora");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField imeField = new TextField(admin.getIme());
        TextField prezimeField = new TextField(admin.getPrezime());
        TextField emailField = new TextField(admin.getEmail());
        TextField telefonField = new TextField(admin.getBroj_telefona() != null ? admin.getBroj_telefona() : "");

        grid.add(new Label("Ime:"), 0, 0); grid.add(imeField, 1, 0);
        grid.add(new Label("Prezime:"), 0, 1); grid.add(prezimeField, 1, 1);
        grid.add(new Label("Email:"), 0, 2); grid.add(emailField, 1, 2);
        grid.add(new Label("Broj Telefona:"), 0, 3); grid.add(telefonField, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    // Postavljanje novih vrijednosti u objekt
                    admin.setIme(imeField.getText());
                    admin.setPrezime(prezimeField.getText());
                    admin.setEmail(emailField.getText());
                    admin.setBroj_telefona(telefonField.getText());

                    // POZIV DAO - Mora biti jedan argument
                    AdminDAO dao = new AdminDAO();
                    dao.update(admin);

                    // Osvježavanje sesije i ekrana
                    sessionManager.setCurrentUser(admin, "Admin", admin.getId());
                    refreshView();

                    showAlert("Uspjeh", "Profil administratora je ažuriran!");
                } catch (SQLException e) {
                    showAlert("Greška", "Baza podataka: " + e.getMessage());
                }
            }
        });
    }
    private void showEditVozacDialog(Vozac vozac) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Uredi Profil");
        dialog.setHeaderText("Izmjena ličnih podataka vozača");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        // Polja popunjena trenutnim podacima iz baze (preko objekta vozac)
        TextField imeField = new TextField(vozac.getIme());
        TextField prezimeField = new TextField(vozac.getPrezime());
        TextField emailField = new TextField(vozac.getEmail());
        TextField telefonField = new TextField(vozac.getBroj_telefona() != null ? vozac.getBroj_telefona() : "");
        TextField vozackaField = new TextField(vozac.getBroj_vozacke_dozvole());
        TextField kategorijaField = new TextField(vozac.getKategorija_dozvole());

        grid.add(new Label("Ime:"), 0, 0); grid.add(imeField, 1, 0);
        grid.add(new Label("Prezime:"), 0, 1); grid.add(prezimeField, 1, 1);
        grid.add(new Label("Email:"), 0, 2); grid.add(emailField, 1, 2);
        grid.add(new Label("Telefon:"), 0, 3); grid.add(telefonField, 1, 3);
        grid.add(new Label("Broj Dozvole:"), 0, 4); grid.add(vozackaField, 1, 4);
        grid.add(new Label("Kategorija:"), 0, 5); grid.add(kategorijaField, 1, 5);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    // 1. Ažuriranje objekta u memoriji
                    vozac.setIme(imeField.getText());
                    vozac.setPrezime(prezimeField.getText());
                    vozac.setEmail(emailField.getText());
                    vozac.setBroj_telefona(telefonField.getText());
                    vozac.setBroj_vozacke_dozvole(vozackaField.getText());
                    vozac.setKategorija_dozvole(kategorijaField.getText());

                    // 2. Slanje promjena u bazu podataka
                    VozacDAO dao = new VozacDAO();
                    dao.update(vozac);

                    // 3. OSVJEŽAVANJE SESIJE (Ovo je ono što ti je falilo da se odmah vidi)
                    // Koristi ulogu "Vozač" (pazi na kvačicu č, mora biti isto kao pri prijavi)
                    sessionManager.setCurrentUser(vozac, sessionManager.getUserRole(), vozac.getId());

                    // 4. Ponovno iscrtavanje ekrana
                    refreshView();

                    showAlert("Uspjeh", "Vaš profil je uspješno ažuriran!");
                } catch (SQLException e) {
                    showAlert("Greška", "Baza podataka: " + e.getMessage());
                }
            }
        });
    }

    private void showChangePasswordDialog(int userId, String role) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Promjena Lozinke");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        PasswordField oldPass = new PasswordField();
        PasswordField newPass = new PasswordField();
        PasswordField confirmPass = new PasswordField();

        grid.add(new Label("Stara lozinka:"), 0, 0); grid.add(oldPass, 1, 0);
        grid.add(new Label("Nova lozinka:"), 0, 1); grid.add(newPass, 1, 1);
        grid.add(new Label("Potvrdi lozinku:"), 0, 2); grid.add(confirmPass, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    String currentHash = "";
                    if ("Admin".equals(role)) {
                        currentHash = new AdminDAO().findById(userId).getLozinka();
                    } else {
                        currentHash = new VozacDAO().findById(userId).getLozinka();
                    }

                    if (unze.ptf.routevision_final.service.SecurityService.verifyPassword(oldPass.getText(), currentHash)) {
                        if (newPass.getText().equals(confirmPass.getText()) && !newPass.getText().isEmpty()) {
                            String newHash = unze.ptf.routevision_final.service.SecurityService.hashPassword(newPass.getText());

                            if ("Admin".equals(role)) {
                                new AdminDAO().updatePassword(userId, newHash);
                            } else {
                                new VozacDAO().updatePassword(userId, newHash); // Sada imamo ovu metodu!
                            }
                            showAlert("Uspjeh", "Lozinka promijenjena.");
                        } else {
                            showAlert("Greška", "Lozinke se ne podudaraju.");
                        }
                    } else {
                        showAlert("Greška", "Pogrešna stara lozinka.");
                    }
                } catch (SQLException e) {
                    showAlert("Greška", "Baza: " + e.getMessage());
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}