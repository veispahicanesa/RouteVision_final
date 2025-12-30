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
    private String marka_kamiona;
    private int trenutna_kilometraza;
    private String broj_vozacke_dozvole;
    private String tip_goriva;
    private String kategorija_dozvole;
    private LocalDate datum_zaposlenja;
    private double plata;
    private int broj_dovrsenih_tura;
    private boolean aktivan;
    private Integer kamionId; // Novo
    private Integer opremaId; // Novo
    private LocalDateTime datum_kreiranja;

    public Vozac() {}

    public Vozac(String ime, String prezime, String email, String lozinka,String broj_telefona,String marka_kamiona,int trenutna_kilometraza,String broj_vozacke_dozvole,String tip_goriva,String kategorija_dozvole,LocalDate datum_zaposlenja,double plata,int broj_dovrsenih_tura,boolean aktivan,Integer kamionId,Integer opremaId,LocalDateTime datum_kreiranja) {
        this.ime = ime;
        this.prezime = prezime;
        this.email = email;
        this.lozinka = lozinka;
        this.broj_telefona = broj_telefona;
        this.marka_kamiona = marka_kamiona;
        this.trenutna_kilometraza = trenutna_kilometraza;
        this.broj_vozacke_dozvole = broj_vozacke_dozvole;
        this.tip_goriva = tip_goriva;
        this.kategorija_dozvole = kategorija_dozvole;
        this.datum_zaposlenja =LocalDate.now();
        this.datum_zaposlenja = datum_zaposlenja; // Koristi parametar, ne LocalDate.now()
        this.plata = plata;
        this.aktivan = true;
        this.kamionId = kamionId;
        this.opremaId = opremaId;
        this.datum_kreiranja = LocalDateTime.now();
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

    public String getMarka_kamiona() { return marka_kamiona; }
    public void setMarka_kamiona(String marka_kamiona) { this.marka_kamiona = marka_kamiona; }

    public int getTrenutna_kilometraza() { return trenutna_kilometraza; }
    public void setTrenutna_kilometraza(int trenutna_kilometraza) { this.trenutna_kilometraza = trenutna_kilometraza; }

    public String getTip_goriva() { return tip_goriva; }
    public void setTip_goriva(String tip_goriva) { this.tip_goriva = tip_goriva; }

    public String getBroj_vozacke_dozvole() { return broj_vozacke_dozvole; }
    public void setBroj_vozacke_dozvole(String broj_vozacke_dozvole) { this.broj_vozacke_dozvole = broj_vozacke_dozvole; }

    public String getKategorija_dozvole() { return kategorija_dozvole; }
    public void setKategorija_dozvole(String kategorija_dozvole) { this.kategorija_dozvole = kategorija_dozvole; }

    public LocalDate getDatum_zaposlenja() { return datum_zaposlenja; }
    public void setDatum_zaposlenja(LocalDate datum_zaposlenja) { this.datum_zaposlenja = datum_zaposlenja; }

    public double getPlata() { return plata; }
    public void setPlata(double plata) { this.plata = plata; }

    public int getBroj_dovrsenih_tura() { return broj_dovrsenih_tura; }
    public void setBroj_dovrsenih_tura(int broj_dovrsenih_tura) { this.broj_dovrsenih_tura = broj_dovrsenih_tura; }


    public boolean isAktivan() { return aktivan; }
    public void setAktivan(boolean aktivan) { this.aktivan = aktivan; }

    public Integer getKamionId() { return kamionId; }
    public void setKamionId(Integer kamionId) { this.kamionId = kamionId; }

    public Integer getOpremaId() { return opremaId; }
    public void setOpremaId(Integer opremaId) { this.opremaId = opremaId; }

    public LocalDateTime getDatum_kreiranja() { return datum_kreiranja; }
    public void setDatum_kreiranja(LocalDateTime datum_kreiranja) { this.datum_kreiranja = datum_kreiranja; }

    @Override
    public String toString() {
        return (ime != null ? ime : "") + " " + (prezime != null ? prezime : "");
    }
}