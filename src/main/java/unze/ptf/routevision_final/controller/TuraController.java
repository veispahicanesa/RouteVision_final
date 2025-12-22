package unze.ptf.routevision_final.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import unze.ptf.routevision_final.model.Tura;
import unze.ptf.routevision_final.repository.TuraDAO;
import java.sql.SQLException;
import java.util.List;

public class TuraController {

    @FXML private TableView<Tura> tableView;
    @FXML private TableColumn<Tura, String> brojCol;
    @FXML private TableColumn<Tura, String> relacijaCol;
    @FXML private TableColumn<Tura, String> datumCol;
    @FXML private TableColumn<Tura, Integer> kmCol;
    @FXML private TableColumn<Tura, String> statusCol;
    @FXML private Button btnDelete;

    private TuraDAO turaDAO = new TuraDAO();

    @FXML
    public void initialize() {
        setupColumns();
        loadData();

        // Sakrij brisanje ako nije admin
        if (!"Admin".equals(SessionManager.getInstance().getUserRole())) {
            btnDelete.setVisible(false);
        }
    }

    private void setupColumns() {
        brojCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBroj_tura()));
        relacijaCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getLokacija_pocetka() + " -> " + c.getValue().getLokacija_kraja()));
        datumCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDatum_pocetka() != null ? c.getValue().getDatum_pocetka().toString() : "-"));
        kmCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getPrijedeni_kilometri()).asObject());
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
    }

    @FXML private void handleRefresh() { loadData(); }

    private void loadData() {
        try {
            String role = SessionManager.getInstance().getUserRole();
            int userId = SessionManager.getInstance().getUserId();

            List<Tura> listaTura;

            if ("Admin".equals(role)) {
                // ADMIN vidi apsolutno sve ture svih vozača
                listaTura = turaDAO.findAll();

                // Opcionalno: Adminu omogućavamo brisanje, vozaču ne
                if (btnDelete != null) btnDelete.setVisible(true);
            } else {
                // VOZAČ vidi samo ture koje su dodijeljene njegovom ID-u
                listaTura = turaDAO.findByVozacId(userId);

                // Sakrivamo dugme za brisanje vozačima
                if (btnDelete != null) btnDelete.setVisible(false);
            }

            tableView.setItems(FXCollections.observableArrayList(listaTura));

        } catch (SQLException e) {
            showAlert("Greška", "Nije moguće učitati ture iz baze.");
            e.printStackTrace();
        }
    }private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        // Ako je naslov "Greška", postavi ikonu na Error tip
        if (title.equalsIgnoreCase("Greška")) {
            alert.setAlertType(Alert.AlertType.ERROR);
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleStatusInProgress() { azurirajStatus("U toku"); }

    @FXML
    private void handleZavrsiTuru() { azurirajStatus("Završena"); }

    @FXML
    private void handleStatusCancel() { azurirajStatus("Prekinuta"); }

    private void azurirajStatus(String noviStatus) {
        Tura sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        try {
            sel.setStatus(noviStatus);
            turaDAO.update(sel);
            loadData();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleDeleteTura() {
        Tura sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Obriši turu " + sel.getBroj_tura() + "?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    turaDAO.delete(sel.getId());
                    loadData();
                } catch (SQLException e) { e.printStackTrace(); }
            }
        });
    }
}