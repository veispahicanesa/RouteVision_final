package unze.ptf.routevision_final;

import unze.ptf.routevision_final.model.Admin;
import unze.ptf.routevision_final.model.Vozac;
import unze.ptf.routevision_final.repository.VozacDAO;
import unze.ptf.routevision_final.service.AuthService;

import java.util.Scanner;

public class Launcher {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        AuthService authService = new AuthService();

        System.out.println("=== ROUTEVISION ===");
        System.out.println("1. Admin");
        System.out.println("2. Vozač");
        System.out.print("Izaberite ulogu: ");
        int izbor = sc.nextInt();
        sc.nextLine();

        String uloga;
        if (izbor == 1) {
            uloga = "Admin";
        } else {
            uloga = "Vozač";
        }

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Lozinka: ");
        String lozinka = sc.nextLine();

        try {
            Object korisnik = authService.authenticate(email, lozinka, uloga);

            if (korisnik == null) {
                System.out.println("Pogrešni podaci za prijavu.");
                return;
            }

            // ===== ADMIN =====
            if (korisnik instanceof Admin) {
                System.out.println("Ulogovani ste kao ADMIN.");

                while (true) {
                    System.out.println("\n1. Dodaj vozača");
                    System.out.println("2. Izlaz");
                    System.out.print("Izbor: ");

                    int opcija = sc.nextInt();
                    sc.nextLine();

                    if (opcija == 1) {
                        System.out.print("Ime: ");
                        String ime = sc.nextLine();

                        System.out.print("Prezime: ");
                        String prezime = sc.nextLine();

                        System.out.print("Email: ");
                        String vEmail = sc.nextLine();

                        System.out.print("Lozinka: ");
                        String vLozinka = sc.nextLine();

                        System.out.print("Broj vozačke dozvole: ");
                        String dozvola = sc.nextLine();

                        Vozac vozac = new Vozac(
                                ime,
                                prezime,
                                vEmail,
                                vLozinka,
                                dozvola
                        );

                        VozacDAO vozacDAO = new VozacDAO();
                        vozacDAO.save(vozac);

                        System.out.println("Vozač je uspješno dodan u bazu.");

                    } else {
                        System.out.println("Izlaz iz sistema.");
                        break;
                    }
                }
            }

            // ===== VOZAČ =====
            if (korisnik instanceof Vozac) {
                Vozac vozac = (Vozac) korisnik;
                System.out.println("Ulogovani ste kao vozač.");
                System.out.println("Ime: " + vozac.getIme());
                System.out.println("Prezime: " + vozac.getPrezime());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
