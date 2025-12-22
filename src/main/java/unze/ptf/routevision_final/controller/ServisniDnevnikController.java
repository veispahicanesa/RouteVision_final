package unze.ptf.routevision_final.controller;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import unze.ptf.routevision_final.model.ServisniDnevnik;
import unze.ptf.routevision_final.repository.ServisniDnevnikDAO;
import unze.ptf.routevision_final.repository.KamionDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ServisniDnevnikController {

    @FXML private TableView<ServisniDnevnik> tableView;
    @FXML private TableColumn<ServisniDnevnik, Integer> idCol;
    @FXML private TableColumn<ServisniDnevnik, LocalDate> datumCol;
    @FXML private TableColumn<ServisniDnevnik, String> vrstaCol;
    @FXML private TableColumn<ServisniDnevnik, Double> trosciCol; // Usklađeno sa FXML-om
    @FXML private TableColumn<ServisniDnevnik, String> servisarCol; // Usklađeno sa FXML-om

    private ServisniDnevnikDAO dao = new ServisniDnevnikDAO();

    @FXML
    public void initialize() {
        // Mapiranje kolona na model
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        datumCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getDatum_servisa()));
        vrstaCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getVrsta_servisa()));
        trosciCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTroskovi()).asObject());
        servisarCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getServiser_naziv()));

        loadDnevnikData();
    }

    @FXML
    public void loadDnevnikData() { // Naziv usklađen sa onAction u FXML-u
        try {
            List<ServisniDnevnik> lista;
            String role = SessionManager.getInstance().getUserRole();
            int userId = SessionManager.getInstance().getUserId();

            if ("VOZAC".equals(role)) {
                lista = dao.findForVozac(userId);
            } else {
                lista = dao.findAll();
            }
            tableView.setItems(FXCollections.observableArrayList(lista));
        } catch (SQLException e) {
            showAlert("Greška", "Problem pri učitavanju: " + e.getMessage());
        }
    }

    @FXML
    private void showAddServisDialog() { // Naziv usklađen sa onAction u FXML-u
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Dodaj Servis");
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField vrstaField = new TextField();
        TextField trosakField = new TextField();
        TextField serviserField = new TextField();

        grid.add(new Label("Vrsta servisa:"), 0, 0); grid.add(vrstaField, 1, 0);
        grid.add(new Label("Trošak (KM):"), 0, 1); grid.add(trosakField, 1, 1);
        grid.add(new Label("Serviser:"), 0, 2); grid.add(serviserField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            try {
                KamionDAO kDao = new KamionDAO();
                // Uzimamo prvi kamion koji pripada vozaču
                var kamioni = kDao.findByVozacId(SessionManager.getInstance().getUserId());
                if(kamioni.isEmpty()) {
                    showAlert("Greška", "Nemate dodijeljen kamion!");
                    return;
                }

                ServisniDnevnik s = new ServisniDnevnik();
                s.setKamion_id(kamioni.get(0).getId());
                s.setVozac_id(SessionManager.getInstance().getUserId());
                s.setDatum_servisa(LocalDate.now());
                s.setVrsta_servisa(vrstaField.getText());
                s.setTroskovi(Double.parseDouble(trosakField.getText()));
                s.setServiser_naziv(serviserField.getText());
                s.setAktivan(true);

                dao.save(s);
                loadDnevnikData();
            } catch (Exception e) {
                showAlert("Greška", "Neispravan unos podataka!");
            }
        }
    }

    @FXML
    private void editSelectedServis() { // Implementirana metoda za 'Uredi'
        ServisniDnevnik selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Upozorenje", "Odaberite servis iz tabele!");
            return;
        }
        // Ovdje možeš dodati sličan dijalog kao za 'Add' ali sa setovanim vrijednostima
    }

    @FXML
    private void deleteSelectedServis() { // Implementirana metoda za 'Obriši'
        ServisniDnevnik selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Obrisati zapis servisa?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    dao.delete(selected.getId());
                    loadDnevnikData();
                } catch (SQLException e) {
                    showAlert("Greška", e.getMessage());
                }
            }
        });
    }

    private void showAlert(String t, String m) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(t);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
}