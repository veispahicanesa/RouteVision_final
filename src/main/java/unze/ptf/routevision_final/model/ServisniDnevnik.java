package unze.ptf.routevision_final.model;


import java.time.LocalDate;
import java.time.LocalDateTime;
/*
 * Model klasa ServisniDnevnik služi za evidenciju servisa kamiona.
 * Čuva podatke o datumu servisa, vrsti, kilometraži, troškovima
 * i dodatnim napomenama vezanim za održavanje vozila.
 */
public class ServisniDnevnik {
    private int id;
    private int kamion_id;
    private Integer vozac_id;
    private LocalDate datum_servisa;
    private String vrsta_servisa;
    private String opisServisa;
    private int km_na_servisu;
    private double troskovi;
    private String serviser_naziv;
    private String napomena;
    private String datoteka_path;
    private boolean aktivan;
    private LocalDateTime datum_kreiranja;

    public ServisniDnevnik() {}

    public ServisniDnevnik(int kamion_id, LocalDate datum_servisa) {
        this.kamion_id = kamion_id;
        this.datum_servisa = datum_servisa;
        this.aktivan = true;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getKamion_id() { return kamion_id; }
    public void setKamion_id(int kamion_id) { this.kamion_id = kamion_id; }

    public Integer getVozac_id() { return vozac_id; }
    public void setVozac_id(Integer vozac_id) { this.vozac_id = vozac_id; }

    public LocalDate getDatum_servisa() { return datum_servisa; }
    public void setDatum_servisa(LocalDate datum_servisa) { this.datum_servisa = datum_servisa; }

    public String getVrsta_servisa() { return vrsta_servisa; }
    public void setVrsta_servisa(String vrsta_servisa) { this.vrsta_servisa = vrsta_servisa; }

    public String getOpisServisa() { return opisServisa; }
    public void setOpisServisa(String opisServisa) { this.opisServisa = opisServisa; }

    public int getKm_na_servisu() { return km_na_servisu; }
    public void setKm_na_servisu(int km_na_servisu) { this.km_na_servisu = km_na_servisu; }

    public double getTroskovi() { return troskovi; }
    public void setTroskovi(double troskovi) { this.troskovi = troskovi; }

    public String getServiser_naziv() { return serviser_naziv; }
    public void setServiser_naziv(String serviser_naziv) { this.serviser_naziv = serviser_naziv; }

    public String getNapomena() { return napomena; }
    public void setNapomena(String napomena) { this.napomena = napomena; }

    public String getDatoteka_path() { return datoteka_path; }
    public void setDatoteka_path(String datoteka_path) { this.datoteka_path = datoteka_path; }

    public boolean isAktivan() { return aktivan; }
    public void setAktivan(boolean aktivan) { this.aktivan = aktivan; }

    public LocalDateTime getDatum_kreiranja() { return datum_kreiranja; }
    public void setDatum_kreiranja(LocalDateTime datum_kreiranja) { this.datum_kreiranja = datum_kreiranja; }
}
