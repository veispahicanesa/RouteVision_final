package unze.ptf.routevision_final.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import unze.ptf.routevision_final.model.Kamion;
import unze.ptf.routevision_final.repository.KamionDAO;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class KamionManagementController {

    @FXML private TableView<Kamion> tableView;
    @FXML private TableColumn<Kamion, Integer> idCol;
    @FXML private TableColumn<Kamion, String> registarskaCol;
    @FXML private TableColumn<Kamion, String> markaCol;
    @FXML private TableColumn<Kamion, String> modelCol;
    @FXML private TableColumn<Kamion, Double> kapacitetCol;
    @FXML private TableColumn<Kamion, Integer> kmCol;
    @FXML private Button btnAdd;    // Dodajte ovo
    @FXML private Button btnDelete; // Dodajte ovo
    @FXML private Button btnEdit;   // Opcionalno, ako želite i njega sakriti

    private KamionDAO kamionDAO = new KamionDAO();
    private ObservableList<Kamion> kamionList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        tableView.setItems(kamionList);
        loadKamionData();
    }
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadKamionData();
    }
    private void setupTableColumns() {
        idCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        registarskaCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRegistarska_tablica()));
        markaCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMarka()));
        modelCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getModel()));
        kapacitetCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getKapacitet_tone()).asObject());
        kmCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStanje_kilometra()).asObject());
    }
    private void loadKamionData() {
        try {
            String role = SessionManager.getInstance().getUserRole();
            int userId = SessionManager.getInstance().getUserId();
            List<Kamion> podaci;

            // Koristimo equalsIgnoreCase ili usklađujemo sa ostatkom aplikacije ("Vozač")
            if ("Vozač".equalsIgnoreCase(role) || "VOZAC".equalsIgnoreCase(role)) {
                podaci = kamionDAO.findByVozacId(userId);

                // Sakrij kontrole koje vozač ne smije dirati
                if (btnAdd != null) btnAdd.setVisible(false);
                if (btnDelete != null) btnDelete.setVisible(false);
                // Obično sakrijemo i Edit dugme za vozača na listi kamiona
                if (btnEdit != null) btnEdit.setVisible(false);
            } else {
                // Admin vidi sve
                podaci = kamionDAO.findAll();
            }

            // 1. Postavi podatke u listu
            kamionList.setAll(podaci);

            // 2. KLJUČNO: Poveži listu sa tabelom ako to već nisi uradila u initialize
            tableView.setItems(kamionList);

            // 3. Osvježi prikaz
            tableView.refresh();

        } catch (SQLException e) {
            showAlert("Greška", "Problem pri učitavanju: " + e.getMessage());
        }
    }
    @FXML
    private void handleAddKamion(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Dodaj Novi Kamion");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField regField = new TextField();
        TextField markaField = new TextField();
        TextField modelField = new TextField();
        TextField kmField = new TextField("0");

        grid.add(new Label("Reg. Tablica:"), 0, 0); grid.add(regField, 1, 0);
        grid.add(new Label("Marka:"), 0, 1); grid.add(markaField, 1, 1);
        grid.add(new Label("Model:"), 0, 2); grid.add(modelField, 1, 2);
        grid.add(new Label("Početni KM:"), 0, 3); grid.add(kmField, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Kamion novi = new Kamion(regField.getText(), markaField.getText(), modelField.getText());
                novi.setStanje_kilometra(Integer.parseInt(kmField.getText()));

                if ("VOZAC".equals(SessionManager.getInstance().getUserRole())) {
                    novi.setZaduzeni_vozac_id(SessionManager.getInstance().getUserId());
                }

                kamionDAO.save(novi);
                loadKamionData();
            } catch (Exception e) {
                showAlert("Greška", "Neispravan unos podataka.");
            }
        }
    }
    @FXML
    private void handleEditKamion(ActionEvent event) {
        // 1. Provjera selekcije
        Kamion selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Upozorenje", "Molimo odaberite kamion iz tabele koji želite urediti!");
            return;
        }

        // 2. Kreiranje dijaloga
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Uredi Kamion: " + selected.getRegistarska_tablica());

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        // 3. Polja za unos sa trenutnim podacima
        TextField markaField = new TextField(selected.getMarka());
        TextField modelField = new TextField(selected.getModel());
        TextField regField = new TextField(selected.getRegistarska_tablica());
        TextField kmField = new TextField(String.valueOf(selected.getStanje_kilometra()));
        TextField nosivostField = new TextField(String.valueOf(selected.getKapacitet_tone()));

        // Dodajemo i DatePicker za datume (ako ih imaš u modelu)
        DatePicker regDate = new DatePicker(selected.getDatum_registracije());

        grid.add(new Label("Marka:"), 0, 0); grid.add(markaField, 1, 0);
        grid.add(new Label("Model:"), 0, 1); grid.add(modelField, 1, 1);
        grid.add(new Label("Registracija:"), 0, 2); grid.add(regField, 1, 2);
        grid.add(new Label("Kilometraža:"), 0, 3); grid.add(kmField, 1, 3);
        grid.add(new Label("Nosivost (t):"), 0, 4); grid.add(nosivostField, 1, 4);
        grid.add(new Label("Datum Reg:"), 0, 5); grid.add(regDate, 1, 5);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // 4. Obrada nakon klika na OK
        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    // Ažuriranje objekta
                    selected.setMarka(markaField.getText());
                    selected.setModel(modelField.getText());
                    selected.setRegistarska_tablica(regField.getText());
                    selected.setStanje_kilometra(Integer.parseInt(kmField.getText()));
                    selected.setKapacitet_tone(Double.parseDouble(nosivostField.getText()));
                    selected.setDatum_registracije(regDate.getValue());

                    // 5. Spasi u bazu
                    kamionDAO.update(selected);

                    // 6. OSVJEŽI DISPLEJ ODMAH
                    loadKamionData();
                    tableView.refresh();

                    showAlert("Uspjeh", "Podaci o kamionu su ažurirani.");
                } catch (NumberFormatException e) {
                    showAlert("Greška", "Kilometri i nosivost moraju biti brojevi!");
                } catch (SQLException e) {
                    showAlert("Greška", "Baza podataka: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleDeleteKamion(ActionEvent event) {
        Kamion selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Obrisati kamion " + selected.getRegistarska_tablica() + "?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().get() == ButtonType.YES) {
            try {
                kamionDAO.delete(selected.getId());
                loadKamionData();
            } catch (SQLException e) {
                showAlert("Greška", e.getMessage());
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}