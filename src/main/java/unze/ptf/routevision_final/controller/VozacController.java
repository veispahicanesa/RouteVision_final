package unze.ptf.routevision_final.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter; // DODAJTE OVAJ IMPORT
import unze.ptf.routevision_final.model.Kamion;
import unze.ptf.routevision_final.model.Oprema;
import unze.ptf.routevision_final.model.Vozac;
import unze.ptf.routevision_final.repository.KamionDAO;
import unze.ptf.routevision_final.repository.OpremaDAO;
import unze.ptf.routevision_final.repository.VozacDAO;
import unze.ptf.routevision_final.service.SecurityService;

import java.sql.SQLException;
import java.util.List;

public class VozacController {

    @FXML private TableView<Vozac> tableView;
    @FXML private TableColumn<Vozac, Integer> idCol;
    @FXML private TableColumn<Vozac, String> imeCol;
    @FXML private TableColumn<Vozac, String> prezimeCol;
    @FXML private TableColumn<Vozac, String> emailCol;
    @FXML private TableColumn<Vozac, String> dozvoleCol;
    @FXML private TableColumn<Vozac, Integer> turaCol;
    @FXML private TableColumn<Vozac, Double> plataCol;
    @FXML private TableColumn<Vozac, String> kategorijaCol;
    @FXML private TableColumn<Vozac, String> markaCol;


    private VozacDAO vozacDAO = new VozacDAO();
    private ObservableList<Vozac> vozaciList;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadVozaciData();
        // Ovdje se obično ne pune ComboBox-ovi jer se oni nalaze UNUTAR Dialoga (pop-upa)
    }

    private void setupTableColumns() {
        idCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        imeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIme()));
        prezimeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrezime()));
        emailCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        dozvoleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBroj_vozacke_dozvole()));
        turaCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getBroj_dovrsenih_tura()).asObject());

        markaCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMarka_kamiona()));

        kategorijaCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKategorija_dozvole()));

        plataCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPlata()).asObject());

        plataCol.setCellFactory(tc -> new TableCell<Vozac, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f KM", price));
                }
            }
        });
    }

    private void loadVozaciData() {
        try {
            List<Vozac> vozaci = vozacDAO.findAll();
            vozaciList = FXCollections.observableArrayList(vozaci);
            tableView.setItems(vozaciList);
        } catch (SQLException e) {
            showAlert("Greška", "Greška pri učitavanju: " + e.getMessage());
        }
    }

    // Pomocna metoda za konfiguraciju ComboBox-ova unutar dijaloga
    private void setupKamionComboBox(ComboBox<Kamion> combo) throws SQLException {
        combo.setItems(FXCollections.observableArrayList(new KamionDAO().findAll()));
        combo.setConverter(new StringConverter<Kamion>() {
            @Override
            public String toString(Kamion k) {
                return k == null ? "Nema kamiona" : k.getMarka() + " " + k.getModel() + " (" + k.getRegistarska_tablica() + ")";
            }
            @Override public Kamion fromString(String s) { return null; }
        });
    }

    @FXML
    private void handleAddVozac(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Dodaj Novog Vozača");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField imeField = new TextField();
        TextField prezimeField = new TextField();
        TextField emailField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField dozvoleField = new TextField();
        TextField telefonField = new TextField();
        TextField markaKamionaField = new TextField(); // Novo
        TextField plataField = new TextField();
        TextField kategorijaField = new TextField();
        ComboBox<Kamion> kamionCombo = new ComboBox<>(); // ComboBox kreiramo lokalno za dijalog

        try { setupKamionComboBox(kamionCombo); } catch (SQLException e) { e.printStackTrace(); }

        grid.add(new Label("Ime:"), 0, 0); grid.add(imeField, 1, 0);
        grid.add(new Label("Prezime:"), 0, 1); grid.add(prezimeField, 1, 1);
        grid.add(new Label("Email:"), 0, 2); grid.add(emailField, 1, 2);
        grid.add(new Label("Lozinka:"), 0, 3); grid.add(passwordField, 1, 3);
        grid.add(new Label("Telefon:"), 0, 4); grid.add(telefonField, 1, 4);
        grid.add(new Label("Plata:"), 0, 5); grid.add(plataField, 1, 5);
        grid.add(new Label("Vozačka:"), 0, 6); grid.add(dozvoleField, 1, 6);
        grid.add(new Label("Kategorija:"), 0, 7); grid.add(kategorijaField, 1, 7);
        grid.add(new Label("Marka kamiona:"), 0, 8); grid.add(markaKamionaField, 1, 8);

        grid.add(new Label("Izaberi kamion:"), 0, 9); grid.add(kamionCombo, 1, 9);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    // 1. Kreiramo prazan objekt (koristi se prazan konstruktor Vozac())
                    Vozac vozac = new Vozac();

                    // 2. Ručno postavljamo svako polje pomoću settera
                    vozac.setIme(imeField.getText());
                    vozac.setPrezime(prezimeField.getText());
                    vozac.setEmail(emailField.getText());

                    // Koristi SecurityService ili direktno BCrypt za lozinku
                    vozac.setLozinka(SecurityService.hashPassword(passwordField.getText()));

                    vozac.setBroj_vozacke_dozvole(dozvoleField.getText());
                    vozac.setKategorija_dozvole(kategorijaField.getText());
                    vozac.setBroj_telefona(telefonField.getText());

                    // Provjera za platu (da ne pukne ako je prazno polje)
                    double plata = plataField.getText().isEmpty() ? 0.0 : Double.parseDouble(plataField.getText());
                    vozac.setPlata(plata);

                    vozac.setMarka_kamiona(markaKamionaField.getText());
                    vozac.setTip_goriva("Dizel"); // Default vrijednost
                    vozac.setAktivan(true);
                    vozac.setDatum_kreiranja(java.time.LocalDateTime.now());
                    vozac.setDatum_zaposlenja(java.time.LocalDate.now());

                    // 3. Postavljanje izabranog kamiona ako postoji
                    if (kamionCombo.getValue() != null) {
                        vozac.setKamionId(kamionCombo.getValue().getId());
                    }

                    // 4. Spašavanje u bazu
                    vozacDAO.save(vozac);
                    loadVozaciData();

                } catch (Exception e) {
                    showAlert("Greška", "Spremanje nije uspjelo: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
    @FXML
    private void handleEditVozac(ActionEvent event) {
        Vozac selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Upozorenje", "Molimo odaberite vozača iz tabele kojeg želite urediti!");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Uredi Vozača: " + selected.getIme() + " " + selected.getPrezime());

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
        TextField tureField = new TextField(String.valueOf(selected.getBroj_dovrsenih_tura()));
        TextField imeField = new TextField(selected.getIme());
        TextField prezimeField = new TextField(selected.getPrezime());
        TextField emailField = new TextField(selected.getEmail());
        TextField telefonField = new TextField(selected.getBroj_telefona() != null ? selected.getBroj_telefona() : "");
        TextField plataField = new TextField(String.valueOf(selected.getPlata()));
        TextField kategorijaField = new TextField(selected.getKategorija_dozvole());
        TextField dozvolaField = new TextField(selected.getBroj_vozacke_dozvole());
        ComboBox<Kamion> kamionCombo = new ComboBox<>();

        try {
            setupKamionComboBox(kamionCombo);
            // Logika za selektovanje trenutnog kamiona u ComboBox-u
            if (selected.getKamionId() != null) {
                for (Kamion k : kamionCombo.getItems()) {
                    if (k.getId() == selected.getKamionId()) {
                        kamionCombo.setValue(k);
                        break;
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }

        grid.add(new Label("Ime:"), 0, 0); grid.add(imeField, 1, 0);
        grid.add(new Label("Prezime:"), 0, 1); grid.add(prezimeField, 1, 1);
        grid.add(new Label("Email:"), 0, 2); grid.add(emailField, 1, 2);
        grid.add(new Label("Telefon:"), 0, 3); grid.add(telefonField, 1, 3);
        grid.add(new Label("Plata (KM):"), 0, 4); grid.add(plataField, 1, 4);
        grid.add(new Label("Kategorija:"), 0, 5); grid.add(kategorijaField, 1, 5);
        grid.add(new Label("Broj Dozvole:"), 0, 6); grid.add(dozvolaField, 1, 6);
        grid.add(new Label("Dodijeli Kamion:"), 0, 7); grid.add(kamionCombo, 1, 7);
        grid.add(new Label("Broj završenih tura:"), 0, 8);
        grid.add(tureField, 1, 8);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    // Ažuriranje objekta
                    selected.setIme(imeField.getText());
                    selected.setPrezime(prezimeField.getText());
                    selected.setEmail(emailField.getText());
                    selected.setBroj_telefona(telefonField.getText());
                    selected.setKategorija_dozvole(kategorijaField.getText());
                    selected.setBroj_vozacke_dozvole(dozvolaField.getText());

                    try {
                        selected.setPlata(Double.parseDouble(plataField.getText()));
                    } catch (NumberFormatException nfe) {
                        showAlert("Greška", "Plata mora biti broj!");
                        return;
                    }

                    try {
                        selected.setBroj_dovrsenih_tura(Integer.parseInt(tureField.getText()));
                    } catch (NumberFormatException nfe) {
                        showAlert("Greška", "Broj tura mora biti cijeli broj!");
                        return;
                    }


                    if (kamionCombo.getValue() != null) {
                        selected.setKamionId(kamionCombo.getValue().getId());
                        selected.setMarka_kamiona(kamionCombo.getValue().getMarka());
                    } else {
                        selected.setKamionId(null);
                        selected.setMarka_kamiona(null);
                    }

                    // 1. Spašavanje u bazu (koristi novu metodu koja uključuje platu i kamion)
                    vozacDAO.update(selected);

                    // 2. Ažuriranje asocijacija
                    vozacDAO.updateAssignment(selected.getId(), selected.getKamionId(), selected.getOpremaId());

                    // 3. OSVJEŽAVANJE TABELE
                    tableView.refresh();
                    loadVozaciData();

                    showAlert("Uspjeh", "Podaci o vozaču su uspješno ažurirani.");

                } catch (SQLException e) {
                    showAlert("Greška", "Baza podataka: " + e.getMessage());
                }
            }
        });
    }
    @FXML
    private void handleDeleteVozac(ActionEvent event) {
        Vozac selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Obrisati " + selected.getIme() + "?");
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    vozacDAO.delete(selected.getId());
                    loadVozaciData();
                } catch (SQLException e) {
                    showAlert("Greška", "Brisanje nije uspjelo!");
                }
            }
        });
    }

    @FXML
    private void handleRefresh() {
        loadVozaciData();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}