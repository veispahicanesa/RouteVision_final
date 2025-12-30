package unze.ptf.routevision_final.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import unze.ptf.routevision_final.model.Admin;
import unze.ptf.routevision_final.repository.AdminDAO;
import unze.ptf.routevision_final.service.SecurityService;

import java.sql.SQLException;
import java.util.List;

public class AdminListController {

    @FXML private TableView<Admin> tableView;
    @FXML private TableColumn<Admin, Integer> idCol;
    @FXML private TableColumn<Admin, String> imeCol;
    @FXML private TableColumn<Admin, String> prezimeCol;
    @FXML private TableColumn<Admin, String> emailCol;
    @FXML private TableColumn<Admin, Double> plataCol;

    private AdminDAO adminDAO = new AdminDAO();
    private ObservableList<Admin> adminiList;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadAdminData();
    }

    @FXML private TableColumn<Admin, String> datumZaposlenjaCol;

    private void setupTableColumns() {
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        imeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIme()));
        prezimeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrezime()));
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        plataCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPlata()).asObject());

        datumZaposlenjaCol.setCellValueFactory(data -> {
            if (data.getValue().getDatum_zaposlenja() != null) {
                // Koristimo tvoj formatter dd.MM.yyyy
                return new SimpleStringProperty(data.getValue().getDatum_zaposlenja().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            }
            return new SimpleStringProperty("N/A");
        });
        plataCol.setCellFactory(tc -> new TableCell<Admin, Double>() {
            @Override
            protected void updateItem(Double plata, boolean empty) {
                super.updateItem(plata, empty);
                setText(empty || plata == null ? null : String.format("%.2f KM", plata));
            }
        });
    }

    @FXML
    public void loadAdminData() {
        try {
            List<Admin> admini = adminDAO.findAll();
            adminiList = FXCollections.observableArrayList(admini);
            tableView.setItems(adminiList);
        } catch (SQLException e) {
            showAlert("Greška", "Greška pri učitavanju: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddAdmin() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Dodaj Novog Administratora");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
        DatePicker datumZaposlenjaField = new DatePicker(java.time.LocalDate.now());

        TextField imeField = new TextField();
        TextField prezimeField = new TextField();
        TextField emailField = new TextField();
        PasswordField lozinkaField = new PasswordField();
        TextField plataField = new TextField();

        grid.add(new Label("Ime:"), 0, 0); grid.add(imeField, 1, 0);
        grid.add(new Label("Prezime:"), 0, 1); grid.add(prezimeField, 1, 1);
        grid.add(new Label("Email:"), 0, 2); grid.add(emailField, 1, 2);
        grid.add(new Label("Lozinka:"), 0, 3); grid.add(lozinkaField, 1, 3);
        grid.add(new Label("Plata:"), 0, 4); grid.add(plataField, 1, 4);
        grid.add(new Label("Datum zaposlenja:"), 0, 5);
        grid.add(datumZaposlenjaField, 1, 5);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    Admin novi = new Admin();
                    novi.setIme(imeField.getText());
                    novi.setPrezime(prezimeField.getText());
                    novi.setEmail(emailField.getText());
                    // VAŽNO: Heširanje lozinke
                    novi.setLozinka(SecurityService.hashPassword(lozinkaField.getText()));
                    novi.setPlata(plataField.getText().isEmpty() ? 0.0 : Double.parseDouble(plataField.getText()));
                    novi.setAktivan(true);
                    novi.setDatum_kreiranja(java.time.LocalDateTime.now());
                    novi.setDatum_zaposlenja(java.time.LocalDateTime.now());
                    novi.setDatum_zaposlenja(datumZaposlenjaField.getValue().atStartOfDay());
                    adminDAO.save(novi);
                    loadAdminData(); // Osvježi tabelu
                    showAlert("Uspjeh", "Admin dodan!");
                } catch (Exception e) {
                    showAlert("Greška", "Neuspješno dodavanje: " + e.getMessage());
                }
            }
        });

    }

    @FXML
    private void handleDeleteAdmin() {
        Admin selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Upozorenje", "Odaberite admina!");
            return;
        }

        // Sigurnosna provjera da ne obrišeš sam sebe
        if (selected.getId() == SessionManager.getInstance().getUserId()) {
            showAlert("Greška", "Ne možete obrisati sebe!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Obriši admina " + selected.getIme() + "?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    adminDAO.delete(selected.getId());
                    loadAdminData();
                } catch (SQLException e) {
                    showAlert("Greška", "Brisanje nije uspjelo.");
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