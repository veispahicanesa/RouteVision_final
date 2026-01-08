package unze.ptf.routevision_final.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import unze.ptf.routevision_final.model.Klijent;
import unze.ptf.routevision_final.model.Narudzba;
import unze.ptf.routevision_final.repository.KlijentDAO;
import unze.ptf.routevision_final.repository.NarudzbaDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class NarudzbaController {

    @FXML private TableView<Narudzba> tableNarudzbe;
    @FXML private TableColumn<Narudzba, String> colBroj;
    @FXML private TableColumn<Narudzba, String> colKlijent;
    @FXML private TableColumn<Narudzba, String> colRoba;
    @FXML private TableColumn<Narudzba, Double> colKolicina;
    @FXML private TableColumn<Narudzba, String> colRelacija;
    @FXML private TableColumn<Narudzba, String> colStatus;

    @FXML private Button btnNova;
    @FXML private Button btnObrisi;

    private NarudzbaDAO narudzbaDAO = new NarudzbaDAO();
    private KlijentDAO klijentDAO = new KlijentDAO(); // Treba nam za ComboBox

    @FXML
    public void initialize() {
        setupColumns();
        loadData();

        // Provjera permisija iz tvoje sesije
        if (SessionManager.getInstance() != null) {
            boolean isAdmin = "Admin".equals(SessionManager.getInstance().getUserRole());
            if (btnNova != null) btnNova.setVisible(isAdmin);
            if (btnObrisi != null) btnObrisi.setVisible(isAdmin);
        }
    }

    private void setupColumns() {
        colBroj.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBroj_narudzbe()));

        // Koristimo nazivKlijenta koji tvoj DAO popunjava preko JOIN-a
        colKlijent.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getNazivKlijenta() != null ? c.getValue().getNazivKlijenta() : "Nije dodijeljen"
        ));

        colRoba.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getVrsta_robe()));
        colKolicina.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getKolicina()).asObject());

        colRelacija.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getLokacija_preuzimanja() + " -> " + c.getValue().getLokacija_dostave()
        ));

        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
    }
    @FXML
    private void loadData() {
        try {
            List<Narudzba> lista = narudzbaDAO.findAll();
            tableNarudzbe.setItems(FXCollections.observableArrayList(lista));
        } catch (SQLException e) {
            showAlert("Greška", "Učitavanje narudžbi nije uspjelo: " + e.getMessage());
        }
    }

    @FXML
    private void handleNovaNarudzba() {
        Dialog<Narudzba> dialog = new Dialog<>();
        dialog.setTitle("Nova Narudžba");
        dialog.setHeaderText("Unesite podatke o novoj narudžbi klijenta");

        ButtonType spremiBtn = new ButtonType("Spremi", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(spremiBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtBroj = new TextField("ORD-" + (System.currentTimeMillis() % 10000));
        ComboBox<Klijent> comboKlijenti = new ComboBox<>();
        TextField txtRoba = new TextField();
        TextField txtKolicina = new TextField();
        TextField txtJedinica = new TextField("tona");
        TextField txtOd = new TextField();
        TextField txtDo = new TextField();
        DatePicker dpIsporuka = new DatePicker(LocalDate.now().plusDays(7));

        try {
            // Učitavamo klijente iz baze u padajući meni
            comboKlijenti.setItems(FXCollections.observableArrayList(klijentDAO.findAll()));
            comboKlijenti.setConverter(new javafx.util.StringConverter<Klijent>() {
                @Override public String toString(Klijent k) { return k == null ? "" : k.getNaziv_firme(); }
                @Override public Klijent fromString(String s) { return null; }
            });
        } catch (SQLException e) { e.printStackTrace(); }

        grid.add(new Label("Broj Narudžbe:"), 0, 0); grid.add(txtBroj, 1, 0);
        grid.add(new Label("Klijent:"), 0, 1);       grid.add(comboKlijenti, 1, 1);
        grid.add(new Label("Vrsta robe:"), 0, 2);    grid.add(txtRoba, 1, 2);
        grid.add(new Label("Količina:"), 0, 3);      grid.add(txtKolicina, 1, 3);
        grid.add(new Label("Jedinica:"), 0, 4);      grid.add(txtJedinica, 1, 4);
        grid.add(new Label("Utovar:"), 0, 5);        grid.add(txtOd, 1, 5);
        grid.add(new Label("Istovar:"), 0, 6);       grid.add(txtDo, 1, 6);
        grid.add(new Label("Rok isporuke:"), 0, 7);  grid.add(dpIsporuka, 1, 7);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == spremiBtn) {
                try {
                    Narudzba n = new Narudzba();
                    n.setBroj_narudzbe(txtBroj.getText());
                    n.setKlijent_id(comboKlijenti.getValue().getId());
                    n.setVrsta_robe(txtRoba.getText());
                    n.setKolicina(Double.parseDouble(txtKolicina.getText()));
                    n.setJedinica_mjere(txtJedinica.getText());
                    n.setLokacija_preuzimanja(txtOd.getText());
                    n.setLokacija_dostave(txtDo.getText());
                    n.setDatum_narudzbe(LocalDate.now());
                    n.setDatum_isporuke(dpIsporuka.getValue());
                    n.setStatus("Novoprijavljena");
                    n.setAktivan(true);
                    return n;
                } catch (Exception e) {
                    showAlert("Greška", "Provjerite unos (Količina mora biti broj).");
                    return null;
                }
            }
            return null;
        });

        Optional<Narudzba> result = dialog.showAndWait();
        result.ifPresent(n -> {
            try {
                narudzbaDAO.save(n);
                loadData();
            } catch (SQLException e) {
                showAlert("Greška", "Neuspješno spašavanje u bazu.");
            }
        });
    }

    @FXML
    private void handleObrisi() {
        Narudzba sel = tableNarudzbe.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Obrisati narudžbu " + sel.getBroj_narudzbe() + "?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    narudzbaDAO.delete(sel.getId());
                    loadData();
                } catch (SQLException e) {
                    showAlert("Greška", "Brisanje nije uspjelo.");
                }
            }
        });
    }

    private void showAlert(String title, String text) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setContentText(text);
        a.showAndWait();
    }

    @FXML
    private void handleEditNarudzba() {
        Narudzba selektovana = tableNarudzbe.getSelectionModel().getSelectedItem();
        if (selektovana == null) {
            showAlert("Upozorenje", "Molimo odaberite narudžbu za uređivanje.");
            return;
        }

        Dialog<Narudzba> dialog = new Dialog<>();
        dialog.setTitle("Uređivanje Narudžbe");
        dialog.setHeaderText("Izmjena podataka za narudžbu: " + selektovana.getBroj_narudzbe());

        ButtonType spremiBtn = new ButtonType("Sačuvaj izmjene", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(spremiBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Popunjavamo polja trenutnim vrijednostima
        TextField txtRoba = new TextField(selektovana.getVrsta_robe());
        TextField txtKolicina = new TextField(String.valueOf(selektovana.getKolicina()));
        TextField txtJedinica = new TextField(selektovana.getJedinica_mjere());
        ComboBox<String> comboStatus = new ComboBox<>(FXCollections.observableArrayList(
                "Novoprijavljena", "U obradi", "Utovareno", "Isporučeno", "Otkazano"
        ));
        comboStatus.setValue(selektovana.getStatus());

        grid.add(new Label("Vrsta robe:"), 0, 0); grid.add(txtRoba, 1, 0);
        grid.add(new Label("Količina:"), 0, 1);    grid.add(txtKolicina, 1, 1);
        grid.add(new Label("Jedinica:"), 0, 2);    grid.add(txtJedinica, 1, 2);
        grid.add(new Label("Status:"), 0, 3);      grid.add(comboStatus, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == spremiBtn) {
                try {
                    selektovana.setVrsta_robe(txtRoba.getText());
                    selektovana.setKolicina(Double.parseDouble(txtKolicina.getText()));
                    selektovana.setJedinica_mjere(txtJedinica.getText());
                    selektovana.setStatus(comboStatus.getValue());
                    return selektovana;
                } catch (Exception e) {
                    showAlert("Greška", "Neispravan unos količine.");
                    return null;
                }
            }
            return null;
        });

        Optional<Narudzba> result = dialog.showAndWait();
        result.ifPresent(n -> {
            try {
                narudzbaDAO.update(n); // Pozivamo tvoj update u DAO
                loadData();
                tableNarudzbe.refresh();
            } catch (SQLException e) {
                showAlert("Greška", "Ažuriranje baze nije uspjelo.");
            }
        });
    }
}