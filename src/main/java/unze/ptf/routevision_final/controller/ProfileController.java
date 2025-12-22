package unze.ptf.routevision_final.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import unze.ptf.routevision_final.model.Admin;
import unze.ptf.routevision_final.model.Vozac;
import unze.ptf.routevision_final.repository.VozacDAO;

import java.sql.SQLException;

public class ProfileController {

    @FXML private VBox mainContainer;

    private SessionManager sessionManager = SessionManager.getInstance();

    @FXML
    public void initialize() {
        // Tvoja originalna logika za grananje profila
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
                {"Broj Telefona:", admin.getBroj_telefona() != null ? admin.getBroj_telefona() : "N/A"},
                {"Datum Kreiranja:", admin.getDatum_kreiranja().toString()}
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

            VBox profileSection = createInfoSection("Osnovno Informacije", new String[][]{
                    {"Ime:", vozac.getIme()},
                    {"Prezime:", vozac.getPrezime()},
                    {"Email:", vozac.getEmail()},
                    {"Broj Telefona:", vozac.getBroj_telefona() != null ? vozac.getBroj_telefona() : "N/A"},
                    {"Broj Vozačke Dozvole:", vozac.getBroj_vozacke_dozvole()},
                    {"Kategorija Dozvole:", vozac.getKategorija_dozvole() != null ? vozac.getKategorija_dozvole() : "N/A"}
            });

            VBox employmentSection = createInfoSection("Informacije o Zaposlenju", new String[][]{
                    {"Datum Zaposlenja:", vozac.getDatum_zaposlenja() != null ? vozac.getDatum_zaposlenja().toString() : "N/A"},
                    {"Plata:", String.format("%.2f KM", vozac.getPlata())},
                    {"Broj Završenih Putovanja:", String.valueOf(vozac.getBroj_dovrsenih_tura())},
                    {"Stanje Računa:", String.format("%.2f KM", vozac.getStanje_racuna())}
            });

            VBox recentTripsSection = createRecentTripsSection(vozac.getId());

            Button editButton = new Button("Uredi Profil");
            editButton.getStyleClass().add("btn-edit");
            Vozac finalVozac = vozac;
            editButton.setOnAction(e -> showEditVozacDialog(finalVozac));

            Button changePassButton = new Button("Promjena Lozinke");
            changePassButton.getStyleClass().add("btn-password");
            Vozac finalVozac1 = vozac;
            changePassButton.setOnAction(e -> showChangePasswordDialog(finalVozac1.getId(), "Vozač"));

            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER_LEFT);
            buttonBox.getChildren().addAll(editButton, changePassButton);

            container.getChildren().addAll(titleLabel, profileSection, employmentSection, recentTripsSection, buttonBox);
            return container;

        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Greška pri učitavanju profila!");
            errorLabel.getStyleClass().add("error-label");
            VBox container = new VBox();
            container.getChildren().add(errorLabel);
            return container;
        }
    }

    private VBox createInfoSection(String title, String[][] data) {
        VBox section = new VBox(8);
        section.getStyleClass().add("info-section");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("section-title");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

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

        Label titleLabel = new Label("Zadnja 3 Putovanja");
        titleLabel.getStyleClass().add("section-title");

        Label placeholderLabel = new Label("Putovanja se učitavaju...");
        placeholderLabel.getStyleClass().add("placeholder-label");

        section.getChildren().addAll(titleLabel, placeholderLabel);
        return section;
    }

    private void showEditAdminDialog(Admin admin) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Uredi Profil - Administrator");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField imeField = new TextField(admin.getIme());
        TextField prezimeField = new TextField(admin.getPrezime());
        TextField telefonField = new TextField(admin.getBroj_telefona() != null ? admin.getBroj_telefona() : "");

        grid.add(new Label("Ime:"), 0, 0); grid.add(imeField, 1, 0);
        grid.add(new Label("Prezime:"), 0, 1); grid.add(prezimeField, 1, 1);
        grid.add(new Label("Broj Telefona:"), 0, 2); grid.add(telefonField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        final Admin adminFinal = admin;
        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                adminFinal.setIme(imeField.getText());
                adminFinal.setPrezime(prezimeField.getText());
                adminFinal.setBroj_telefona(telefonField.getText());
                showAlert("Uspjeha", "Profil je ažuriran!");
            }
        });
    }

    private void showEditVozacDialog(Vozac vozac) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Uredi Profil - Vozač");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField imeField = new TextField(vozac.getIme());
        TextField prezimeField = new TextField(vozac.getPrezime());
        TextField telefonField = new TextField(vozac.getBroj_telefona() != null ? vozac.getBroj_telefona() : "");
        TextField kategorijaField = new TextField(vozac.getKategorija_dozvole() != null ? vozac.getKategorija_dozvole() : "");

        grid.add(new Label("Ime:"), 0, 0); grid.add(imeField, 1, 0);
        grid.add(new Label("Prezime:"), 0, 1); grid.add(prezimeField, 1, 1);
        grid.add(new Label("Broj Telefona:"), 0, 2); grid.add(telefonField, 1, 2);
        grid.add(new Label("Kategorija Dozvole:"), 0, 3); grid.add(kategorijaField, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        final Vozac vozacFinal = vozac;
        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                vozacFinal.setIme(imeField.getText());
                vozacFinal.setPrezime(prezimeField.getText());
                vozacFinal.setBroj_telefona(telefonField.getText());
                vozacFinal.setKategorija_dozvole(kategorijaField.getText());
                showAlert("Uspjeha", "Profil je ažuriran!");
            }
        });
    }

    private void showChangePasswordDialog(int userId, String role) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Promjena Lozinke");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        PasswordField currentPassField = new PasswordField();
        PasswordField newPassField = new PasswordField();
        PasswordField confirmPassField = new PasswordField();

        grid.add(new Label("Trenutna Lozinka:"), 0, 0); grid.add(currentPassField, 1, 0);
        grid.add(new Label("Nova Lozinka:"), 0, 1); grid.add(newPassField, 1, 1);
        grid.add(new Label("Potvrdi Lozinku:"), 0, 2); grid.add(confirmPassField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                if (newPassField.getText().equals(confirmPassField.getText())) {
                    showAlert("Uspjeha", "Lozinka je promijenjena!");
                } else {
                    showAlert("Greška", "Lozinke se ne podudaraju!");
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