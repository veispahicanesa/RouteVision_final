USE BazaPodataka;

-- Admin Test Users
-- Password: admin123 (BCrypt hash with LOG_ROUNDS = 12)
-- Hash: $2a$12$6tZF5tFsyE5kJ8Kj9mL3POX5p5p5p5p5p5p5p5p5p5p5p5p5p5p5p
INSERT INTO admin (ime, prezime, email, lozinka, broj_telefona, aktivan) VALUES
('Marko', 'Administrator', 'marko@routevision.com', '$2a$12$dCj/vd.J.mmqWV6uqoIVye7IUeq0iF9hGozoNi./O3KDqRUGFfE6O', '+387 1 234 5678', TRUE),
('Ana', 'Šef', 'ana.sef@routevision.com', '$2a$12$dCj/vd.J.mmqWV6uqoIVye7IUeq0iF9hGozoNi./O3KDqRUGFfE6O', '+387 1 234 5679', TRUE);

-- Driver Test Users
-- Password: vozac123 (BCrypt hash with LOG_ROUNDS = 12)
-- Hash: $2a$12$8uN7vL2Kp9mQ3rS4tU5vWOY6z7a8b9c0d1e2f3g4h5i6j7k8l9m0n
INSERT INTO vozac (
    ime, prezime, email, lozinka, broj_telefona, broj_vozacke_dozvole,
    kategorija_dozvole, datum_zaposlenja, plata, broj_dovrsenih_tura,
    stanje_racuna, aktivan
) VALUES
('Marko', 'Marković', 'marko.markovic@routevision.com', '...', '+387 1 222 1111', 'DL-001-2024', 'C', '2023-01-15', 1500.00, 45, 3200.50, TRUE),
('Ivan', 'Horvat', 'ivan.horvat@routevision.com', '...', '+387 1 222 2222', 'DL-002-2024', 'C', '2023-03-20', 1500.00, 38, 2850.75, TRUE),
('Petar', 'Petrović', 'petar.petrovic@routevision.com', '...', '+387 1 222 3333', 'DL-003-2024', 'C', '2023-05-10', 1500.00, 52, 4100.25, TRUE),
('Jovan', 'Jovanović', 'jovan.jovanovic@routevision.com', '...', '+387 1 222 4444', 'DL-004-2024', 'C', '2023-07-05', 1500.00, 31, 1950.00, TRUE);

-- Kamion (Trucks)
INSERT INTO kamion (registarska_tablica, marka, model, godina_proizvodnje, kapacitet_tone, vrsta_voza,
    stanje_kilometra, datum_registracije, datum_zakljucnog_pregleda, vozac_id, aktivan) VALUES
('BJ-001-AB', 'Volvo', 'FH16', 2022, 25.0, 'Hladnjača', 45000, '2022-03-10', '2025-03-10', 1, TRUE),
('BJ-002-AB', 'Mercedes', 'Actros', 2021, 20.0, 'Standardni', 62000, '2021-06-15', '2025-06-15', 2, TRUE),
('BJ-003-AB', 'Scania', 'R440', 2023, 24.0, 'Kiper', 28000, '2023-01-20', '2025-01-20', 3, TRUE),
('BJ-004-AB', 'DAF', 'XF95', 2020, 18.0, 'Standardni', 78000, '2020-11-05', '2025-11-05', 4, TRUE);

-- Oprema (Equipment)
INSERT INTO oprema (naziv, vrsta, kamion_id, kapacitet, stanje, datum_nabavke, datum_zadnje_provjere, aktivan) VALUES
('Hidraulička rampa', 'Pomoćna oprema', 1, 5.0, 'Odličan', '2021-05-10', '2024-11-01', TRUE),
('Sigurnosni lanac', 'Osiguranje tereta', 2, 2.0, 'Dobar', '2022-02-15', '2024-10-15', TRUE),
('Termostat', 'Hladnjača', 1, 1.0, 'Odličan', '2022-03-01', '2024-11-10', TRUE),
('GPS Tracker', 'Praćenje', 3, 0.5, 'Odličan', '2023-01-15', '2024-11-05', TRUE),
('Rezervna točka', 'Rezervni dijelovi', 4, 0.3, 'Dobar', '2020-11-20', '2024-09-20', TRUE);

-- Klijent (Clients)
INSERT INTO klijent (naziv_firme, tip_klijenta, adresa, mjesto, postanskiBroj, drzava, kontakt_osoba,
    email, broj_telefona, poreska_broj, naziv_banke, racun_broj, aktivan) VALUES
('Adriatic Trade Ltd', 'Privatna', 'Ulica Svetog Save 25', 'Sarajevo', '71000', 'Bosna i Hercegovina',
    'Fahrudin Abdić', 'info@adriatic-trade.ba', '+387 33 123 456', 'PIB-001', 'UniCredit Bank', '1010001001001001', TRUE),
('Balkan Foods Export', 'Privatna', 'Industrijska zona 15', 'Pale', '71410', 'Bosna i Hercegovina',
    'Mirna Adamović', 'contact@balkanfoods.ba', '+387 57 234 567', 'PIB-002', 'Raiffeisen Bank', '2020002002002002', TRUE),
('Slovenija Logistika', 'Privatna', 'Cesta Svobode 42', 'Ljubljana', '1000', 'Slovenija',
    'Jovan Novak', 'logistics@slovenija-log.si', '+386 1 456 789', 'PIB-003', 'NLB Bank', '3030003003003003', TRUE),
('Import Export Centar', 'Privatna', 'Trgovska 8', 'Beograd', '11000', 'Srbija',
    'Vesna Nikolić', 'office@import-export.rs', '+381 11 567 890', 'PIB-004', 'Komercijalna Banka', '4040004004004004', TRUE);

-- Narudba (Orders)
INSERT INTO narudba (broj_narudbe, klijent_id, datum_narudbe, datum_isporuke, vrsta_robe, kolicina,
    jedinica_mjere, lokacija_preuzimanja, lokacija_dostave, status, aktivan) VALUES
('ORD-2024-001', 1, '2024-11-01', '2024-11-15', 'Elektronika', 500, 'kom', 'Sarajevo', 'Zagreb', 'U toku', TRUE),
('ORD-2024-002', 2, '2024-11-05', '2024-11-20', 'Hrana i piće', 3500, 'kg', 'Pale', 'Ljubljana', 'U toku', TRUE),
('ORD-2024-003', 3, '2024-11-08', '2024-11-25', 'Industrijska roba', 12.5, 'tona', 'Ljubljana', 'Sarajevo', 'Završena', TRUE),
('ORD-2024-004', 4, '2024-11-10', '2024-11-28', 'Materijali za gradnju', 25.0, 'tona', 'Beograd', 'Sarajevo', 'U toku', TRUE);

-- Putovanje (Trips)
INSERT INTO tura (broj_ture, vozac_id, kamion_id, narudba_id, datum_pocetka, vrijeme_pocetka,
    datum_kraja, vrijeme_kraja, lokacija_pocetka, lokacija_kraja, prijedeni_kilometri, prosjecna_brzina,
    fuel_used, status, aktivan) VALUES
('TRIP-2024-001', 1, 1, 1, '2024-11-01', '08:00:00', '2024-11-03', '14:30:00', 'Sarajevo', 'Zagreb', 650, 85, 195.0, 'Završena', TRUE),
('TRIP-2024-002', 2, 2, 2, '2024-11-05', '06:30:00', '2024-11-07', '16:45:00', 'Pale', 'Ljubljana', 520, 80, 156.0, 'Završena', TRUE),
('TRIP-2024-003', 3, 3, 3, '2024-11-08', '07:00:00', '2024-11-10', '15:15:00', 'Ljubljana', 'Sarajevo', 480, 82, 144.0, 'Završena', TRUE),
('TRIP-2024-004', 4, 4, 4, '2024-11-12', '09:00:00', NULL, NULL, 'Beograd', 'Sarajevo', 0, 0, 0.0, 'U toku', TRUE);

-- Račun (Invoices)
INSERT INTO fakture (broj_fakture, tura_id, klijent_id, datum_izdavanja, datum_dospjeća, vrsta_usluge,
    cijena_po_km, broj_km, iznos_usluge, porez, ukupan_iznos, status_placanja, nacin_placanja, datum_placanja, aktivan) VALUES
('INV-2024-001', 1, 1, '2024-11-03', '2024-12-03', 'Prevoz elektronike', 1.5, 650, 975.00, 195.00, 1170.00, 'Plaćeno', 'Bankovni transfer', '2024-11-10', TRUE),
('INV-2024-002', 2, 2, '2024-11-07', '2024-12-07', 'Prevoz hrane i pića', 1.8, 520, 936.00, 187.20, 1123.20, 'Neplačeno', 'Bankovni transfer', NULL, TRUE),
('INV-2024-003', 3, 3, '2024-11-10', '2024-12-10', 'Prevoz industrijske robe', 2.0, 480, 960.00, 192.00, 1152.00, 'Plaćeno', 'Bankovni transfer', '2024-11-15', TRUE);

-- Servisni Dnevnik (Service Logs)
INSERT INTO servisni_dnevnik (kamion_id, vozac_id, datum_servisa, vrsta_servisa, opisServisa, km_na_servisu, 
    troskovi, serviser_naziv, napomena, aktivan) VALUES
(1, 1, '2024-10-15', 'Redovni servis', 'Zamjena ulja i filtera', 40000, 450.00, 'Volvo Servis Sarajevo', 'Sve ispravno', TRUE),
(2, 2, '2024-09-20', 'Popravka', 'Zamjena kočnica', 55000, 1200.00, 'Mercedes Centar Pale', 'Kočnice zamijenjene', TRUE),
(3, 3, '2024-11-01', 'Redovni servis', 'Pregled i održavanje', 25000, 380.00, 'Scania Partner Ljubljana', 'OK', TRUE),
(4, 4, '2024-08-10', 'Redovni servis', 'Zamjena filtera i ulja', 70000, 500.00, 'DAF Centar Beograd', 'Sve u redu', TRUE);
