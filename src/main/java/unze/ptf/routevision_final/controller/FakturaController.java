package unze.ptf.routevision_final.controller;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import unze.ptf.routevision_final.model.Fakture;
import unze.ptf.routevision_final.repository.FaktureDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class FakturaController {

    @FXML private TableView<Fakture> tableView;
    @FXML private TableColumn<Fakture, Integer> idCol;
    @FXML private TableColumn<Fakture, String> brojCol;
    @FXML private TableColumn<Fakture, Double> iznosCol;
    @FXML private TableColumn<Fakture, String> statusCol;
    @FXML private TableColumn<Fakture, String> vrstaCol;
    @FXML private TableColumn<Fakture, String> datumIzdavanjaCol;
    @FXML private TableColumn<Fakture, String> datumDospjecaCol;
    @FXML private TableColumn<Fakture, Double> porezCol;
    @FXML private Button btnMarkPaid;

    @FXML private Button btnDelete;
    @FXML private Button btnAdd;
    @FXML private Button btnEdit;

    private final FaktureDAO dao = new FaktureDAO();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @FXML
    public void initialize() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && btnMarkPaid != null) {
                if ("PLAĆENO".equalsIgnoreCase(newSelection.getStatus_placanja())) {
                    btnMarkPaid.setText("Označi kao Neplaćeno");
                } else {
                    btnMarkPaid.setText("Označi kao Plaćeno");
                }
            }
        });
        // Mapiranje kolona
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        brojCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBroj_fakture()));
        iznosCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getUkupan_iznos()).asObject());
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus_placanja()));
        vrstaCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getVrsta_usluge()));

        if (porezCol != null) {
            porezCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPorez()).asObject());
        }

        datumIzdavanjaCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDatum_izdavanja() != null ?
                        dtf.format(c.getValue().getDatum_izdavanja()) : ""));

        datumDospjecaCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDatum_dospjeca() != null ?
                        dtf.format(c.getValue().getDatum_dospjeca()) : ""));

        podesiPrivilegije();
        loadData();
    }

    private void podesiPrivilegije() {
        String role = SessionManager.getInstance().getUserRole();
        if (role != null && (role.equalsIgnoreCase("VOZAC") || role.equalsIgnoreCase("Vozač"))) {

            if (btnDelete != null) { btnDelete.setVisible(false); btnDelete.setManaged(false); }
            if (btnAdd != null) { btnAdd.setVisible(false); btnAdd.setManaged(false); }
            if (btnEdit != null) { btnEdit.setVisible(false); btnEdit.setManaged(false); }


            if (btnMarkPaid != null) {
                btnMarkPaid.setVisible(false);
                btnMarkPaid.setManaged(false);
            }
        }
    }

    @FXML
    public void loadData() {
        try {
            List<Fakture> lista;
            int userId = SessionManager.getInstance().getUserId();
            String role = SessionManager.getInstance().getUserRole();

            if (role != null && (role.equalsIgnoreCase("VOZAC") || role.equalsIgnoreCase("Vozač"))) {
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
    private void handleAddFaktura() {
        prikaziFormu(null);
    }

    @FXML
    private void handleEditFaktura() {
        Fakture selektovana = tableView.getSelectionModel().getSelectedItem();
        if (selektovana == null) {
            showAlert("Upozorenje", "Molimo odaberite fakturu!");
            return;
        }
        prikaziFormu(selektovana);
    }

    private void prikaziFormu(Fakture postojeca) {
        Dialog<Fakture> dialog = new Dialog<>();
        dialog.setTitle(postojeca == null ? "Nova Faktura" : "Uredi Fakturu");
        dialog.setHeaderText(postojeca == null ? "Unesite podatke o novoj fakturi" : "Izmijenite podatke");

        ButtonType saveButtonType = new ButtonType("Spremi", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtBroj = new TextField();
        TextField txtUsluga = new TextField();
        TextField txtIznos = new TextField();
        DatePicker dpDospijeće = new DatePicker(LocalDate.now().plusDays(15));
        ComboBox<String> cbStatus = new ComboBox<>(FXCollections.observableArrayList("Neplaćeno", "Plaćeno", "U obradi"));

        if (postojeca != null) {
            txtBroj.setText(postojeca.getBroj_fakture());
            txtUsluga.setText(postojeca.getVrsta_usluge());
            txtIznos.setText(String.valueOf(postojeca.getUkupan_iznos()));
            dpDospijeće.setValue(postojeca.getDatum_dospjeca());
            cbStatus.getSelectionModel().select(postojeca.getStatus_placanja());
        } else {
            cbStatus.getSelectionModel().selectFirst();
        }

        grid.add(new Label("Broj Fakture:"), 0, 0); grid.add(txtBroj, 1, 0);
        grid.add(new Label("Usluga:"), 0, 1); grid.add(txtUsluga, 1, 1);
        grid.add(new Label("Ukupan Iznos:"), 0, 2); grid.add(txtIznos, 1, 2);
        grid.add(new Label("Dospijeće:"), 0, 3); grid.add(dpDospijeće, 1, 3);
        grid.add(new Label("Status:"), 0, 4); grid.add(cbStatus, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    Fakture f = (postojeca == null) ? new Fakture() : postojeca;
                    f.setBroj_fakture(txtBroj.getText());
                    f.setVrsta_usluge(txtUsluga.getText());
                    double iznos = Double.parseDouble(txtIznos.getText());
                    f.setUkupan_iznos(iznos);
                    f.setPorez(iznos * 0.17); // Automatski PDV 17%
                    f.setDatum_dospjeca(dpDospijeće.getValue());
                    f.setStatus_placanja(cbStatus.getValue());

                    if (postojeca == null) {
                        f.setTura_id(1);
                        f.setKlijent_id(1);
                        f.setDatum_izdavanja(LocalDate.now());
                        f.setOdobrio_admin_id(SessionManager.getInstance().getUserId());
                    }
                    return f;
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        Optional<Fakture> result = dialog.showAndWait();
        result.ifPresent(faktura -> {
            try {
                if (postojeca == null) dao.save(faktura);
                else dao.update(faktura);
                loadData();
            } catch (SQLException e) {
                showAlert("Greška", "Baza podataka: " + e.getMessage());
            }
        });
    }

    @FXML
    private void markAsPaid() {
        Fakture selektovana = tableView.getSelectionModel().getSelectedItem();
        if (selektovana == null) {
            showAlert("Upozorenje", "Molimo odaberite fakturu!");
            return;
        }


        String trenutniStatus = selektovana.getStatus_placanja();
        String noviStatus = (trenutniStatus != null && trenutniStatus.equalsIgnoreCase("PLAĆENO"))
                ? "NEPLAĆENO"
                : "PLAĆENO";

        try {
            dao.updateStatus(selektovana.getId(), noviStatus);
            loadData();


            System.out.println("Status fakture " + selektovana.getBroj_fakture() + " promijenjen u: " + noviStatus);
        } catch (SQLException e) {
            showAlert("Greška", "Greška pri ažuriranju statusa: " + e.getMessage());
        }
    }

    @FXML
    private void deleteFaktura() {
        Fakture selektovana = tableView.getSelectionModel().getSelectedItem();
        if (selektovana != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Obrisati fakturu " + selektovana.getBroj_fakture() + "?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(res -> {
                if (res == ButtonType.YES) {
                    try {
                        dao.delete(selektovana.getId());
                        loadData();
                    } catch (SQLException e) {
                        showAlert("Greška", e.getMessage());
                    }
                }
            });
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}