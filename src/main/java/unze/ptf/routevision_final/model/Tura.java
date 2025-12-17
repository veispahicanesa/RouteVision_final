package unze.ptf.routevision_final.model;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
/*
 * Model klasa Tura predstavlja jednu rutu/kamionsku vožnju.
 * Čuva podatke o vozilu, vozaču, narudžbi, vremenu i lokacijama.
 * Takođe prati kilometražu, potrošnju goriva i status ture.
 */
public class Tura {
    private int id;
    private String broj_tura;
    private int vozac_id;
    private int kamion_id;
    private int narudba_id;
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
    private LocalDateTime datum_kreiranja;

    public Tura() {}

    public Tura(String broj_tura, int vozac_id, int kamion_id, int narudba_id) {
        this.broj_tura = broj_tura;
        this.vozac_id = vozac_id;
        this.kamion_id = kamion_id;
        this.narudba_id = narudba_id;
        this.aktivan = true;
        this.status = "U toku";
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBroj_tura() { return broj_tura; }
    public void setBroj_tura(String broj_tura) { this.broj_tura = broj_tura; }

    public int getVozac_id() { return vozac_id; }
    public void setVozac_id(int vozac_id) { this.vozac_id = vozac_id; }

    public int getKamion_id() { return kamion_id; }
    public void setKamion_id(int kamion_id) { this.kamion_id = kamion_id; }

    public int getNarudba_id() { return narudba_id; }
    public void setNarudba_id(int narudba_id) { this.narudba_id = narudba_id; }

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

    public LocalDateTime getDatum_kreiranja() { return datum_kreiranja; }
    public void setDatum_kreiranja(LocalDateTime datum_kreiranja) { this.datum_kreiranja = datum_kreiranja; }
}
