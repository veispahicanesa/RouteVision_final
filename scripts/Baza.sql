-- Create BazaPodataka Database and Schema
DROP DATABASE IF EXISTS BazaPodataka;
CREATE DATABASE BazaPodataka CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE BazaPodataka;

-- Admin Table
CREATE TABLE admin (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ime VARCHAR(100) NOT NULL,
    prezime VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    lozinka VARCHAR(255) NOT NULL,
    broj_telefona VARCHAR(20),
    datum_kreiranja TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    aktivan BOOLEAN DEFAULT TRUE
);

-- Vozač (Driver) Table
CREATE TABLE vozac (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ime VARCHAR(100) NOT NULL,
    prezime VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    lozinka VARCHAR(255) NOT NULL,
    broj_telefona VARCHAR(20),
    broj_vozacke_dozvole VARCHAR(50) UNIQUE,
    kategorija_dozvole VARCHAR(50),
    datum_zaposlenja DATE,
    plata DECIMAL(10,2),
    broj_dovrsenih_tura INT DEFAULT 0, -- AŽURIRANO
    stanje_racuna DECIMAL(10,2) DEFAULT 0,
    aktivna_slika VARCHAR(255),
    aktivan BOOLEAN DEFAULT TRUE,
    datum_kreiranja TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Kamion (Truck) Table
CREATE TABLE kamion (
    id INT PRIMARY KEY AUTO_INCREMENT,
    registarska_tablica VARCHAR(20) NOT NULL UNIQUE,
    marka VARCHAR(100),
    model VARCHAR(100),
    godina_proizvodnje INT,
    kapacitet_tone DECIMAL(8,2),
    vrsta_voza VARCHAR(100),
    stanje_kilometra INT DEFAULT 0,
    datum_registracije DATE,
    datum_zakljucnog_pregleda DATE,
    vozac_id INT,
    aktivna_slika VARCHAR(255),
    aktivan BOOLEAN DEFAULT TRUE,
    datum_kreiranja TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vozac_id) REFERENCES vozac(id) ON DELETE SET NULL
);

-- Oprema (Equipment) Table
CREATE TABLE oprema (
    id INT PRIMARY KEY AUTO_INCREMENT,
    naziv VARCHAR(150) NOT NULL,
    vrsta VARCHAR(100),
    kamion_id INT,
    kapacitet DECIMAL(10,2),
    stanje VARCHAR(50),
    datum_nabavke DATE,
    datum_zadnje_provjere DATE,
    napomena TEXT,
    aktivan BOOLEAN DEFAULT TRUE,
    datum_kreiranja TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (kamion_id) REFERENCES kamion(id) ON DELETE SET NULL
);

-- Klijent (Client) Table
CREATE TABLE klijent (
    id INT PRIMARY KEY AUTO_INCREMENT,
    naziv_firme VARCHAR(150) NOT NULL UNIQUE,
    tip_klijenta VARCHAR(50),
    adresa VARCHAR(255),
    mjesto VARCHAR(100),
    postanskiBroj VARCHAR(10),
    drzava VARCHAR(100),
    kontakt_osoba VARCHAR(150),
    email VARCHAR(150),
    broj_telefona VARCHAR(20),
    broj_faksa VARCHAR(20),
    poreska_broj VARCHAR(50) UNIQUE,
    naziv_banke VARCHAR(100),
    racun_broj VARCHAR(50),
    ukupna_narudena_kolicina DECIMAL(15,2) DEFAULT 0,
    ukupno_placeno DECIMAL(15,2) DEFAULT 0,
    aktivna_slika VARCHAR(255),
    aktivan BOOLEAN DEFAULT TRUE,
    datum_kreiranja TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Narudžba (Order) Table
CREATE TABLE narudba (
    id INT PRIMARY KEY AUTO_INCREMENT,
    broj_narudbe VARCHAR(50) NOT NULL UNIQUE,
    klijent_id INT NOT NULL,
    datum_narudbe DATE,
    datum_isporuke DATE,
    vrsta_robe VARCHAR(150),
    kolicina DECIMAL(10,2),
    jedinica_mjere VARCHAR(20),
    lokacija_preuzimanja VARCHAR(255),
    lokacija_dostave VARCHAR(255),
    napomena TEXT,
    status VARCHAR(50) DEFAULT 'Novoprijavljena',
    aktivan BOOLEAN DEFAULT TRUE,
    datum_kreiranja TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (klijent_id) REFERENCES klijent(id) ON DELETE CASCADE
);

-- Putovanje (Trip) Table
CREATE TABLE tura (
    id INT PRIMARY KEY AUTO_INCREMENT,
    broj_ture VARCHAR(50) NOT NULL UNIQUE,
    vozac_id INT NOT NULL,
    kamion_id INT NOT NULL,
    narudba_id INT NOT NULL,
    datum_pocetka DATE,
    vrijeme_pocetka TIME,
    datum_kraja DATE,
    vrijeme_kraja TIME,
    lokacija_pocetka VARCHAR(255),
    lokacija_kraja VARCHAR(255),
    prijedeni_kilometri INT,
    prosjecna_brzina INT,
    spent_fuel DECIMAL(8,2),
    fuel_used DECIMAL(8,2),
    napomena TEXT,
    status VARCHAR(50) DEFAULT 'U toku',
    aktivan BOOLEAN DEFAULT TRUE,
    datum_kreiranja TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vozac_id) REFERENCES vozac(id) ON DELETE CASCADE,
    FOREIGN KEY (kamion_id) REFERENCES kamion(id) ON DELETE CASCADE,
    FOREIGN KEY (narudba_id) REFERENCES narudba(id) ON DELETE CASCADE
);

-- Račun (Invoice) Table
CREATE TABLE fakture (
    id INT PRIMARY KEY AUTO_INCREMENT,
    broj_fakture VARCHAR(50) NOT NULL UNIQUE,
    tura_id INT NOT NULL,
    klijent_id INT NOT NULL,
    datum_izdavanja DATE,
    datum_dospjeća DATE,
    vrsta_usluge VARCHAR(150),
    cijena_po_km DECIMAL(8,2),
    broj_km INT,
    iznos_usluge DECIMAL(10,2),
    porez DECIMAL(10,2),
    ukupan_iznos DECIMAL(10,2),
    status_placanja VARCHAR(50) DEFAULT 'Neplačeno',
    nacin_placanja VARCHAR(50),
    datum_placanja DATE,
    napomena TEXT,
    datoteka_path VARCHAR(255),
    aktivan BOOLEAN DEFAULT TRUE,
    datum_kreiranja TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tura_id) REFERENCES tura(id) ON DELETE CASCADE,
    FOREIGN KEY (klijent_id) REFERENCES klijent(id) ON DELETE CASCADE
);

-- Servisni dnevnik (Service Log) Table
CREATE TABLE servisni_dnevnik (
    id INT PRIMARY KEY AUTO_INCREMENT,
    kamion_id INT NOT NULL,
    vozac_id INT,
    datum_servisa DATE,
    vrsta_servisa VARCHAR(150),
    opisServisa TEXT,
    km_na_servisu INT,
    troskovi DECIMAL(10,2),
    serviser_naziv VARCHAR(150),
    napomena TEXT,
    datoteka_path VARCHAR(255),
    aktivan BOOLEAN DEFAULT TRUE,
    datum_kreiranja TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (kamion_id) REFERENCES kamion(id) ON DELETE CASCADE,
    FOREIGN KEY (vozac_id) REFERENCES vozac(id) ON DELETE SET NULL
);
-- =========================
-- DODATE TABELE IZ ORIGINALNE BAZE
-- =========================

-- Status Vozača (Status_Vozaca)
DROP TABLE IF EXISTS status_vozaca;
CREATE TABLE status_vozaca (
 id INT AUTO_INCREMENT PRIMARY KEY,
 vozac_id INT NOT NULL,
 status VARCHAR(50), -- npr. 'aktivan', 'na pauzi', 'bolovanje', 'na turi'
 tura_id INT NULL, -- Povezano sa tura tabelom
 timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 napomena TEXT,
 FOREIGN KEY (vozac_id) REFERENCES vozac(id) ON DELETE CASCADE,
 FOREIGN KEY (tura_id) REFERENCES tura(id) ON DELETE SET NULL
);

-- Dokumenti (Dokumenti)
DROP TABLE IF EXISTS dokument;
CREATE TABLE dokument (
 id INT AUTO_INCREMENT PRIMARY KEY,
 vezano_za VARCHAR(50), -- npr. 'vozac', 'kamion', 'oprema', 'klijent', 'tura'
  vezani_id INT NULL,
  putanja_datoteke VARCHAR(255),
  uploadao_admin_id INT NULL,
  uploadao_vozac_id INT NULL,
  tip_dokumenta VARCHAR(50), -- npr. 'licenca', 'registracija', 'ugovor', 'faktura_kopija'
  datum_izdavanja DATE,
  datum_isteka DATE,
 datum_kreiranja TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY (uploadao_admin_id) REFERENCES admin(id) ON DELETE SET NULL,
 FOREIGN KEY (uploadao_vozac_id) REFERENCES vozac(id) ON DELETE SET NULL
);

-- Dnevnici Aktivnosti (Dnevnici_Aktivnosti)
DROP TABLE IF EXISTS dnevnik_aktivnosti;
CREATE TABLE dnevnik_aktivnosti (
 id INT AUTO_INCREMENT PRIMARY KEY,
 admin_id INT NULL,
 akcija VARCHAR(255), -- npr. 'Kreiran novi vozac', 'Izmijenjena faktura'
 entitet VARCHAR(50), -- npr. 'Vozac', 'Faktura', 'Kamion'
 entitet_id INT,
 timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY (admin_id) REFERENCES admin(id) ON DELETE SET NULL
);


-- Create Indexes for Performance
CREATE INDEX idx_vozac_email ON vozac(email);
CREATE INDEX idx_admin_email ON admin(email);
CREATE INDEX idx_klijent_naziv ON klijent(naziv_firme);
CREATE INDEX idx_tura_vozac ON tura(vozac_id); -- AŽURIRANO
CREATE INDEX idx_tura_kamion ON tura(kamion_id); -- AŽURIRANO
CREATE INDEX idx_narudba_klijent ON narudba(klijent_id);
CREATE INDEX idx_fakture_tura ON fakture(tura_id); -- AŽURIRANO
CREATE INDEX idx_evidencija_servisa_kamion ON servisni_dnevnik(kamion_id);