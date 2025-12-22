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
    private ObservableList<Kamion> kamionList;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadKamionData();
    }
    @FXML private void handleRefresh(ActionEvent event) {
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

            if ("VOZAC".equals(role)) {
                // Pronađi kamion koji je povezan sa ovim vozačem u tabeli 'vozac'
                // Upit u bazi: SELECT k.* FROM kamion k JOIN vozac v ON v.kamion_id = k.id WHERE v.id = ?
                List<Kamion> mojKamion = kamionDAO.findByVozacId(userId);
                tableView.setItems(FXCollections.observableArrayList(mojKamion));

                // Sakrij dugmiće za dodavanje i brisanje ako je vozač (on smije samo gledati/osvježiti)
                btnAdd.setVisible(false);
                btnDelete.setVisible(false);
            } else {
                // Admin vidi sve
                tableView.setItems(FXCollections.observableArrayList(kamionDAO.findAll()));
            }
        } catch (SQLException e) {
            showAlert("Greška", e.getMessage());
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
                novi.setAktivan(true);

                // Ako dodaje vozač, kamion je odmah njegov
                if ("VOZAC".equals(SessionManager.getInstance().getUserRole())) {
                    novi.setVozac_id(SessionManager.getInstance().getUserId());
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
        Kamion selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Upozorenje", "Odaberite kamion za izmjenu.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getStanje_kilometra()));
        dialog.setTitle("Ažuriraj Kilometražu");
        dialog.setHeaderText("Kamion: " + selected.getRegistarska_tablica());
        dialog.setContentText("Novo stanje kilometara:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(km -> {
            try {
                selected.setStanje_kilometra(Integer.parseInt(km));
                kamionDAO.update(selected);
                loadKamionData();
            } catch (Exception e) {
                showAlert("Greška", "Unesite ispravan broj.");
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