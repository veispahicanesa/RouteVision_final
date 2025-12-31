package unze.ptf.routevision_final.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import unze.ptf.routevision_final.model.Tura;
import unze.ptf.routevision_final.repository.TuraDAO;
import unze.ptf.routevision_final.controller.SessionManager;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class TuraController {

    @FXML private TableView<Tura> tableView;
    @FXML private TableColumn<Tura, String> brojCol;
    @FXML private TableColumn<Tura, String> relacijaCol;
    @FXML private TableColumn<Tura, String> datumCol;
    @FXML private TableColumn<Tura, String> vrijemeCol; // Nova kolona
    @FXML private TableColumn<Tura, Integer> kmCol;
    @FXML private TableColumn<Tura, Double> gorivoCol; // Nova kolona
    @FXML private TableColumn<Tura, String> statusCol;
    @FXML private Button btnDelete;

    private TuraDAO turaDAO = new TuraDAO();

    @FXML
    public void initialize() {
        setupColumns();
        loadData();

        if (SessionManager.getInstance() != null) {
            String role = SessionManager.getInstance().getUserRole();
            if (!"Admin".equals(role) && btnDelete != null) {
                btnDelete.setVisible(false);
            }
        }
    }

    private void setupColumns() {
        // 1. Kolona za broj ture
        brojCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBroj_tura()));

        // 2. Kolona za relaciju (Od -> Do)
        relacijaCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getLokacija_pocetka() + " -> " + c.getValue().getLokacija_kraja()));

        // 3. Kolona za datum početka
        datumCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDatum_pocetka() != null ? c.getValue().getDatum_pocetka().toString() : "-"));

        // 4. Kolona za vrijeme početka
        vrijemeCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getVrijeme_pocetka() != null ? c.getValue().getVrijeme_pocetka().toString() : "-"));

        // 5. Kolona za kilometre (Prikazuje "km" samo ako je tura završena)
        kmCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getPrijedeni_kilometri()).asObject());
        kmCol.setCellFactory(tc -> new TableCell<Tura, Integer>() {
            @Override
            protected void updateItem(Integer km, boolean empty) {
                super.updateItem(km, empty);
                Tura tura = getTableRow().getItem();

                // Provjera: ako je tura prazna, u toku ili su km 0, ispiši "-"
                if (empty || tura == null || !"Završena".equals(tura.getStatus()) || km == null || km == 0) {
                    setText("-");
                } else {
                    setText(km + " km");
                }
            }
        });

        // 6. Kolona za gorivo (Prikazuje "L" i 2 decimale samo ako je tura završena)
        gorivoCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getSpent_fuel()).asObject());
        gorivoCol.setCellFactory(tc -> new TableCell<Tura, Double>() {
            @Override
            protected void updateItem(Double fuel, boolean empty) {
                super.updateItem(fuel, empty);
                Tura tura = getTableRow().getItem();

                if (empty || tura == null || !"Završena".equals(tura.getStatus()) || fuel == null || fuel == 0) {
                    setText("-");
                } else {
                    setText(String.format("%.2f L", fuel));
                }
            }
        });

        // 7. Kolona za status
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
    }
    @FXML private void handleRefresh() { loadData(); }

    private void loadData() {
        try {
            if (SessionManager.getInstance() == null) return;
            String role = SessionManager.getInstance().getUserRole();
            int userId = SessionManager.getInstance().getUserId();

            List<Tura> listaTura = "Admin".equals(role) ? turaDAO.findAll() : turaDAO.findByVozacId(userId);
            tableView.setItems(FXCollections.observableArrayList(listaTura));
        } catch (SQLException e) {
            showAlert("Greška", "Nije moguće učitati ture.");
        }
    }

    @FXML private void handleStatusInProgress() { azurirajStatus("U toku"); }
    @FXML private void handleStatusCancel() { azurirajStatus("Prekinuta"); }

    @FXML
    private void handleZavrsiTuru() {
        Tura sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert("Upozorenje", "Molimo odaberite turu.");
            return;
        }

        // Otvaramo dijalog za unos kilometara
        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle("Završetak ture");
        dialog.setHeaderText("Tura: " + sel.getBroj_tura());
        dialog.setContentText("Unesite ukupno pređene kilometre:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(km -> {
            try {
                int predjeniKM = Integer.parseInt(km);
                sel.setPrijedeni_kilometri(predjeniKM);

                // Automatsko računanje potrošnje (npr. prosjek 30L/100km)
                double potrosnja = (predjeniKM / 100.0) * 30.0;
                sel.setSpent_fuel(potrosnja);
                sel.setFuel_used(potrosnja);

                // Računanje prosječne brzine
                sel.setDatum_kraja(LocalDate.now());
                sel.setVrijeme_kraja(LocalTime.now());
                sel.setProsjecna_brzina(izracunajBrzinu(sel, predjeniKM));

                sel.setStatus("Završena");
                turaDAO.update(sel);
                loadData();
                showAlert("Uspjeh", "Tura uspješno završena!");
            } catch (NumberFormatException e) {
                showAlert("Greška", "Kilometri moraju biti broj.");
            } catch (SQLException e) {
                showAlert("Greška", "Greška pri čuvanju podataka.");
            }
        });
    }

    private int izracunajBrzinu(Tura t, int km) {
        if (t.getDatum_pocetka() == null || t.getVrijeme_pocetka() == null) return 0;

        LocalDateTime start = LocalDateTime.of(t.getDatum_pocetka(), t.getVrijeme_pocetka());
        LocalDateTime end = LocalDateTime.now();
        long sati = Duration.between(start, end).toHours();

        return sati > 0 ? (int)(km / sati) : km; // Ako je trajalo manje od sat, uzmi km kao brzinu
    }

    private void azurirajStatus(String noviStatus) {
        Tura sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { showAlert("Upozorenje", "Odaberite turu."); return; }

        try {
            sel.setStatus(noviStatus);
            turaDAO.update(sel);
            loadData();
        } catch (SQLException e) {
            showAlert("Greška", "Promjena statusa nije uspjela.");
        }
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
                } catch (SQLException e) {
                    showAlert("Greška", "Brisanje nije uspjelo.");
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equalsIgnoreCase("Greška") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}