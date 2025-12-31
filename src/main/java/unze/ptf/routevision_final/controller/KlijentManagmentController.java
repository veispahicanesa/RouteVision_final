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
import unze.ptf.routevision_final.model.Klijent;
import unze.ptf.routevision_final.repository.KlijentDAO;

import java.sql.SQLException;
import java.util.List;

public class KlijentManagmentController {

    @FXML private TableView<Klijent> tableView;
    @FXML private TableColumn<Klijent, Integer> idCol;
    @FXML private TableColumn<Klijent, String> nazivCol;
    @FXML private TableColumn<Klijent, String> tipCol;
    @FXML private TableColumn<Klijent, String> emailCol;
    @FXML private TableColumn<Klijent, String> telefonCol;
    @FXML private TableColumn<Klijent, String> mjestoCol;
    @FXML private TableColumn<Klijent, String> kontaktCol;
    @FXML private TableColumn<Klijent, Double> prometCol;

    private KlijentDAO klijentDAO = new KlijentDAO();
    private ObservableList<Klijent> klijentiList;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadKlijentiData();
    }

    private void setupTableColumns() {
        // 1. Redni brojevi
        idCol.setCellFactory(column -> new TableCell<Klijent, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        // 2. Osnovni podaci
        nazivCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNaziv_firme()));

        // OVO JE DODANO:
        kontaktCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getKontakt_osoba()));

        tipCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTip_klijenta()));
        emailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        telefonCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBroj_telefona()));
        mjestoCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getMjesto() + " (" + c.getValue().getDrzava() + ")"
        ));

        // 3. Formatiranje kolone za Promet
        prometCol.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getUkupno_placeno()));
        prometCol.setCellFactory(column -> new TableCell<Klijent, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.2f KM", item));
                    if (item > 5000) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }

    private void loadKlijentiData() {
        try {
            List<Klijent> klijenti = klijentDAO.findAll();
            klijentiList = FXCollections.observableArrayList(klijenti);
            tableView.setItems(klijentiList);
        } catch (SQLException e) {
            showAlert("Greška", "Greška pri učitavanju klijenata: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() { loadKlijentiData(); }

    @FXML
    private void handleAddKlijent(ActionEvent event) { prikaziDijalogForme(null); }

    @FXML
    private void handleEditKlijent(ActionEvent event) {
        Klijent selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) prikaziDijalogForme(selected);
        else showAlert("Upozorenje", "Odaberite klijenta iz tabele!");
    }

    private void prikaziDijalogForme(Klijent postojeci) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(postojeci == null ? "Dodaj Novog Klijenta" : "Uredi Klijenta");

        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(10); grid.setPadding(new Insets(20));

        // KOLONA 1 - Osnovne informacije
        TextField nazivF = new TextField();
        ComboBox<String> tipC = new ComboBox<>(FXCollections.observableArrayList("Privatna", "Proizvodnja", "Logistika", "Inostrana"));
        TextField adresaF = new TextField();
        TextField mjestoF = new TextField();
        TextField drzavaF = new TextField();
        TextField emailF = new TextField();

        // KOLONA 2 - Kontakt i Finansije
        TextField kontaktF = new TextField();
        TextField telF = new TextField();
        TextField porezF = new TextField();
        TextField bankaF = new TextField();
        TextField racunF = new TextField();
        TextField prometF = new TextField(); // NOVO POLJE
        prometF.setPromptText("0.00");

        // Ako je "Uredi", popuni polja iz objekta
        if (postojeci != null) {
            nazivF.setText(postojeci.getNaziv_firme());
            tipC.setValue(postojeci.getTip_klijenta());
            adresaF.setText(postojeci.getAdresa());
            mjestoF.setText(postojeci.getMjesto());
            drzavaF.setText(postojeci.getDrzava());
            emailF.setText(postojeci.getEmail());
            kontaktF.setText(postojeci.getKontakt_osoba());
            telF.setText(postojeci.getBroj_telefona());
            porezF.setText(postojeci.getPoreska_broj());
            bankaF.setText(postojeci.getNaziv_banke());
            racunF.setText(postojeci.getRacun_broj());
            prometF.setText(String.valueOf(postojeci.getUkupno_placeno()));
        }

        // Dodavanje elemenata u Grid (Lijeva kolona)
        grid.add(new Label("Naziv Firme:"), 0, 0); grid.add(nazivF, 1, 0);
        grid.add(new Label("Tip Klijenta:"), 0, 1); grid.add(tipC, 1, 1);
        grid.add(new Label("Adresa:"), 0, 2); grid.add(adresaF, 1, 2);
        grid.add(new Label("Grad:"), 0, 3); grid.add(mjestoF, 1, 3);
        grid.add(new Label("Država:"), 0, 4); grid.add(drzavaF, 1, 4);
        grid.add(new Label("Email:"), 0, 5); grid.add(emailF, 1, 5);

        // Dodavanje elemenata u Grid (Desna kolona)
        grid.add(new Label("Kontakt Osoba:"), 2, 0); grid.add(kontaktF, 3, 0);
        grid.add(new Label("Broj Telefona:"), 2, 1); grid.add(telF, 3, 1);
        grid.add(new Label("Poreski Broj:"), 2, 2); grid.add(porezF, 3, 2);
        grid.add(new Label("Banka:"), 2, 3); grid.add(bankaF, 3, 3);
        grid.add(new Label("Broj Računa:"), 2, 4); grid.add(racunF, 3, 4);
        grid.add(new Label("Ukupni Promet (KM):"), 2, 5); grid.add(prometF, 3, 5); // DODANO

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    Klijent k = (postojeci == null) ? new Klijent() : postojeci;

                    // Preuzimanje podataka
                    k.setNaziv_firme(nazivF.getText());
                    k.setTip_klijenta(tipC.getValue());
                    k.setAdresa(adresaF.getText());
                    k.setMjesto(mjestoF.getText());
                    k.setDrzava(drzavaF.getText());
                    k.setEmail(emailF.getText());
                    k.setKontakt_osoba(kontaktF.getText());
                    k.setBroj_telefona(telF.getText());
                    k.setPoreska_broj(porezF.getText());
                    k.setNaziv_banke(bankaF.getText());
                    k.setRacun_broj(racunF.getText());
                    k.setAktivan(true);

                    // Sigurna konverzija broja
                    try {
                        double p = Double.parseDouble(prometF.getText().replace(",", "."));
                        k.setUkupno_placeno(p);
                    } catch (Exception e) {
                        k.setUkupno_placeno(0.0);
                    }

                    // Slanje u bazu
                    if (postojeci == null) klijentDAO.save(k);
                    else klijentDAO.update(k);

                    loadKlijentiData();
                } catch (SQLException e) {
                    showAlert("Greška", "Baza podataka: " + e.getMessage());
                }
            }
        });
    }
    @FXML
    private void handleDeleteKlijent(ActionEvent event) {
        Klijent selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Obriši " + selected.getNaziv_firme() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(res -> {
            if (res == ButtonType.YES) {
                try {
                    klijentDAO.delete(selected.getId());
                    loadKlijentiData();
                } catch (SQLException e) { showAlert("Greška", e.getMessage()); }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message);
        alert.showAndWait();
    }
}