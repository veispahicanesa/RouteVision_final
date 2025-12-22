package unze.ptf.routevision_final.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter; // DODAJTE OVAJ IMPORT
import unze.ptf.routevision_final.model.Kamion;
import unze.ptf.routevision_final.model.Oprema;
import unze.ptf.routevision_final.model.Vozac;
import unze.ptf.routevision_final.repository.KamionDAO;
import unze.ptf.routevision_final.repository.OpremaDAO;
import unze.ptf.routevision_final.repository.VozacDAO;
import unze.ptf.routevision_final.service.SecurityService;

import java.sql.SQLException;
import java.util.List;

public class VozacController {

    @FXML private TableView<Vozac> tableView;
    @FXML private TableColumn<Vozac, Integer> idCol;
    @FXML private TableColumn<Vozac, String> imeCol;
    @FXML private TableColumn<Vozac, String> prezimeCol;
    @FXML private TableColumn<Vozac, String> emailCol;
    @FXML private TableColumn<Vozac, String> dozvoleCol;
    @FXML private TableColumn<Vozac, Integer> turaCol;

    private VozacDAO vozacDAO = new VozacDAO();
    private ObservableList<Vozac> vozaciList;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadVozaciData();
        // Ovdje se obično ne pune ComboBox-ovi jer se oni nalaze UNUTAR Dialoga (pop-upa)
    }

    private void setupTableColumns() {
        idCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        imeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIme()));
        prezimeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrezime()));
        emailCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        dozvoleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBroj_vozacke_dozvole()));

        // Povezivanje sa brojem tura
        turaCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getBroj_dovrsenih_tura()).asObject());
    }

    private void loadVozaciData() {
        try {
            List<Vozac> vozaci = vozacDAO.findAll();
            vozaciList = FXCollections.observableArrayList(vozaci);
            tableView.setItems(vozaciList);
        } catch (SQLException e) {
            showAlert("Greška", "Greška pri učitavanju: " + e.getMessage());
        }
    }

    // Pomocna metoda za konfiguraciju ComboBox-ova unutar dijaloga
    private void setupKamionComboBox(ComboBox<Kamion> combo) throws SQLException {
        combo.setItems(FXCollections.observableArrayList(new KamionDAO().findAll()));
        combo.setConverter(new StringConverter<Kamion>() {
            @Override
            public String toString(Kamion k) {
                return k == null ? "Nema kamiona" : k.getMarka() + " " + k.getModel() + " (" + k.getRegistarska_tablica() + ")";
            }
            @Override public Kamion fromString(String s) { return null; }
        });
    }

    @FXML
    private void handleAddVozac(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Dodaj Novog Vozača");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField imeField = new TextField();
        TextField prezimeField = new TextField();
        TextField emailField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField dozvoleField = new TextField();
        ComboBox<Kamion> kamionCombo = new ComboBox<>(); // ComboBox kreiramo lokalno za dijalog

        try { setupKamionComboBox(kamionCombo); } catch (SQLException e) { e.printStackTrace(); }

        grid.add(new Label("Ime:"), 0, 0); grid.add(imeField, 1, 0);
        grid.add(new Label("Prezime:"), 0, 1); grid.add(prezimeField, 1, 1);
        grid.add(new Label("Email:"), 0, 2); grid.add(emailField, 1, 2);
        grid.add(new Label("Lozinka:"), 0, 3); grid.add(passwordField, 1, 3);
        grid.add(new Label("Vozačka:"), 0, 4); grid.add(dozvoleField, 1, 4);
        grid.add(new Label("Dodijeli Kamion:"), 0, 5); grid.add(kamionCombo, 1, 5);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    Vozac vozac = new Vozac(imeField.getText(), prezimeField.getText(), emailField.getText(),
                            SecurityService.hashPassword(passwordField.getText()), dozvoleField.getText());

                    // Postavljanje izabranog kamiona
                    if (kamionCombo.getValue() != null) {
                        vozac.setKamionId(kamionCombo.getValue().getId());
                    }

                    vozacDAO.save(vozac);
                    loadVozaciData();
                } catch (SQLException e) {
                    showAlert("Greška", "Spremanje nije uspjelo: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleEditVozac(ActionEvent event) {
        Vozac selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Upozorenje", "Odaberite vozača!"); return; }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Uredi Vozača");
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField imeField = new TextField(selected.getIme());
        TextField prezimeField = new TextField(selected.getPrezime());
        ComboBox<Kamion> kamionCombo = new ComboBox<>();

        try { setupKamionComboBox(kamionCombo); } catch (SQLException e) { e.printStackTrace(); }

        grid.add(new Label("Ime:"), 0, 0); grid.add(imeField, 1, 0);
        grid.add(new Label("Prezime:"), 0, 1); grid.add(prezimeField, 1, 1);
        grid.add(new Label("Kamion:"), 0, 2); grid.add(kamionCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                selected.setIme(imeField.getText());
                selected.setPrezime(prezimeField.getText());
                if (kamionCombo.getValue() != null) {
                    selected.setKamionId(kamionCombo.getValue().getId());
                }
                try {
                    vozacDAO.update(selected);
                    // Ako ste dodali metodu updateAssignment u DAO, pozovite je ovdje
                    vozacDAO.updateAssignment(selected.getId(), selected.getKamionId(), selected.getOpremaId());
                    loadVozaciData();
                } catch (SQLException e) {
                    showAlert("Greška", "Ažuriranje nije uspjelo!");
                }
            }
        });
    }

    @FXML
    private void handleDeleteVozac(ActionEvent event) {
        Vozac selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Obrisati " + selected.getIme() + "?");
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    vozacDAO.delete(selected.getId());
                    loadVozaciData();
                } catch (SQLException e) {
                    showAlert("Greška", "Brisanje nije uspjelo!");
                }
            }
        });
    }

    @FXML
    private void handleRefresh() {
        loadVozaciData();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}