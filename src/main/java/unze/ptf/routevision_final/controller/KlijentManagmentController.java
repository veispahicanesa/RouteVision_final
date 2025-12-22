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
import unze.ptf.routevision_final.model.Klijent;
import unze.ptf.routevision_final.repository.KlijentDAO;

import java.sql.SQLException;
import java.util.List;

public class KlijentManagmentController {

    @FXML private TableView<Klijent> tableView;
    @FXML private TableColumn<Klijent, Integer> idCol;
    @FXML private TableColumn<Klijent, String> nazivCol;
    @FXML private TableColumn<Klijent, String> tipCol;
    @FXML private TableColumn<Klijent, String> emailCol;
    @FXML private TableColumn<Klijent, String> telefonCol;

    private KlijentDAO klijentDAO = new KlijentDAO();
    private ObservableList<Klijent> klijentiList;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadKlijentiData();
    }

    private void setupTableColumns() {
        idCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nazivCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNaziv_firme()));
        tipCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTip_klijenta()));
        emailCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        telefonCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBroj_telefona()));
    }

    @FXML
    private void handleRefresh() {
        loadKlijentiData();
    }

    private void loadKlijentiData() {
        try {
            List<Klijent> klijenti = klijentDAO.findAll();
            klijentiList = FXCollections.observableArrayList(klijenti);
            tableView.setItems(klijentiList);
        } catch (SQLException e) {
            showAlert("Greška", "Greška pri učitavanju klijenata: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddKlijent(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Dodaj Novog Klijenta");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nazivField = new TextField();
        nazivField.setPromptText("Naziv Firme");
        ComboBox<String> tipCombo = new ComboBox<>(FXCollections.observableArrayList(
                "Pojedinac", "Mala firma", "Srednja firma", "Velika firma"
        ));
        tipCombo.setPrefWidth(200);
        TextField adresaField = new TextField();
        adresaField.setPromptText("Adresa");
        TextField mjestoField = new TextField();
        mjestoField.setPromptText("Mjesto");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField telefonField = new TextField();
        telefonField.setPromptText("Broj Telefona");

        grid.add(new Label("Naziv Firme:"), 0, 0);
        grid.add(nazivField, 1, 0);
        grid.add(new Label("Tip:"), 0, 1);
        grid.add(tipCombo, 1, 1);
        grid.add(new Label("Adresa:"), 0, 2);
        grid.add(adresaField, 1, 2);
        grid.add(new Label("Mjesto:"), 0, 3);
        grid.add(mjestoField, 1, 3);
        grid.add(new Label("Email:"), 0, 4);
        grid.add(emailField, 1, 4);
        grid.add(new Label("Telefon:"), 0, 5);
        grid.add(telefonField, 1, 5);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    Klijent klijent = new Klijent(nazivField.getText(), tipCombo.getValue());
                    klijent.setAdresa(adresaField.getText());
                    klijent.setMjesto(mjestoField.getText());
                    klijent.setEmail(emailField.getText());
                    klijent.setBroj_telefona(telefonField.getText());

                    klijentDAO.save(klijent);
                    loadKlijentiData();
                    showAlert("Uspjeh", "Klijent je uspješno dodan!");
                } catch (SQLException e) {
                    showAlert("Greška", "Greška pri spremanju klijenta: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleEditKlijent(ActionEvent event) {
        Klijent selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Upozorenje", "Molimo odaberite klijenta iz tabele!");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Uredi Klijenta");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nazivField = new TextField(selected.getNaziv_firme());
        TextField adresaField = new TextField(selected.getAdresa() != null ? selected.getAdresa() : "");
        TextField emailField = new TextField(selected.getEmail() != null ? selected.getEmail() : "");
        TextField telefonField = new TextField(selected.getBroj_telefona() != null ? selected.getBroj_telefona() : "");

        grid.add(new Label("Naziv Firme:"), 0, 0);
        grid.add(nazivField, 1, 0);
        grid.add(new Label("Adresa:"), 0, 1);
        grid.add(adresaField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Telefon:"), 0, 3);
        grid.add(telefonField, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    selected.setNaziv_firme(nazivField.getText());
                    selected.setAdresa(adresaField.getText());
                    selected.setEmail(emailField.getText());
                    selected.setBroj_telefona(telefonField.getText());

                    klijentDAO.update(selected);
                    loadKlijentiData();
                    showAlert("Uspjeh", "Podaci o klijentu su ažurirani!");
                } catch (SQLException e) {
                    showAlert("Greška", "Greška pri ažuriranju: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleDeleteKlijent(ActionEvent event) {
        Klijent selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Upozorenje", "Molimo odaberite klijenta!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Potvrda Brisanja");
        confirm.setHeaderText(null);
        confirm.setContentText("Da li ste sigurni da želite obrisati klijenta: " + selected.getNaziv_firme() + "?");

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    klijentDAO.delete(selected.getId());
                    loadKlijentiData();
                    showAlert("Uspjeh", "Klijent je uspješno obrisan!");
                } catch (SQLException e) {
                    showAlert("Greška", "Greška pri brisanju: " + e.getMessage());
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