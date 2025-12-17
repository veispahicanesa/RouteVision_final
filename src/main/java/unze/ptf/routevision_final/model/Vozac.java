package unze.ptf.routevision_final.model;



import java.time.LocalDate;
import java.time.LocalDateTime;

/*
 * Model klasa Vozac predstavlja jednog vozača u sistemu.
 * Čuva osnovne podatke o vozaču, kontakt, dozvolu, datum zaposlenja, plate i statistiku tura.
 * Takođe prati status aktivnosti i datum kreiranja zapisa.
 */
public class Vozac {
    private int id;
    private String ime;
    private String prezime;
    private String email;
    private String lozinka;
    private String broj_telefona;
    private String broj_vozacke_dozvole;
    private String kategorija_dozvole;
    private LocalDate datum_zaposlenja;
    private double plata;
    private int broj_dovrsenih_tura;
    private double stanje_racuna;
    private String aktivna_slika;
    private boolean aktivan;
    private LocalDateTime datum_kreiranja;

    public Vozac() {}

    public Vozac(String ime, String prezime, String email, String lozinka, String broj_vozacke_dozvole) {
        this.ime = ime;
        this.prezime = prezime;
        this.email = email;
        this.lozinka = lozinka;
        this.broj_vozacke_dozvole = broj_vozacke_dozvole;
        this.aktivan = true;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getIme() { return ime; }
    public void setIme(String ime) { this.ime = ime; }

    public String getPrezime() { return prezime; }
    public void setPrezime(String prezime) { this.prezime = prezime; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLozinka() { return lozinka; }
    public void setLozinka(String lozinka) { this.lozinka = lozinka; }

    public String getBroj_telefona() { return broj_telefona; }
    public void setBroj_telefona(String broj_telefona) { this.broj_telefona = broj_telefona; }

    public String getBroj_vozacke_dozvole() { return broj_vozacke_dozvole; }
    public void setBroj_vozacke_dozvole(String broj_vozacke_dozvole) { this.broj_vozacke_dozvole = broj_vozacke_dozvole; }

    public String getKategorija_dozvole() { return kategorija_dozvole; }
    public void setKategorija_dozvole(String kategorija_dozvole) { this.kategorija_dozvole = kategorija_dozvole; }

    public LocalDate getDatum_zaposlenja() { return datum_zaposlenja; }
    public void setDatum_zaposlenja(LocalDate datum_zaposlenja) { this.datum_zaposlenja = datum_zaposlenja; }

    public double getPlata() { return plata; }
    public void setPlata(double plata) { this.plata = plata; }

    public int getBroj_dovrsenih_tura() {return broj_dovrsenih_tura;}
    public void setBroj_dovrsenih_tura(int broj_dovrsenih_tura) { this.broj_dovrsenih_tura = broj_dovrsenih_tura; }

    public double getStanje_racuna() { return stanje_racuna; }
    public void setStanje_racuna(double stanje_racuna) { this.stanje_racuna = stanje_racuna; }

    public String getAktivna_slika() { return aktivna_slika; }
    public void setAktivna_slika(String aktivna_slika) { this.aktivna_slika = aktivna_slika; }

    public boolean isAktivan() { return aktivan; }
    public void setAktivan(boolean aktivan) { this.aktivan = aktivan; }

    public LocalDateTime getDatum_kreiranja() { return datum_kreiranja; }
    public void setDatum_kreiranja(LocalDateTime datum_kreiranja) { this.datum_kreiranja = datum_kreiranja; }
}