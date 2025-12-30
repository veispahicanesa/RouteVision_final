package unze.ptf.routevision_final.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/*
 * Model klasa Tura predstavlja jednu rutu/kamionsku vožnju.
 */
public class Tura {
    private int id;
    private String broj_tura;
    private int vozac_id;
    private int kamion_id;
    private int narudzba_id;
    private LocalDate datum_pocetka;
    private LocalTime vrijeme_pocetka;
    private LocalDate datum_kraja;
    private LocalTime vrijeme_kraja;
    private String lokacija_pocetka;
    private String lokacija_kraja;
    private int prijedeni_kilometri;
    private int prosjecna_brzina;
    private double spent_fuel;
    private double fuel_used;
    private String napomena;
    private String status;
    private boolean aktivan;
    private String kreirao_admin_id;
    private String kreirao_vozac_id;
    private LocalDateTime datum_kreiranja;

    // 1. Prazan konstruktor
    public Tura() {}

    // 2. Veliki konstruktor sa svim parametrima
    public Tura(String broj_tura, int vozac_id, int kamion_id, int narudzba_id, LocalDate datum_pocetka,
                LocalTime vrijeme_pocetka, LocalDate datum_kraja, LocalTime vrijeme_kraja,
                String lokacija_pocetka, String lokacija_kraja, int prijedeni_kilometri,
                int prosjecna_brzina, double spent_fuel, double fuel_used, String napomena,
                String status, boolean aktivan, String kreirao_admin_id,
                String kreirao_vozac_id, LocalDateTime datum_kreiranja) {
        this.broj_tura = broj_tura;
        this.vozac_id = vozac_id;
        this.kamion_id = kamion_id;
        this.narudzba_id = narudzba_id;
        this.datum_pocetka = datum_pocetka;
        this.vrijeme_pocetka = vrijeme_pocetka;
        this.datum_kraja = datum_kraja;
        this.vrijeme_kraja = vrijeme_kraja;
        this.lokacija_pocetka = lokacija_pocetka;
        this.lokacija_kraja = lokacija_kraja;
        this.prijedeni_kilometri = prijedeni_kilometri;
        this.prosjecna_brzina = prosjecna_brzina;
        this.spent_fuel = spent_fuel;
        this.fuel_used = fuel_used;
        this.napomena = napomena;
        this.status = status != null ? status : "U toku";
        this.aktivan = aktivan;
        this.kreirao_admin_id = kreirao_admin_id;
        this.kreirao_vozac_id = kreirao_vozac_id;
        this.datum_kreiranja = datum_kreiranja != null ? datum_kreiranja : LocalDateTime.now();
    }

    // --- Getteri i Setteri ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBroj_tura() { return broj_tura; }
    public void setBroj_tura(String broj_tura) { this.broj_tura = broj_tura; }

    public int getVozac_id() { return vozac_id; }
    public void setVozac_id(int vozac_id) { this.vozac_id = vozac_id; }

    public int getKamion_id() { return kamion_id; }
    public void setKamion_id(int kamion_id) { this.kamion_id = kamion_id; }

    public int getNarudzba_id() { return narudzba_id; }
    public void setNarudzba_id(int narudzba_id) { this.narudzba_id = narudzba_id; }

    public LocalDate getDatum_pocetka() { return datum_pocetka; }
    public void setDatum_pocetka(LocalDate datum_pocetka) { this.datum_pocetka = datum_pocetka; }

    public LocalTime getVrijeme_pocetka() { return vrijeme_pocetka; }
    public void setVrijeme_pocetka(LocalTime vrijeme_pocetka) { this.vrijeme_pocetka = vrijeme_pocetka; }

    public LocalDate getDatum_kraja() { return datum_kraja; }
    public void setDatum_kraja(LocalDate datum_kraja) { this.datum_kraja = datum_kraja; }

    public LocalTime getVrijeme_kraja() { return vrijeme_kraja; }
    public void setVrijeme_kraja(LocalTime vrijeme_kraja) { this.vrijeme_kraja = vrijeme_kraja; }

    public String getLokacija_pocetka() { return lokacija_pocetka; }
    public void setLokacija_pocetka(String lokacija_pocetka) { this.lokacija_pocetka = lokacija_pocetka; }

    public String getLokacija_kraja() { return lokacija_kraja; }
    public void setLokacija_kraja(String lokacija_kraja) { this.lokacija_kraja = lokacija_kraja; }

    public int getPrijedeni_kilometri() { return prijedeni_kilometri; }
    public void setPrijedeni_kilometri(int prijedeni_kilometri) { this.prijedeni_kilometri = prijedeni_kilometri; }

    public int getProsjecna_brzina() { return prosjecna_brzina; }
    public void setProsjecna_brzina(int prosjecna_brzina) { this.prosjecna_brzina = prosjecna_brzina; }

    public double getSpent_fuel() { return spent_fuel; }
    public void setSpent_fuel(double spent_fuel) { this.spent_fuel = spent_fuel; }

    public double getFuel_used() { return fuel_used; }
    public void setFuel_used(double fuel_used) { this.fuel_used = fuel_used; }

    public String getNapomena() { return napomena; }
    public void setNapomena(String napomena) { this.napomena = napomena; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isAktivan() { return aktivan; }
    public void setAktivan(boolean aktivan) { this.aktivan = aktivan; }

    public String getKreirao_admin_id() { return kreirao_admin_id; }
    public void setKreirao_admin_id(String kreirao_admin_id) { this.kreirao_admin_id = kreirao_admin_id; }

    public String getKreirao_vozac_id() { return kreirao_vozac_id; }
    public void setKreirao_vozac_id(String kreirao_vozac_id) { this.kreirao_vozac_id = kreirao_vozac_id; }

    public LocalDateTime getDatum_kreiranja() { return datum_kreiranja; }
    public void setDatum_kreiranja(LocalDateTime datum_kreiranja) { this.datum_kreiranja = datum_kreiranja; }
}