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
        checkPermissions();

        loadKamionData();
    }
    private void checkPermissions() {
        String role = SessionManager.getInstance().getUserRole();
        if ("Vozač".equalsIgnoreCase(role)) {

            if (btnAdd != null) {
                btnAdd.setVisible(false);
                btnAdd.setManaged(false);
            }
            if (btnDelete != null) {
                btnDelete.setVisible(false);
                btnDelete.setManaged(false);
            }
        }
    }
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadKamionData();
    }
    private void setupTableColumns() {
        idCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setText(null);
                else setText(String.valueOf(getIndex() + 1));
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
            Kamion k = cellData.getValue();
            String ime = k.getIme_vozaca();
            String prezime = k.getPrezime_vozaca();

            // Ako u bazi PIŠE ime, prikaži ga
            if (ime != null && !ime.isEmpty()) {
                return new SimpleStringProperty(ime + " " + prezime);
            }

            // Ako je ime NULL, ali kamion IMA ID vozača (znači Haris ga duži, ali bazi fali tekst)
            if (k.getZaduzeni_vozac_id() != null) {
                return new SimpleStringProperty("Zauzet (ID: " + k.getZaduzeni_vozac_id() + ")");
            }

            return new SimpleStringProperty("Slobodan");
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

            kamionList.setAll(podaci);

            // Eksplicitno kažemo tabeli da su se podaci promijenili
            tableView.setItems(kamionList);
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
        grid.add(new Label("km:"), 0, 5);           grid.add(kmField, 1, 5);
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
            // 1. Učitaj sve vozače u ComboBox
            List<unze.ptf.routevision_final.model.Vozac> sviVozaci = new unze.ptf.routevision_final.repository.VozacDAO().findAll();
            vozacCombo.setItems(FXCollections.observableArrayList(sviVozaci));

            // 2. Pronalaženje trenutnog vozača u listi - SIGURNA PROVJERA
            if (selected.getZaduzeni_vozac_id() != null) {
                for (unze.ptf.routevision_final.model.Vozac v : sviVozaci) {
                    if (v.getId() == selected.getZaduzeni_vozac_id().intValue()) {
                        vozacCombo.setValue(v);
                        break;
                    }
                }
            } else {
                vozacCombo.setValue(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // --- PRAVILO: Vozač ne može mijenjati ko vozi kamion ---
        if ("Vozač".equalsIgnoreCase(SessionManager.getInstance().getUserRole())) {
            vozacCombo.setDisable(true); // Vozač vidi ko je zadužen, ali ne može mijenjati
            // Sva ostala polja su mu DOSTUPNA (regField, markaField, kmField itd.)
        }

        grid.add(new Label("Registracija:"), 0, 0); grid.add(regField, 1, 0);
        grid.add(new Label("Marka:"), 0, 1);        grid.add(markaField, 1, 1);
        grid.add(new Label("Model:"), 0, 2);        grid.add(modelField, 1, 2);
        grid.add(new Label("Godina:"), 0, 3);       grid.add(godinaField, 1, 3);
        grid.add(new Label("Nosivost:"), 0, 4);     grid.add(nosivostField, 1, 4);
        grid.add(new Label("km:"), 0, 5);           grid.add(kmField, 1, 5);
        grid.add(new Label("Datum Reg:"), 0, 6);    grid.add(regDate, 1, 6);
        grid.add(new Label("Vozač:"), 0, 7);        grid.add(vozacCombo, 1, 7);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            // Kod za spašavanje (unutar OK klika)
            if (!vozacCombo.isDisable()) {
                unze.ptf.routevision_final.model.Vozac selektovaniVozac = vozacCombo.getValue();
                if (selektovaniVozac != null) {
                    selected.setZaduzeni_vozac_id(selektovaniVozac.getId());
                    selected.setIme_vozaca(selektovaniVozac.getIme());
                    selected.setPrezime_vozaca(selektovaniVozac.getPrezime());
                } else {
                    // Ako je administrator ispraznio izbor vozača
                    selected.setZaduzeni_vozac_id(null);
                    selected.setIme_vozaca(null);
                    selected.setPrezime_vozaca(null);
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