package unze.ptf.routevision_final.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import unze.ptf.routevision_final.config.DatabaseConfig;
import unze.ptf.routevision_final.model.Kamion;
import unze.ptf.routevision_final.model.Narudzba;
import unze.ptf.routevision_final.model.Tura;
import unze.ptf.routevision_final.model.Vozac;
import unze.ptf.routevision_final.repository.KamionDAO;
import unze.ptf.routevision_final.repository.NarudzbaDAO;
import unze.ptf.routevision_final.repository.TuraDAO;
import unze.ptf.routevision_final.controller.SessionManager;
import unze.ptf.routevision_final.repository.VozacDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
    private NarudzbaDAO narudzbaDAO = new NarudzbaDAO();
    private void setupColumns() {
        // 1. Kolona za broj ture
        brojCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBroj_tura()));

        // 2. Kolona za relaciju (Od -> Do)
        // 2. Kolona za relaciju (Sada uključuje KLIJENTA i lokacije)
        relacijaCol.setCellValueFactory(c -> {
            // Naziv firme koji je došao iz JOIN-a u DAO
            String klijent = (c.getValue().getNapomena() != null) ? c.getValue().getNapomena() : "N/A";
            String gradovi = c.getValue().getLokacija_pocetka() + " -> " + c.getValue().getLokacija_kraja();

            return new SimpleStringProperty(klijent + " | " + gradovi);
        });
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
                // Izmjeni ovaj IF u setupColumns:
                if (empty || tura == null || km == null || km == 0) {
                    setText("-");
                } else {
                    // Sada će ispisati kilometre i za "Završena" i za "Prekinuta"
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


                if (empty || tura == null || fuel == null || fuel == 0) {
                    setText("");
                } else if ("Završena".equals(tura.getStatus()) || "Prekinuta".equals(tura.getStatus())) {
                    setText(String.format("%.2f L", fuel));
                } else {
                    setText("");
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
            SessionManager session = SessionManager.getInstance();
            List<Tura> prikazTura;

            // Ako je korisnik Admin, vidi sve ture
            if ("Admin".equals(session.getUserRole())) {
                prikazTura = turaDAO.findAll();
            }
            // Ako je korisnik Vozac, vidi samo svoje ture preko svog ID-a
            else {
                Vozac ulogovaniVozac = (Vozac) session.getCurrentUser();
                prikazTura = turaDAO.findByVozacId(ulogovaniVozac.getId());
            }

            tableView.getItems().clear();
            tableView.getItems().addAll(prikazTura);
            tableView.refresh(); // Osigurava da se UI osvježi

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Greška", "Učitavanje neuspješno: " + e.getMessage());
        }
    }

    @FXML private void handleStatusInProgress() { azurirajStatus("U toku"); }


    @FXML
    private void handleZavrsiTuru() {
        Tura sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert("Upozorenje", "Molimo odaberite turu.");
            return;
        }

        if ("Završena".equals(sel.getStatus())) {
            showAlert("Upozorenje", "Ova tura je već završena.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle("Završetak ture");
        dialog.setHeaderText("Tura: " + sel.getBroj_tura());
        dialog.setContentText("Unesite ukupno pređene kilometre:");

        dialog.showAndWait().ifPresent(km -> {
            try {
                int predjeniKM = Integer.parseInt(km);
                sel.setPrijedeni_kilometri(predjeniKM);

                // Tvoja logika za gorivo i brzinu
                double potrosnja = (predjeniKM / 100.0) * 30.0;
                sel.setSpent_fuel(potrosnja);
                sel.setFuel_used(potrosnja);
                sel.setDatum_kraja(LocalDate.now());
                sel.setVrijeme_kraja(LocalTime.now());
                sel.setProsjecna_brzina(izracunajBrzinu(sel, predjeniKM));
                sel.setStatus("Završena");

                // 1. Ažuriraj turu u bazi
                turaDAO.update(sel);


                kreirajFakturuAutomatski(sel);

                loadData();
                tableView.refresh();
                showAlert("Uspjeh", "Tura završena i faktura je automatski generisana!");

            } catch (NumberFormatException e) {
                showAlert("Greška", "Kilometri moraju biti broj.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Greška", "Greška pri radu sa bazom.");
            }
        });
    }

    private void kreirajFakturuAutomatski(Tura tura) throws SQLException {
        // SQL koji povlači klijent_id iz narudžbe povezane sa turom
        String sql = "INSERT INTO fakture (broj_fakture, tura_id, klijent_id, datum_izdavanja, " +
                "broj_km, cijena_po_km, iznos_usluge, porez, ukupan_iznos, vrsta_usluge, status_placanja) " +
                "SELECT CONCAT('INV-', ?), t.id, n.klijent_id, CURDATE(), ?, 2.0, " +
                "(? * 2.0), (? * 2.0 * 0.17), (? * 2.0 * 1.17), 'Prevoz robe', 'Neplaćeno' " +
                "FROM tura t " +
                "JOIN narudzba n ON t.narudzba_id = n.id " +
                "WHERE t.id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tura.getBroj_tura());
            stmt.setInt(2, tura.getId());
            stmt.setInt(3, tura.getPrijedeni_kilometri());
            stmt.setInt(4, tura.getPrijedeni_kilometri());
            stmt.setInt(5, tura.getPrijedeni_kilometri());
            stmt.setInt(6, tura.getId());

            stmt.executeUpdate();
        }
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
        // 1. Dohvati označenu turu iz tabele
        Tura sel = tableView.getSelectionModel().getSelectedItem();

        if (sel == null) {
            showAlert("Upozorenje", "Molimo odaberite turu koju želite obrisati.");
            return;
        }

        // 2. Potvrda brisanja
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potvrda brisanja");
        alert.setHeaderText("Brisanje ture: " + sel.getBroj_tura());
        alert.setContentText("Da li ste sigurni da želite TRAJNO obrisati ovu turu iz baze?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Pozivamo DAO da izvrši fizičko brisanje
                turaDAO.hardDelete(sel.getId());

                // 3. Osvježi prikaz
                loadData();
                showAlert("Uspjeh", "Tura je trajno obrisana.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Greška", "Brisanje nije uspjelo. Moguće je da postoje fakture vezane za ovu turu.");
            }
        }
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
        TextField kmField = new TextField("0");
        kmField.setPromptText("Unesite pređene km");

        // Novo: Polje za vrijeme (podrazumijevano trenutno vrijeme)
        TextField vrijemeField = new TextField(LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        vrijemeField.setPromptText("HH:mm");

        // Novo: Status (podrazumijevano "U toku" ili "Novo" prema tvojoj slici)
        ComboBox<String> comboStatus = new ComboBox<>(FXCollections.observableArrayList("Novo", "U toku", "Završena"));
        comboStatus.setValue("Novo");

        // ComboBox-ovi za odabir vozača i kamiona
        ComboBox<Vozac> comboVozaci = new ComboBox<>();
        ComboBox<Kamion> comboKamioni = new ComboBox<>();
        ComboBox<Narudzba> comboNarudzbe = new ComboBox<>();
        try {
            comboVozaci.setItems(FXCollections.observableArrayList(vozacDAO.findAll()));
            comboKamioni.setItems(FXCollections.observableArrayList(kamionDAO.findAll()));
            List<Narudzba> sveNarudzbe = narudzbaDAO.findAll();
            comboNarudzbe.setItems(FXCollections.observableArrayList(sveNarudzbe));
            comboVozaci.setConverter(new javafx.util.StringConverter<>() {
                @Override public String toString(Vozac v) { return v == null ? "" : v.getIme() + " " + v.getPrezime(); }
                @Override public Vozac fromString(String s) { return null; }
            });

            comboKamioni.setConverter(new javafx.util.StringConverter<>() {
                @Override public String toString(Kamion k) { return k == null ? "" : k.getRegistarska_tablica() + " (" + k.getMarka() + ")"; }
                @Override public Kamion fromString(String s) { return null; }
            });

            // NOVO: Konverter za Narudžbu - da admin vidi broj narudžbe i ime klijenta
            comboNarudzbe.setConverter(new javafx.util.StringConverter<>() {
                @Override public String toString(Narudzba n) {
                    return n == null ? "" : n.getBroj_narudzbe() + " | " + n.getNazivKlijenta();
                }
                @Override public Narudzba fromString(String s) { return null; }
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
        grid.add(new Label("Vezana Narudžba:"), 0, 9);
        grid.add(comboNarudzbe, 1, 9);
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
                    n.setPrijedeni_kilometri(Integer.parseInt(kmField.getText()));
                    double potrosnja = (n.getPrijedeni_kilometri() / 100.0) * 30.0;
                    n.setSpent_fuel(potrosnja);
                    n.setFuel_used(potrosnja);
                    if (comboNarudzbe.getValue() != null) {
                        n.setNarudzba_id(comboNarudzbe.getValue().getId());
                    }
                    if(SessionManager.getInstance() != null) {
                        n.setKreirao_admin_id(String.valueOf(SessionManager.getInstance().getUserId()));
                    }
                    return n;
                } catch (Exception e) {
                    showAlert("Greška", "Provjerite unos podataka (Kilometri moraju biti broj!)");
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
        TextField kmField = new TextField(String.valueOf(odabranaTura.getPrijedeni_kilometri()));
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
                    int noviKm = Integer.parseInt(kmField.getText());
                    odabranaTura.setPrijedeni_kilometri(noviKm);
                    double novaPotrosnja = (noviKm / 100.0) * 30.0;
                    odabranaTura.setSpent_fuel(novaPotrosnja);
                    odabranaTura.setFuel_used(novaPotrosnja);

                    if (comboVozaci.getValue() != null) odabranaTura.setVozac_id(comboVozaci.getValue().getId());
                    if (comboKamioni.getValue() != null) odabranaTura.setKamion_id(comboKamioni.getValue().getId());

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
    @FXML
    private void handleStatusCancel() {
        Tura sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert("Upozorenje", "Odaberite turu.");
            return;
        }

        // Otvaramo prozor za unos kilometara iako je tura prekinuta
        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle("Prekid ture");
        dialog.setHeaderText("Tura: " + sel.getBroj_tura());
        dialog.setContentText("Unesite kilometre pređene do trenutka prekida:");

        dialog.showAndWait().ifPresent(km -> {
            try {
                int predjeniKM = Integer.parseInt(km);
                sel.setPrijedeni_kilometri(predjeniKM);

                // Računamo potrošnju do prekida
                double potrosnja = (predjeniKM / 100.0) * 30.0;
                sel.setSpent_fuel(potrosnja);
                sel.setFuel_used(potrosnja);

                sel.setStatus("Prekinuta");
                sel.setDatum_kraja(LocalDate.now());
                sel.setVrijeme_kraja(LocalTime.now());

                // Spašavamo u bazu
                turaDAO.update(sel);
                loadData();
                showAlert("Informacija", "Tura je označena kao prekinuta sa " + predjeniKM + " pređenih km.");
            } catch (NumberFormatException e) {
                showAlert("Greška", "Kilometri moraju biti broj.");
            } catch (SQLException e) {
                showAlert("Greška", "Problem sa bazom.");
            }
        });
    }
}