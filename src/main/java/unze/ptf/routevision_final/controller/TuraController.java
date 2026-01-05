package unze.ptf.routevision_final.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import unze.ptf.routevision_final.model.Kamion;
import unze.ptf.routevision_final.model.Tura;
import unze.ptf.routevision_final.model.Vozac;
import unze.ptf.routevision_final.repository.KamionDAO;
import unze.ptf.routevision_final.repository.TuraDAO;
import unze.ptf.routevision_final.controller.SessionManager;
import unze.ptf.routevision_final.repository.VozacDAO;

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
    @FXML private Button btnAddTura;
    private TuraDAO turaDAO = new TuraDAO();
    @FXML private Button btnEditTura;
    @FXML private TableColumn<Tura, String> vozacCol;
    @FXML private TableColumn<Tura, String> kamionCol;

    @FXML
    public void initialize() {
        setupColumns();
        loadData();

        if (SessionManager.getInstance() != null) {
            String role = SessionManager.getInstance().getUserRole();
            boolean isAdmin = "Admin".equals(role);

            if (btnDelete != null) btnDelete.setVisible(isAdmin);
            if (btnEditTura != null) btnEditTura.setVisible(isAdmin);
            if (btnAddTura != null) btnAddTura.setVisible(isAdmin); // Vozač ne vidi "Nova tura"
        }
    }
    private VozacDAO vozacDAO = new VozacDAO();
    private KamionDAO kamionDAO = new KamionDAO();

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
        vozacCol.setCellValueFactory(c -> {
            int id = c.getValue().getVozac_id();
            try {
                Vozac v = vozacDAO.findById(id); // Koristimo vašu metodu iz VozacDAO
                if (v != null) {
                    return new SimpleStringProperty(v.getIme() + " " + v.getPrezime());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty("Nepoznat (" + id + ")");
        });

        // 2. Kolona za Kamion - pretvaranje ID-a u Registraciju
        kamionCol.setCellValueFactory(c -> {
            int id = c.getValue().getKamion_id();
            try {
                // Pronađite kamion po ID-u (provjerite imate li findById u KamionDAO)
                // Ako nemate findById, možemo koristiti findAll i filtrirati
                List<Kamion> svi = kamionDAO.findAll();
                Optional<Kamion> k = svi.stream().filter(kam -> kam.getId() == id).findFirst();
                if (k.isPresent()) {
                    return new SimpleStringProperty(k.get().getRegistarska_tablica());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty("ID: " + id);
        });
        // 7. Kolona za status
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
    }
    @FXML private void handleRefresh() { loadData(); }

    private void loadData() {
        try {
            List<Tura> svjezeTure = turaDAO.findAll();
            tableView.getItems().clear(); // POTPUNO OBRIŠI TRENUTNI PRIKAZ
            tableView.getItems().addAll(svjezeTure); // DODAJ SVE PONOVO KAO NOVE STAVKE
        } catch (SQLException e) {
            showAlert("Greška", "Učitavanje neuspješno.");
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
    @FXML
    private void handlePrikaziFormuZaDodavanje() {
        Dialog<Tura> dialog = new Dialog<>();
        dialog.setTitle("Dodjeljivanje nove ture");
        dialog.setHeaderText("Unesite detalje za novu turu");

        ButtonType spremiButtonType = new ButtonType("Kreiraj", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(spremiButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Osnovna polja
        TextField brojTure = new TextField();
        TextField polaziste = new TextField();
        TextField odrediste = new TextField();
        DatePicker datum = new DatePicker(LocalDate.now());

        // Novo: Polje za vrijeme (podrazumijevano trenutno vrijeme)
        TextField vrijemeField = new TextField(LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        vrijemeField.setPromptText("HH:mm");

        // Novo: Status (podrazumijevano "U toku" ili "Novo" prema tvojoj slici)
        ComboBox<String> comboStatus = new ComboBox<>(FXCollections.observableArrayList("Novo", "U toku", "Završena"));
        comboStatus.setValue("Novo");

        // ComboBox-ovi za odabir vozača i kamiona
        ComboBox<Vozac> comboVozaci = new ComboBox<>();
        ComboBox<Kamion> comboKamioni = new ComboBox<>();

        try {
            comboVozaci.setItems(FXCollections.observableArrayList(vozacDAO.findAll()));
            comboKamioni.setItems(FXCollections.observableArrayList(kamionDAO.findAll()));

            // Postavljanje prikaza imena i registracija
            comboVozaci.setConverter(new javafx.util.StringConverter<>() {
                @Override public String toString(Vozac v) { return v == null ? "" : v.getIme() + " " + v.getPrezime(); }
                @Override public Vozac fromString(String s) { return null; }
            });

            comboKamioni.setConverter(new javafx.util.StringConverter<>() {
                @Override public String toString(Kamion k) { return k == null ? "" : k.getRegistarska_tablica() + " (" + k.getMarka() + ")"; }
                @Override public Kamion fromString(String s) { return null; }
            });
        } catch (SQLException e) { e.printStackTrace(); }

        grid.add(new Label("Broj ture:"), 0, 0);
        grid.add(brojTure, 1, 0);
        grid.add(new Label("Polazište:"), 0, 1);
        grid.add(polaziste, 1, 1);
        grid.add(new Label("Odredište:"), 0, 2);
        grid.add(odrediste, 1, 2);
        grid.add(new Label("Datum:"), 0, 3);
        grid.add(datum, 1, 3);
        grid.add(new Label("Vrijeme:"), 0, 4);
        grid.add(vrijemeField, 1, 4);
        grid.add(new Label("Vozač:"), 0, 5);
        grid.add(comboVozaci, 1, 5);
        grid.add(new Label("Kamion:"), 0, 6);
        grid.add(comboKamioni, 1, 6);
        grid.add(new Label("Status:"), 0, 7);
        grid.add(comboStatus, 1, 7);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == spremiButtonType) {
                try {
                    Tura n = new Tura();
                    n.setBroj_tura(brojTure.getText());
                    n.setLokacija_pocetka(polaziste.getText());
                    n.setLokacija_kraja(odrediste.getText());
                    n.setDatum_pocetka(datum.getValue());
                    n.setVrijeme_pocetka(LocalTime.parse(vrijemeField.getText()));
                    n.setVozac_id(comboVozaci.getValue().getId());
                    n.setKamion_id(comboKamioni.getValue().getId());
                    n.setStatus(comboStatus.getValue());
                    n.setAktivan(true);

                    // Početni kilometri su 0, gorivo se računa tek kad se tura završi
                    n.setPrijedeni_kilometri(0);
                    n.setSpent_fuel(0.0);
                    n.setFuel_used(0.0);

                    if(SessionManager.getInstance() != null) {
                        n.setKreirao_admin_id(String.valueOf(SessionManager.getInstance().getUserId()));
                    }
                    return n;
                } catch (Exception e) {
                    showAlert("Greška", "Provjerite unos podataka (Vrijeme mora biti u formatu HH:mm)");
                    return null;
                }
            }
            return null;
        });

        Optional<Tura> result = dialog.showAndWait();
        result.ifPresent(novaTura -> {
            try {
                turaDAO.save(novaTura);
                loadData();
                showAlert("Uspjeh", "Tura je uspješno kreirana!");
            } catch (SQLException e) {
                showAlert("Greška", "Problem sa bazom: " + e.getMessage());
            }
        });
    }
    @FXML
    private void handleEditTura() {
        Tura odabranaTura = tableView.getSelectionModel().getSelectedItem();
        if (odabranaTura == null) {
            showAlert("Upozorenje", "Molimo odaberite turu koju želite urediti.");
            return;
        }

        Dialog<Tura> dialog = new Dialog<>();
        dialog.setTitle("Uređivanje ture");
        dialog.setHeaderText("Izmjena podataka za: " + odabranaTura.getBroj_tura());

        ButtonType spremiButtonType = new ButtonType("Sačuvaj", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(spremiButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField polaziste = new TextField(odabranaTura.getLokacija_pocetka());
        TextField odrediste = new TextField(odabranaTura.getLokacija_kraja());

        // Provjera da li je vrijeme null prije toString()
        String trenutnoVrijeme = odabranaTura.getVrijeme_pocetka() != null ? odabranaTura.getVrijeme_pocetka().toString() : "08:00";
        TextField vrijemeField = new TextField(trenutnoVrijeme);

        ComboBox<Vozac> comboVozaci = new ComboBox<>();
        ComboBox<Kamion> comboKamioni = new ComboBox<>();
        ComboBox<String> comboStatus = new ComboBox<>(FXCollections.observableArrayList("Novo", "U toku", "Završena", "Prekinuta"));
        comboStatus.setValue(odabranaTura.getStatus());

        try {
            // Punjenje vozača
            List<Vozac> sviVozaci = vozacDAO.findAll();
            comboVozaci.setItems(FXCollections.observableArrayList(sviVozaci));
            comboVozaci.setConverter(new javafx.util.StringConverter<Vozac>() {
                @Override public String toString(Vozac v) { return v == null ? "" : v.getIme() + " " + v.getPrezime(); }
                @Override public Vozac fromString(String s) { return null; }
            });
            sviVozaci.stream().filter(v -> v.getId() == odabranaTura.getVozac_id()).findFirst().ifPresent(comboVozaci::setValue);

            // Punjenje kamiona
            List<Kamion> sviKamioni = kamionDAO.findAll();
            comboKamioni.setItems(FXCollections.observableArrayList(sviKamioni));
            comboKamioni.setConverter(new javafx.util.StringConverter<Kamion>() {
                @Override public String toString(Kamion k) { return k == null ? "" : k.getRegistarska_tablica() + " (" + k.getMarka() + ")"; }
                @Override public Kamion fromString(String s) { return null; }
            });
            sviKamioni.stream().filter(k -> k.getId() == odabranaTura.getKamion_id()).findFirst().ifPresent(comboKamioni::setValue);

        } catch (Exception e) {
            System.out.println("Greška pri punjenju combo boxova: " + e.getMessage());
        }

        grid.add(new Label("Polazište:"), 0, 0);
        grid.add(polaziste, 1, 0);
        grid.add(new Label("Odredište:"), 0, 1);
        grid.add(odrediste, 1, 1);
        grid.add(new Label("Vrijeme:"), 0, 2);
        grid.add(vrijemeField, 1, 2);
        grid.add(new Label("Vozač:"), 0, 3);
        grid.add(comboVozaci, 1, 3);
        grid.add(new Label("Kamion:"), 0, 4);
        grid.add(comboKamioni, 1, 4);
        grid.add(new Label("Status:"), 0, 5);
        grid.add(comboStatus, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == spremiButtonType) {
                try {
                    odabranaTura.setLokacija_pocetka(polaziste.getText());
                    odabranaTura.setLokacija_kraja(odrediste.getText());
                    odabranaTura.setVrijeme_pocetka(LocalTime.parse(vrijemeField.getText()));

                    if (comboVozaci.getValue() != null) odabranaTura.setVozac_id(comboVozaci.getValue().getId());
                    if (comboKamioni.getValue() != null) odabranaTura.setKamion_id(comboKamioni.getValue().getId());

                    odabranaTura.setStatus(comboStatus.getValue());
                    return odabranaTura;
                } catch (Exception e) {
                    // Ako vrijeme nije HH:mm, ovdje će puknuti
                    System.out.println("Greška u konverziji podataka: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Tura> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                // 1. Ažuriraj bazu
                turaDAO.update(result.get());

                // 2. KLJUČNO: Moramo reći tabeli da su podaci "prljavi" i da ih ponovo pročita
                // Prvo pozovemo loadData da dobijemo novu listu iz baze
                loadData();

                // 3. Forsiramo UI refresh na dva nivoa
                tableView.getColumns().get(0).setVisible(false); // Mali trik za resetovanje renderera
                tableView.getColumns().get(0).setVisible(true);
                tableView.refresh();

                showAlert("Uspjeh", "Tura je izmijenjena i prikaz osvježen!");
            } catch (SQLException e) {
                showAlert("Greška", "Baza: " + e.getMessage());
            }
        }
    }
}