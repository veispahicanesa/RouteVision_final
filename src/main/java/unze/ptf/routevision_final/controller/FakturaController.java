package unze.ptf.routevision_final.controller;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import unze.ptf.routevision_final.model.Fakture;
import unze.ptf.routevision_final.repository.FaktureDAO;

import java.sql.SQLException;
import java.util.List;

public class FakturaController {

    @FXML private TableView<Fakture> tableView;
    @FXML private TableColumn<Fakture, Integer> idCol;       // Dodano
    @FXML private TableColumn<Fakture, String> brojCol;
    @FXML private TableColumn<Fakture, Double> iznosCol;
    @FXML private TableColumn<Fakture, String> statusCol;    // Dodano
    @FXML private TableColumn<Fakture, String> vrstaCol;     // Dodano

    private FaktureDAO dao = new FaktureDAO();

    @FXML
    public void initialize() {
        // Povezivanje kolona sa modelom Fakture
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        brojCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBroj_fakture()));
        iznosCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getUkupan_iznos()).asObject());
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus_placanja()));
        vrstaCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getVrsta_usluge()));

        loadData();
    }

    @FXML
    public void loadData() { // Promijenjeno u public da bi ga onAction u FXML vidio
        try {
            List<Fakture> lista;
            int userId = SessionManager.getInstance().getUserId();
            String role = SessionManager.getInstance().getUserRole();

            if ("VOZAC".equals(role)) {
                lista = dao.findForVozac(userId);
            } else {
                lista = dao.findAll();
            }
            tableView.setItems(FXCollections.observableArrayList(lista));
        } catch (SQLException e) {
            showAlert("Greška", "Nije moguće učitati podatke: " + e.getMessage());
        }
    }

    @FXML
    private void markAsPaid() {
        Fakture selektovana = tableView.getSelectionModel().getSelectedItem();
        if (selektovana == null) {
            showAlert("Upozorenje", "Molimo odaberite fakturu!");
            return;
        }

        try {
            selektovana.setStatus_placanja("PLAĆENO");
            dao.updateStatus(selektovana.getId(), "PLAĆENO");
            loadData();
            showAlert("Uspjeh", "Faktura je označena kao plaćena.");
        } catch (SQLException e) {
            showAlert("Greška", "Greška pri ažuriranju: " + e.getMessage());
        }
    }

    @FXML
    private void deleteFaktura() {
        Fakture selektovana = tableView.getSelectionModel().getSelectedItem();
        if (selektovana == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Obrisati fakturu " + selektovana.getBroj_fakture() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    dao.delete(selektovana.getId());
                    loadData();
                } catch (SQLException e) {
                    showAlert("Greška", e.getMessage());
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