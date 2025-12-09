package unze.ptf.routevision_final.model;

import java.time.LocalDateTime;

public class Klijent {
    private int id;
    private String naziv_firme;
    private String tip_klijenta;
    private String adresa;
    private String mjesto;
    private String postanskiBroj;
    private String drzava;
    private String kontakt_osoba;
    private String email;
    private String broj_telefona;
    private String broj_faksa;
    private String poreska_broj;
    private String naziv_banke;
    private String racun_broj;
    private double ukupna_narudena_kolicina;
    private double ukupno_placeno;
    private String aktivna_slika;
    private boolean aktivan;
    private LocalDateTime datum_kreiranja;

    public Klijent() {}

    public Klijent(String naziv_firme, String tip_klijenta) {
        this.naziv_firme = naziv_firme;
        this.tip_klijenta = tip_klijenta;
        this.aktivan = true;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNaziv_firme() { return naziv_firme; }
    public void setNaziv_firme(String naziv_firme) { this.naziv_firme = naziv_firme; }

    public String getTip_klijenta() { return tip_klijenta; }
    public void setTip_klijenta(String tip_klijenta) { this.tip_klijenta = tip_klijenta; }

    public String getAdresa() { return adresa; }
    public void setAdresa(String adresa) { this.adresa = adresa; }

    public String getMjesto() { return mjesto; }
    public void setMjesto(String mjesto) { this.mjesto = mjesto; }

    public String getPostanskiBroj() { return postanskiBroj; }
    public void setPostanskiBroj(String postanskiBroj) { this.postanskiBroj = postanskiBroj; }

    public String getDrzava() { return drzava; }
    public void setDrzava(String drzava) { this.drzava = drzava; }

    public String getKontakt_osoba() { return kontakt_osoba; }
    public void setKontakt_osoba(String kontakt_osoba) { this.kontakt_osoba = kontakt_osoba; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBroj_telefona() { return broj_telefona; }
    public void setBroj_telefona(String broj_telefona) { this.broj_telefona = broj_telefona; }

    public String getBroj_faksa() { return broj_faksa; }
    public void setBroj_faksa(String broj_faksa) { this.broj_faksa = broj_faksa; }

    public String getPoreska_broj() { return poreska_broj; }
    public void setPoreska_broj(String poreska_broj) { this.poreska_broj = poreska_broj; }

    public String getNaziv_banke() { return naziv_banke; }
    public void setNaziv_banke(String naziv_banke) { this.naziv_banke = naziv_banke; }

    public String getRacun_broj() { return racun_broj; }
    public void setRacun_broj(String racun_broj) { this.racun_broj = racun_broj; }

    public double getUkupna_narudena_kolicina() { return ukupna_narudena_kolicina; }
    public void setUkupna_narudena_kolicina(double ukupna_narudena_kolicina) { this.ukupna_narudena_kolicina = ukupna_narudena_kolicina; }

    public double getUkupno_placeno() { return ukupno_placeno; }
    public void setUkupno_placeno(double ukupno_placeno) { this.ukupno_placeno = ukupno_placeno; }

    public String getAktivna_slika() { return aktivna_slika; }
    public void setAktivna_slika(String aktivna_slika) { this.aktivna_slika = aktivna_slika; }

    public boolean isAktivan() { return aktivan; }
    public void setAktivan(boolean aktivan) { this.aktivan = aktivan; }

    public LocalDateTime getDatum_kreiranja() { return datum_kreiranja; }
    public void setDatum_kreiranja(LocalDateTime datum_kreiranja) { this.datum_kreiranja = datum_kreiranja; }
}
