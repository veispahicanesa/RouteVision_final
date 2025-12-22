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
import unze.ptf.routevision_final.model.Oprema;
import unze.ptf.routevision_final.model.Kamion;
import unze.ptf.routevision_final.repository.OpremaDAO;
import unze.ptf.routevision_final.repository.KamionDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OpremaController {

    @FXML private TableView<Oprema> tableView;
    @FXML private TableColumn<Oprema, Integer> idCol;
    @FXML private TableColumn<Oprema, String> nazivCol;
    @FXML private TableColumn<Oprema, String> vrstaCol;
    @FXML private TableColumn<Oprema, Double> kapacitetCol;
    @FXML private TableColumn<Oprema, String> stanjeCol;

    private OpremaDAO opremaDAO = new OpremaDAO();
    private ObservableList<Oprema> opremeList;

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        nazivCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNaziv()));
        vrstaCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getVrsta()));
        kapacitetCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getKapacitet()).asObject());
        stanjeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStanje()));

        loadOpremaData();
    }

    // POPRAVKA: Dodana metoda za osvježavanje (handleRefresh)
    @FXML
    private void handleRefresh() {
        loadOpremaData();
    }

    private void loadOpremaData() {
        try {
            List<Oprema> podaci;
            if ("VOZAC".equals(SessionManager.getInstance().getUserRole())) {
                podaci = opremaDAO.findForVozac(SessionManager.getInstance().getUserId());
            } else {
                podaci = opremaDAO.findAll();
            }
            opremeList = FXCollections.observableArrayList(podaci);
            tableView.setItems(opremeList);
        } catch (SQLException e) {
            showAlert("Greška", e.getMessage());
        }
    }

    @FXML
    private void handleAddOprema() {
        showOpremaDialog(null);
    }

    // POPRAVKA: Dodana metoda za uređivanje (handleEditOprema)
    @FXML
    private void handleEditOprema() {
        Oprema selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Upozorenje", "Molimo odaberite opremu za uređivanje.");
            return;
        }
        showOpremaDialog(selected);
    }

    // Pomoćna metoda za zajednički dijalog dodavanja/uređivanja
    private void showOpremaDialog(Oprema oprema) {
        boolean isEdit = (oprema != null);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Uredi Opremu" : "Dodaj Novu Opremu");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField naziv = new TextField(isEdit ? oprema.getNaziv() : "");
        TextField vrsta = new TextField(isEdit ? oprema.getVrsta() : "");
        TextField kapacitet = new TextField(isEdit ? String.valueOf(oprema.getKapacitet()) : "");
        TextField stanje = new TextField(isEdit ? oprema.getStanje() : "");

        grid.add(new Label("Naziv:"), 0, 0); grid.add(naziv, 1, 0);
        grid.add(new Label("Vrsta:"), 0, 1); grid.add(vrsta, 1, 1);
        grid.add(new Label("Kapacitet:"), 0, 2); grid.add(kapacitet, 1, 2);
        grid.add(new Label("Stanje:"), 0, 3); grid.add(stanje, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (!isEdit) {
                    Oprema o = new Oprema(naziv.getText(), vrsta.getText());
                    o.setStanje(stanje.getText());
                    if (!kapacitet.getText().isEmpty()) o.setKapacitet(Double.parseDouble(kapacitet.getText()));

                    KamionDAO kDao = new KamionDAO();
                    List<Kamion> moji = kDao.findByVozacId(SessionManager.getInstance().getUserId());
                    if (!moji.isEmpty()) o.setKamion_id(moji.get(0).getId());

                    opremaDAO.save(o);
                } else {
                    oprema.setNaziv(naziv.getText());
                    oprema.setVrsta(vrsta.getText());
                    oprema.setStanje(stanje.getText());
                    oprema.setKapacitet(Double.parseDouble(kapacitet.getText()));
                    opremaDAO.update(oprema); // Pretpostavka da update metoda postoji u DAO
                }
                loadOpremaData();
            } catch (Exception e) {
                showAlert("Greška", "Neispravan unos: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleDeleteOprema() {
        Oprema sel = tableView.getSelectionModel().getSelectedItem();
        if (sel != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Obrisati " + sel.getNaziv() + "?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(res -> {
                if (res == ButtonType.YES) {
                    try {
                        opremaDAO.delete(sel.getId());
                        loadOpremaData();
                    } catch (SQLException e) { showAlert("Greška", e.getMessage()); }
                }
            });
        }
    }

    private void showAlert(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
}