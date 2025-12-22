package unze.ptf.routevision_final.model;


import java.time.LocalDate;
import java.time.LocalDateTime;
/*
 * Model klasa Oprema predstavlja dodatnu ili pomoćnu opremu u sistemu.
 * Oprema može biti vezana za određeni kamion ili postojati samostalno.
 * Koristi se za evidenciju stanja, kapaciteta i datuma provjera opreme.
 */

public class Oprema {
    private int id;
    private String naziv;
    private String vrsta;
    private Integer kamion_id;
    private double kapacitet;
    private String stanje;
    private LocalDate datum_nabavke;
    private LocalDate datum_zadnje_provjere;
    private String napomena;
    private boolean aktivan;
    private LocalDateTime datum_kreiranja;

    public Oprema() {}

    public Oprema(String naziv, String vrsta) {
        this.naziv = naziv;
        this.vrsta = vrsta;
        this.aktivan = true;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNaziv() { return naziv; }
    public void setNaziv(String naziv) { this.naziv = naziv; }

    public String getVrsta() { return vrsta; }
    public void setVrsta(String vrsta) { this.vrsta = vrsta; }

    public Integer getKamion_id() { return kamion_id; }
    public void setKamion_id(Integer kamion_id) { this.kamion_id = kamion_id; }

    public double getKapacitet() { return kapacitet; }
    public void setKapacitet(double kapacitet) { this.kapacitet = kapacitet; }

    public String getStanje() { return stanje; }
    public void setStanje(String stanje) { this.stanje = stanje; }

    public LocalDate getDatum_nabavke() { return datum_nabavke; }
    public void setDatum_nabavke(LocalDate datum_nabavke) { this.datum_nabavke = datum_nabavke; }

    public LocalDate getDatum_zadnje_provjere() { return datum_zadnje_provjere; }
    public void setDatum_zadnje_provjere(LocalDate datum_zadnje_provjere) { this.datum_zadnje_provjere = datum_zadnje_provjere; }

    public String getNapomena() { return napomena; }
    public void setNapomena(String napomena) { this.napomena = napomena; }

    public boolean isAktivan() { return aktivan; }
    public void setAktivan(boolean aktivan) { this.aktivan = aktivan; }

    public LocalDateTime getDatum_kreiranja() { return datum_kreiranja; }
    public void setDatum_kreiranja(LocalDateTime datum_kreiranja) { this.datum_kreiranja = datum_kreiranja; }
}