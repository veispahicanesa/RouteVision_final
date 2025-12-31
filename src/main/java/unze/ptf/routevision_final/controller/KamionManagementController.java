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
import javafx.scene.layout.HBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;

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
    @FXML private Button btnAdd;
    @FXML private Button btnDelete;
    @FXML private Button btnEdit;
    @FXML private TableColumn<Kamion, Integer> godinaCol;
    @FXML private TableColumn<Kamion, String> datumRegCol;
    @FXML private TableColumn<Kamion, String> vozacImeCol;

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
        idCol.setCellFactory(column -> new TableCell<Kamion, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    // getIndex() vraća 0 za prvi red, pa dodajemo 1
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });
        registarskaCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRegistarska_tablica()));
        markaCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMarka()));
        modelCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getModel()));
        kapacitetCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getKapacitet_tone()).asObject());
        kmCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStanje_kilometra()).asObject());
        godinaCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getGodina_proizvodnje()).asObject());

        datumRegCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDatum_registracije() != null) {
                return new SimpleStringProperty(cellData.getValue().getDatum_registracije().toString());
            }
            return new SimpleStringProperty("N/A");
        });
        vozacImeCol.setCellValueFactory(cellData -> {
            String ime = cellData.getValue().getIme_vozaca();
            String prezime = cellData.getValue().getPrezime_vozaca();
            if (ime == null || ime.isEmpty()) {
                return new SimpleStringProperty("Slobodan");
            }
            return new SimpleStringProperty(ime + " " + prezime);
        });
    }

    private void loadKamionData() {
        try {
            String role = SessionManager.getInstance().getUserRole();
            int userId = SessionManager.getInstance().getUserId();
            List<Kamion> podaci;


            if ("Vozač".equalsIgnoreCase(role)) {
                podaci = kamionDAO.findByVozacId(userId);
            } else {
                podaci = kamionDAO.findAll();
            }

            kamionList.clear();
            kamionList.addAll(podaci);

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
        TextField godinaField = new TextField();
        TextField nosivostField = new TextField();
        TextField kmField = new TextField("0");
        DatePicker regDate = new DatePicker(java.time.LocalDate.now());

        ComboBox<unze.ptf.routevision_final.model.Vozac> vozacCombo = new ComboBox<>();
        try {
            vozacCombo.setItems(FXCollections.observableArrayList(new unze.ptf.routevision_final.repository.VozacDAO().findAll()));
        } catch (SQLException e) {
            System.out.println("Greška pri učitavanju vozača.");
        }

        grid.add(new Label("Reg. Tablica:"), 0, 0); grid.add(regField, 1, 0);
        grid.add(new Label("Marka:"), 0, 1);        grid.add(markaField, 1, 1);
        grid.add(new Label("Model:"), 0, 2);        grid.add(modelField, 1, 2);
        grid.add(new Label("Godina:"), 0, 3);       grid.add(godinaField, 1, 3);
        grid.add(new Label("Nosivost (t):"), 0, 4); grid.add(nosivostField, 1, 4);
        grid.add(new Label("KM:"), 0, 5);           grid.add(kmField, 1, 5);
        grid.add(new Label("Datum Reg:"), 0, 6);    grid.add(regDate, 1, 6);
        grid.add(new Label("Vozač:"), 0, 7);        grid.add(vozacCombo, 1, 7);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    Kamion novi = new Kamion();
                    novi.setRegistarska_tablica(regField.getText());
                    novi.setMarka(markaField.getText());
                    novi.setModel(modelField.getText());
                    novi.setGodina_proizvodnje(Integer.parseInt(godinaField.getText()));
                    novi.setKapacitet_tone(Double.parseDouble(nosivostField.getText()));
                    novi.setStanje_kilometra(Integer.parseInt(kmField.getText()));
                    novi.setDatum_registracije(regDate.getValue());
                    if (vozacCombo.getValue() != null) {
                        novi.setIme_vozaca(vozacCombo.getValue().getIme());
                        novi.setPrezime_vozaca(vozacCombo.getValue().getPrezime());
                        novi.setZaduzeni_vozac_id(vozacCombo.getValue().getId());
                    }
                    novi.setAktivan(true);

                    kamionDAO.save(novi);
                    loadKamionData();
                    showAlert("Uspjeh", "Kamion dodan!");
                } catch (Exception e) {
                    showAlert("Greška", "Provjerite unos brojeva!");
                }
            }
        });
    }
    @FXML
    private void handleEditKamion(ActionEvent event) {
        Kamion selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Upozorenje", "Molimo odaberite kamion!");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Uredi Kamion");
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField regField = new TextField(selected.getRegistarska_tablica());
        TextField markaField = new TextField(selected.getMarka());
        TextField modelField = new TextField(selected.getModel());
        TextField godinaField = new TextField(String.valueOf(selected.getGodina_proizvodnje()));
        TextField nosivostField = new TextField(String.valueOf(selected.getKapacitet_tone()));
        TextField kmField = new TextField(String.valueOf(selected.getStanje_kilometra()));
        DatePicker regDate = new DatePicker(selected.getDatum_registracije());

        ComboBox<unze.ptf.routevision_final.model.Vozac> vozacCombo = new ComboBox<>();
        try {
            List<unze.ptf.routevision_final.model.Vozac> sviVozaci = new unze.ptf.routevision_final.repository.VozacDAO().findAll();
            vozacCombo.setItems(FXCollections.observableArrayList(sviVozaci));


            for (unze.ptf.routevision_final.model.Vozac v : sviVozaci) {
                if (v.getIme().equals(selected.getIme_vozaca()) && v.getPrezime().equals(selected.getPrezime_vozaca())) {
                    vozacCombo.setValue(v);
                    break;
                }
            }
        } catch (SQLException e) { /* Ignorisati ili logovati */ }


        if ("Vozač".equalsIgnoreCase(SessionManager.getInstance().getUserRole())) {
            grid.getChildren().forEach(node -> node.setDisable(true));
            kmField.setDisable(false); // Vozač smije samo KM mijenjati
        }

        grid.add(new Label("Registracija:"), 0, 0); grid.add(regField, 1, 0);
        grid.add(new Label("Marka:"), 0, 1);        grid.add(markaField, 1, 1);
        grid.add(new Label("Model:"), 0, 2);        grid.add(modelField, 1, 2);
        grid.add(new Label("Godina:"), 0, 3);       grid.add(godinaField, 1, 3);
        grid.add(new Label("Nosivost:"), 0, 4);     grid.add(nosivostField, 1, 4);
        grid.add(new Label("KM:"), 0, 5);           grid.add(kmField, 1, 5);
        grid.add(new Label("Datum Reg:"), 0, 6);    grid.add(regDate, 1, 6);
        grid.add(new Label("Vozač:"), 0, 7);        grid.add(vozacCombo, 1, 7);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    selected.setRegistarska_tablica(regField.getText());
                    selected.setMarka(markaField.getText());
                    selected.setModel(modelField.getText());
                    selected.setStanje_kilometra(Integer.parseInt(kmField.getText()));
                    selected.setGodina_proizvodnje(Integer.parseInt(godinaField.getText()));
                    selected.setKapacitet_tone(Double.parseDouble(nosivostField.getText()));
                    selected.setDatum_registracije(regDate.getValue());

                    if (vozacCombo.getValue() != null) {
                        selected.setIme_vozaca(vozacCombo.getValue().getIme());
                        selected.setPrezime_vozaca(vozacCombo.getValue().getPrezime());
                        selected.setZaduzeni_vozac_id(vozacCombo.getValue().getId());
                    } else {
                        selected.setIme_vozaca("");
                        selected.setPrezime_vozaca("");
                        selected.setZaduzeni_vozac_id(null);
                    }

                    kamionDAO.update(selected);
                    loadKamionData();
                    showAlert("Uspjeh", "Izmjene spašene!");
                } catch (Exception e) {
                    showAlert("Greška", "Provjerite unos podataka.");
                }
            }
        });
    }

    @FXML
    private void handleDeleteKamion(ActionEvent event) {
        Kamion selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Upozorenje", "Molimo odaberite kamion kojeg želite obrisati!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Jeste li sigurni da želite obrisati kamion " + selected.getRegistarska_tablica() + "?",
                ButtonType.YES, ButtonType.NO);

        if (confirm.showAndWait().get() == ButtonType.YES) {
            try {

                kamionDAO.delete(selected.getId());


                loadKamionData();

                showAlert("Uspjeh", "Kamion je uspješno obrisan.");
            } catch (SQLException e) {
                showAlert("Greška", "Baza podataka: " + e.getMessage());
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