USE AivenCloud;
ALTER TABLE tura MODIFY COLUMN narudzba_id INT NULL;
SELECT broj_ture, status, prijedeni_kilometri FROM tura WHERE broj_ture = 'TRIP-2026-002';
SELECT t.*, k.naziv_firme 
FROM tura t
JOIN narudzba n ON t.narudzba_id = n.id
JOIN klijent k ON n.klijent_id = k.id
WHERE t.aktivan = TRUE;
SET SQL_SAFE_UPDATES = 0;
DELETE FROM tura WHERE aktivan = FALSE;
SET SQL_SAFE_UPDATES = 1;
SELECT 
    t.id AS Tura_ID, 
    t.broj_ture, 
    t.lokacija_pocetka, 
    t.lokacija_kraja, 
    n.broj_narudzbe AS Povezana_Narudzba, 
    k.naziv_firme AS Firma
FROM tura t
LEFT JOIN narudzba n ON t.narudzba_id = n.id
LEFT JOIN klijent k ON n.klijent_id = k.id;