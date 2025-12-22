package unze.ptf.routevision_final.model;

import java.time.LocalDateTime;
//Ova klasa se koristi kao mapa između baze podataka i aplikacije
public class Admin {
    private int id;
    private String ime;
    private String prezime;
    private String email;
    private String lozinka;
    private String broj_telefona;
    private LocalDateTime datum_kreiranja;
    private boolean aktivan;

    public Admin() {}

    public Admin(String ime, String prezime, String email, String lozinka, String broj_telefona) {
        this.ime = ime;
        this.prezime = prezime;
        this.email = email;
        this.lozinka = lozinka;
        this.broj_telefona = broj_telefona;
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

    public LocalDateTime getDatum_kreiranja() { return datum_kreiranja; }
    public void setDatum_kreiranja(LocalDateTime datum_kreiranja) { this.datum_kreiranja = datum_kreiranja; }

    public boolean isAktivan() { return aktivan; }
    public void setAktivan(boolean aktivan) { this.aktivan = aktivan; }
}
