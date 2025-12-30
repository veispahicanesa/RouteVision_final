package unze.ptf.routevision_final.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
 * Model klasa Kamion predstavlja vozilo (kamion) u sistemu.
 * Koristi se za evidenciju kamiona, tehničkih podataka,
 * dodijeljenog vozača i statusa vozila.
 */
public class Kamion {
    private int id;
    private String registarska_tablica;
    private String marka;
    private String model;
    private int godina_proizvodnje;
    private double kapacitet_tone;
    private int stanje_kilometra;
    private LocalDate datum_registracije;
    private LocalDate datum_zakljucnog_pregleda;
    private String ime_vozaca;
    private String prezime_vozaca;
    private Integer zaduzeni_vozac_id;
    private boolean aktivan;
    private LocalDateTime datum_kreiranja;

    // Prazan konstruktor
    public Kamion() {}

    // Konstruktor sa osnovnim parametrima
    public Kamion(String registarska_tablica, String marka, String model) {
        this.registarska_tablica = registarska_tablica;
        this.marka = marka;
        this.model = model;
        this.aktivan = true;
        this.datum_kreiranja = LocalDateTime.now();
        this.godina_proizvodnje = LocalDate.now().getYear(); // Postavlja trenutnu godinu kao default
    }

    // GETERI I SETERI
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRegistarska_tablica() { return registarska_tablica; }
    public void setRegistarska_tablica(String registarska_tablica) { this.registarska_tablica = registarska_tablica; }

    public String getMarka() { return marka; }
    public void setMarka(String marka) { this.marka = marka; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getGodina_proizvodnje() { return godina_proizvodnje; }
    public void setGodina_proizvodnje(int godina_proizvodnje) { this.godina_proizvodnje = godina_proizvodnje; }

    public double getKapacitet_tone() { return kapacitet_tone; }
    public void setKapacitet_tone(double kapacitet_tone) { this.kapacitet_tone = kapacitet_tone; }

    public int getStanje_kilometra() { return stanje_kilometra; }
    public void setStanje_kilometra(int stanje_kilometra) { this.stanje_kilometra = stanje_kilometra; }

    public LocalDate getDatum_registracije() { return datum_registracije; }
    public void setDatum_registracije(LocalDate datum_registracije) { this.datum_registracije = datum_registracije; }

    public LocalDate getDatum_zakljucnog_pregleda() { return datum_zakljucnog_pregleda; }
    public void setDatum_zakljucnog_pregleda(LocalDate datum_zakljucnog_pregleda) { this.datum_zakljucnog_pregleda = datum_zakljucnog_pregleda; }

    public String getIme_vozaca() { return ime_vozaca; }
    public void setIme_vozaca(String ime_vozaca) { this.ime_vozaca = ime_vozaca; }

    public String getPrezime_vozaca() { return prezime_vozaca; }
    public void setPrezime_vozaca(String prezime_vozaca) { this.prezime_vozaca = prezime_vozaca; }

    public Integer getZaduzeni_vozac_id() { return zaduzeni_vozac_id; }
    public void setZaduzeni_vozac_id(Integer zaduzeni_vozac_id) { this.zaduzeni_vozac_id = zaduzeni_vozac_id; }

    public boolean isAktivan() { return aktivan; }
    public void setAktivan(boolean aktivan) { this.aktivan = aktivan; }

    public LocalDateTime getDatum_kreiranja() { return datum_kreiranja; }
    public void setDatum_kreiranja(LocalDateTime datum_kreiranja) { this.datum_kreiranja = datum_kreiranja; }

    // Overridovan toString radi lakšeg prikaza u ComboBoxu
    @Override
    public String toString() {
        return marka + " " + model + " (" + registarska_tablica + ")";
    }
}