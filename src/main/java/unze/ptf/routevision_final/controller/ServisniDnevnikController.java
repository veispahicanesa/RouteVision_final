package unze.ptf.routevision_final.controller;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import unze.ptf.routevision_final.model.ServisniDnevnik;
import unze.ptf.routevision_final.repository.ServisniDnevnikDAO;
import unze.ptf.routevision_final.repository.KamionDAO;
import unze.ptf.routevision_final.controller.SessionManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ServisniDnevnikController {

    @FXML private TableView<ServisniDnevnik> tableView;
    @FXML private TableColumn<ServisniDnevnik, Integer> idCol;
    @FXML private TableColumn<ServisniDnevnik, String> kamionCol;
    @FXML private TableColumn<ServisniDnevnik, String> vozacCol;
    @FXML private TableColumn<ServisniDnevnik, LocalDate> datumCol;
    @FXML private TableColumn<ServisniDnevnik, String> vrstaCol;
    @FXML private TableColumn<ServisniDnevnik, Integer> kmCol;
    @FXML private TableColumn<ServisniDnevnik, Double> trosciCol;
    @FXML private TableColumn<ServisniDnevnik, String> servisarCol;
    @FXML private TableColumn<ServisniDnevnik, String> korisnikCol;
    @FXML private VBox mainContainer;
    private ServisniDnevnikDAO dao = new ServisniDnevnikDAO();

    @FXML
    public void initialize() {

        setupTableColumns();
        loadDnevnikData();
    }

    private void setupTableColumns() {
        // 1. Redni broj - POPRAVLJENO
        idCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(getIndex() + 1));


                    if (!getStyleClass().contains("table-cell")) {
                        getStyleClass().add("table-cell");
                    }
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });





        kamionCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getRegistracijaKamiona() != null ? cellData.getValue().getRegistracijaKamiona() : "N/A"
        ));
        vozacCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getImeVozaca() != null ? cellData.getValue().getImeVozaca() : "Nema vozača"
        ));
        datumCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDatum_servisa()));
        vrstaCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVrsta_servisa()));
        kmCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getKm_na_servisu()));
        servisarCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getServiser_naziv()));
        korisnikCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKreirao_korisnik()));

        // 3. Posebno formatiranje za TROŠKOVE
        trosciCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTroskovi()));
        trosciCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                    getStyleClass().removeAll("skupo", "jeftino");
                } else {
                    setText(String.format("%.2f KM", price));

                    // OVO JE KLJUČNO: Ne koristi setStyle, nego klase
                    getStyleClass().removeAll("skupo", "jeftino");
                    if (price > 1000) {
                        getStyleClass().add("skupo");
                    } else {
                        getStyleClass().add("jeftino");
                    }
                }
            }
        });
    }

    @FXML
    public void loadDnevnikData() {
        try {
            SessionManager session = SessionManager.getInstance();
            String role = (session.getUserRole() != null) ? session.getUserRole().trim() : "";
            int userId = session.getUserId();

            // OVO ĆE TI TAČNO REĆI ŠTA JE PROBLEM U KONZOLI
            System.out.println("--- DEBUG INFO ---");
            System.out.println("Uloga iz sesije: '" + role + "'");
            System.out.println("ID korisnika: " + userId);

            List<ServisniDnevnik> lista;

            // Provjeravamo sve varijacije (velika/mala slova i sadržaj)
            if (role.equalsIgnoreCase("VOZAC") || role.equalsIgnoreCase("VOZAČ")) {
                System.out.println("ISHOD: Prepoznat kao VOZAČ. Pozivam findForVozac(" + userId + ")");
                lista = dao.findForVozac(userId);
            } else {
                System.out.println("ISHOD: Prepoznat kao ADMIN (ili nepoznato). Pozivam findAll()");
                lista = dao.findAll();
            }

            tableView.setItems(FXCollections.observableArrayList(lista));
            tableView.refresh();
            System.out.println("------------------");

        } catch (SQLException e) {
            showAlert("Greška", "Baza: " + e.getMessage());
        }
    }
    @FXML
    private void showAddServisDialog() {
        try {
            int currentUserId = SessionManager.getInstance().getUserId();
            KamionDAO kDao = new KamionDAO();
            var kamioni = kDao.findByVozacId(currentUserId);

            if (kamioni.isEmpty()) {
                showAlert("Greška", "Sistem ne pronalazi kamion zadužen na Vaše ime.");
                return;
            }

            // --- DEFINICIJA DIJALOGA I POLJA ---
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Novi Unos u Servisni Dnevnik");

            GridPane grid = new GridPane();
            grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

            TextField vrstaField = new TextField();
            TextField trosakField = new TextField();
            TextField kmField = new TextField();
            TextField serviserField = new TextField();
            TextArea opisArea = new TextArea(); // Definisan ovdje da bi ga Java vidjela kasnije
            opisArea.setPrefRowCount(3);

            grid.add(new Label("Vrsta servisa:"), 0, 0); grid.add(vrstaField, 1, 0);
            grid.add(new Label("Trošak (KM):"), 0, 1);    grid.add(trosakField, 1, 1);
            grid.add(new Label("Kilometraža:"), 0, 2);   grid.add(kmField, 1, 2);
            grid.add(new Label("Serviser:"), 0, 3);       grid.add(serviserField, 1, 3);
            grid.add(new Label("Opis/Napomena:"), 0, 4);  grid.add(opisArea, 1, 4);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // --- POKRETANJE I REZULTAT (res) ---
            Optional<ButtonType> res = dialog.showAndWait(); // 'res' je sada definisan

            if (res.isPresent() && res.get() == ButtonType.OK) {
                ServisniDnevnik s = new ServisniDnevnik();
                s.setKamion_id(kamioni.get(0).getId());
                s.setVozac_id(currentUserId);
                s.setDatum_servisa(LocalDate.now());
                s.setVrsta_servisa(vrstaField.getText());
                s.setOpisServisa(opisArea.getText()); // Sada dostupno

                try {
                    s.setKm_na_servisu(Integer.parseInt(kmField.getText().replace(" ", "")));
                    s.setTroskovi(Double.parseDouble(trosakField.getText().replace(",", ".")));
                } catch (NumberFormatException e) {
                    showAlert("Greška", "Kilometri i trošak moraju biti brojevi!");
                    return;
                }

                s.setServiser_naziv(serviserField.getText());

                // Koristimo fiksni string ili ID pošto ne diramo SessionManager
                s.setKreirao_korisnik("Vozač (Sistem)");
                s.setAktivan(true);

                dao.save(s);
                loadDnevnikData();
            }
        } catch (Exception e) {
            showAlert("Greška", "Došlo je do problema: " + e.getMessage());
        }
    }

    @FXML
    private void editSelectedServis() {
        ServisniDnevnik selected = tableView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Upozorenje", "Odaberite servis iz tabele koji želite urediti!");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Uredi Servisni Zapis");

        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(10); grid.setPadding(new Insets(20));

        // Definisanje polja
        TextField vrstaField = new TextField(selected.getVrsta_servisa());
        TextField trosakField = new TextField(String.valueOf(selected.getTroskovi()));
        TextField kmField = new TextField(String.valueOf(selected.getKm_na_servisu()));
        TextField serviserField = new TextField(selected.getServiser_naziv());

        TextArea opisArea = new TextArea(selected.getOpisServisa()); // Osiguraj da geter vraća string iz baze
        opisArea.setPrefRowCount(3);
        opisArea.setWrapText(true);

        // Raspored u gridu (isto kao kod Add dijaloga radi konzistentnosti)
        grid.add(new Label("Vrsta servisa:"), 0, 0); grid.add(vrstaField, 1, 0);
        grid.add(new Label("Trošak (KM):"), 0, 1);    grid.add(trosakField, 1, 1);
        grid.add(new Label("Kilometraža:"), 0, 2);   grid.add(kmField, 1, 2);

        grid.add(new Label("Serviser:"), 2, 0);       grid.add(serviserField, 3, 0);
        grid.add(new Label("Opis/Napomena:"), 2, 1);  grid.add(opisArea, 3, 1, 1, 2);
        grid.add(new Label("Kamion:"), 0, 3);
        grid.add(new Label(selected.getRegistracijaKamiona()), 1, 3);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            try {
                // Ažuriramo postojeći objekat 'selected' novim vrijednostima iz polja
                selected.setVrsta_servisa(vrstaField.getText());
                selected.setOpisServisa(opisArea.getText());

                // Parsiranje brojeva uz osnovnu zaštitu
                int km = kmField.getText().isEmpty() ? 0 : Integer.parseInt(kmField.getText().replace(".", ""));
                double trosak = trosakField.getText().isEmpty() ? 0.0 : Double.parseDouble(trosakField.getText().replace(",", "."));

                selected.setKm_na_servisu(km);
                selected.setTroskovi(trosak);
                selected.setServiser_naziv(serviserField.getText());

                // Zadržavamo originalni kamion_id i vozac_id, ali ažuriramo bazu
                dao.update(selected);

                // Osvježavamo tabelu da se vide promjene
                loadDnevnikData();

            } catch (NumberFormatException e) {
                showAlert("Greška", "Trošak i kilometraža moraju biti validni brojevi!");
            } catch (SQLException e) {
                showAlert("Greška u bazi", "Nije moguće spasiti promjene: " + e.getMessage());
            } catch (Exception e) {
                showAlert("Greška", "Došlo je do neočekivanog problema.");
            }
        }
    }
    @FXML
    private void deleteSelectedServis() {
        ServisniDnevnik selected = tableView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Upozorenje", "Molimo odaberite servis iz tabele koji želite obrisati.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potvrda brisanja");
        // Prikazujemo registraciju umjesto ID-a
        alert.setHeaderText("Brisanje servisa za kamion: " + selected.getRegistracijaKamiona());
        alert.setContentText("Jeste li sigurni da želite ukloniti ovaj zapis?");

        Optional<ButtonType> response = alert.showAndWait();
        if (response.isPresent() && response.get() == ButtonType.OK) { // Provjeri da li koristiš OK ili YES dugme
            try {
                dao.delete(selected.getId());
                loadDnevnikData();
            } catch (SQLException e) {
                showAlert("Greška pri brisanju", e.getMessage());
            }
        }
    }
    private void showAlert(String t, String m) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(t);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
}