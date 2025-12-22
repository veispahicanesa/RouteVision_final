package unze.ptf.routevision_final.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Fakture {
    private int id;
    private String broj_fakture; // U bazi je 'broj_racuna'
    private int tura_id;         // U bazi je 'putovanje_id'
    private int klijent_id;
    private LocalDate datum_izdavanja;
    private LocalDate datum_dospjeca;
    private String vrsta_usluge;
    private double cijena_po_km;
    private int broj_km;
    private double iznos_usluge;
    private double porez;
    private double ukupan_iznos;
    private String status_placanja;
    private String nacin_placanja;
    private LocalDate datum_placanja;
    private String napomena;
    private String datoteka_path;
    private boolean aktivan;
    private LocalDateTime datum_kreiranja;

    public Fakture() {}

    // Getteri i Setteri
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getBroj_fakture() { return broj_fakture; }
    public void setBroj_fakture(String broj_fakture) { this.broj_fakture = broj_fakture; }
    public int getTura_id() { return tura_id; }
    public void setTura_id(int tura_id) { this.tura_id = tura_id; }
    public int getKlijent_id() { return klijent_id; }
    public void setKlijent_id(int klijent_id) { this.klijent_id = klijent_id; }
    public LocalDate getDatum_izdavanja() { return datum_izdavanja; }
    public void setDatum_izdavanja(LocalDate datum_izdavanja) { this.datum_izdavanja = datum_izdavanja; }
    public LocalDate getDatum_dospjeca() { return datum_dospjeca; }
    public void setDatum_dospjeca(LocalDate datum_dospjeca) { this.datum_dospjeca = datum_dospjeca; }
    public String getVrsta_usluge() { return vrsta_usluge; }
    public void setVrsta_usluge(String vrsta_usluge) { this.vrsta_usluge = vrsta_usluge; }
    public double getUkupan_iznos() { return ukupan_iznos; }
    public void setUkupan_iznos(double ukupan_iznos) { this.ukupan_iznos = ukupan_iznos; }
    public String getStatus_placanja() { return status_placanja; }
    public void setStatus_placanja(String status_placanja) { this.status_placanja = status_placanja; }
    public String getNacin_placanja() { return nacin_placanja; }
    public void setNacin_placanja(String nacin_placanja) { this.nacin_placanja = nacin_placanja; }
    public LocalDate getDatum_placanja() { return datum_placanja; }
    public void setDatum_placanja(LocalDate datum_placanja) { this.datum_placanja = datum_placanja; }
    public boolean isAktivan() { return aktivan; }
    public void setAktivan(boolean aktivan) { this.aktivan = aktivan; }
}