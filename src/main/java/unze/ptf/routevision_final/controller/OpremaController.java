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
import unze.ptf.routevision_final.controller.SessionManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class OpremaController {

    @FXML private TableView<Oprema> tableView;
    @FXML private TableColumn<Oprema, Integer> idCol;
    @FXML private TableColumn<Oprema, String> nazivCol;
    @FXML private TableColumn<Oprema, String> vrstaCol;
    @FXML private TableColumn<Oprema, Double> kapacitetCol;
    @FXML private TableColumn<Oprema, String> stanjeCol;
    @FXML private TableColumn<Oprema, String> datumNabavkeCol;
    @FXML private TableColumn<Oprema, String> zadnjaProvjeraCol;
    @FXML private TableColumn<Oprema, String> napomenaCol;
    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private OpremaDAO opremaDAO = new OpremaDAO();
    private ObservableList<Oprema> opremeList;


    @FXML
    public void initialize() {
        setupColumns();
        applyPermissions();
        loadOpremaData();
    }

    private void applyPermissions() {
        String role = SessionManager.getInstance().getUserRole();

        System.out.println("-----------------------------------------");
        System.out.println("PROVJERA SESIJE: Uloga je [" + role + "]");
        System.out.println("-----------------------------------------");

        if (role != null) {

            String r = role.trim();
            if (r.equalsIgnoreCase("VOZAC") || r.equalsIgnoreCase("Vozač")) {

                System.out.println("DETEKTOVAN VOZAČ - Sakrivam dugmad...");


                btnAdd.setVisible(false);
                btnAdd.setManaged(false);

                btnEdit.setVisible(false);
                btnEdit.setManaged(false);

                btnDelete.setVisible(false);
                btnDelete.setManaged(false);


                btnAdd.setDisable(true);
                btnEdit.setDisable(true);
                btnDelete.setDisable(true);
            }
        }
    }

    private void setupColumns() {
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        nazivCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNaziv()));
        vrstaCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getVrsta()));
        stanjeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStanje()));
        kapacitetCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getKapacitet()).asObject());


        napomenaCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNapomena()));
        datumNabavkeCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDatum_nabavke() != null ? c.getValue().getDatum_nabavke().toString() : "-"));
        zadnjaProvjeraCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDatum_zadnje_provjere() != null ? c.getValue().getDatum_zadnje_provjere().toString() : "-"));

        kapacitetCol.setCellFactory(tc -> new TableCell<Oprema, Double>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null || val == 0) setText("-");
                else setText(String.format("%.2f", val));
            }
        });
    }

    @FXML
    private void handleRefresh() {
        loadOpremaData();
    }

    private void loadOpremaData() {
        try {
            String role = SessionManager.getInstance().getUserRole();
            int userId = SessionManager.getInstance().getUserId();
            List<Oprema> podaci = "VOZAC".equals(role) ? opremaDAO.findForVozac(userId) : opremaDAO.findAll();

            opremeList = FXCollections.observableArrayList(podaci);
            tableView.setItems(opremeList);
        } catch (SQLException e) {
            showAlert("Greška", "Nije moguće učitati podatke: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddOprema() {
        showOpremaDialog(null);
    }

    @FXML
    private void handleEditOprema() {
        Oprema sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert("Upozorenje", "Odaberite stavku koju želite urediti.");
            return;
        }
        showOpremaDialog(sel);
    }

    private void showOpremaDialog(Oprema oprema) {
        boolean isEdit = (oprema != null);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Uredi Opremu" : "Dodaj Novu Opremu");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));


        TextField n = new TextField(isEdit ? oprema.getNaziv() : "");
        TextField v = new TextField(isEdit ? oprema.getVrsta() : "");
        TextField k = new TextField(isEdit ? String.valueOf(oprema.getKapacitet()) : "0");
        TextField s = new TextField(isEdit ? oprema.getStanje() : "Novo");


        DatePicker dpNabavka = new DatePicker(isEdit && oprema.getDatum_nabavke() != null ? oprema.getDatum_nabavke() : LocalDate.now());
        DatePicker dpProvjera = new DatePicker(isEdit && oprema.getDatum_zadnje_provjere() != null ? oprema.getDatum_zadnje_provjere() : LocalDate.now());
        TextArea txtNapomena = new TextArea(isEdit ? oprema.getNapomena() : "");
        txtNapomena.setPrefRowCount(3);
        txtNapomena.setWrapText(true);


        grid.add(new Label("Naziv:"), 0, 0); grid.add(n, 1, 0);
        grid.add(new Label("Vrsta:"), 0, 1); grid.add(v, 1, 1);
        grid.add(new Label("Kapacitet:"), 0, 2); grid.add(k, 1, 2);
        grid.add(new Label("Stanje:"), 0, 3); grid.add(s, 1, 3);
        grid.add(new Label("Datum nabavke:"), 0, 4); grid.add(dpNabavka, 1, 4);
        grid.add(new Label("Zadnja provjera:"), 0, 5); grid.add(dpProvjera, 1, 5);
        grid.add(new Label("Napomena:"), 0, 6); grid.add(txtNapomena, 1, 6);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (!isEdit) {
                        Oprema o = new Oprema(n.getText(), v.getText());
                        o.setStanje(s.getText());
                        o.setKapacitet(Double.parseDouble(k.getText()));
                        o.setDatum_nabavke(dpNabavka.getValue());
                        o.setDatum_zadnje_provjere(dpProvjera.getValue());
                        o.setNapomena(txtNapomena.getText());
                        o.setAktivan(true);
                        opremaDAO.save(o);
                    } else {
                        oprema.setNaziv(n.getText());
                        oprema.setVrsta(v.getText());
                        oprema.setStanje(s.getText());
                        oprema.setKapacitet(Double.parseDouble(k.getText()));
                        oprema.setDatum_nabavke(dpNabavka.getValue());
                        oprema.setDatum_zadnje_provjere(dpProvjera.getValue());
                        oprema.setNapomena(txtNapomena.getText());
                        opremaDAO.update(oprema);
                    }
                    loadOpremaData();
                } catch (Exception e) {
                    showAlert("Greška", "Provjerite unos (Kapacitet mora biti broj).");
                }
            }
        });
    }

    private void updateOpremaFromUI(Oprema o, TextField s, TextField k, DatePicker dpN, DatePicker dpP, TextArea tn) {
        o.setStanje(s.getText());
        o.setKapacitet(Double.parseDouble(k.getText()));
        o.setDatum_nabavke(dpN.getValue());
        o.setDatum_zadnje_provjere(dpP.getValue());
        o.setNapomena(tn.getText());
        o.setAktivan(true);
    }



    @FXML
    private void handleDeleteOprema() {
        Oprema sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Obriši " + sel.getNaziv() + "?");
        a.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                try {
                    opremaDAO.delete(sel.getId());
                    loadOpremaData();
                } catch (SQLException e) {
                    showAlert("Greška", "Brisanje neuspješno.");
                }
            }
        });
    }

    private void showAlert(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
}