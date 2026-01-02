package unze.ptf.routevision_final.service;

import unze.ptf.routevision_final.config.DatabaseConfig;
import unze.ptf.routevision_final.controller.SessionManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class ReportService {

    private final PdfService pdfService = new PdfService();

    // 1. OSNOVNA METODA (koja je falila)
    public void generateReport(String outputPath) throws Exception {
        String template = loadTemplate("report_template.html");
        // Smanjili smo broj kolona na one koje su najvjerovatnije ispravne
        StringBuilder sb = new StringBuilder("<table><thead><tr><th>ID</th><th>Relacija</th><th>Status</th></tr></thead><tbody>");

        String query = "SELECT * FROM tura ORDER BY id DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                sb.append("<tr>")
                        .append("<td>").append(rs.getInt("id")).append("</td>") // Koristimo 'id' jer ga svaka tabela ima
                        .append("<td>").append(rs.getString("lokacija_pocetka")).append(" - ").append(rs.getString("lokacija_kraja")).append("</td>")
                        .append("<td>").append(rs.getString("status")).append("</td>")
                        .append("</tr>");
            }
        }
        sb.append("</tbody></table>");

        String html = template
                .replace("{{NASLOV}}", "IZVJEŠTAJ TURA")
                .replace("{{PODNASLOV}}", "Pregled svih transportnih operacija")
                .replace("{{KORISNIK}}", "Administrator")
                .replace("{{VRIJEME}}", now())
                .replace("{{KPI_SEKCIJA}}", "")
                .replace("{{TABELA_SADRZAJ}}", sb.toString());

        pdfService.exportHtmlToPdf(html, outputPath);
    }

    // 2. FINANSIJSKI IZVJEŠTAJ (Sa imenom firme umjesto ID-a)
    public void generateFinancialReport(String outputPath) throws Exception {
        String template = loadTemplate("report_template.html");
        StringBuilder sb = new StringBuilder("<table><thead><tr><th>Faktura</th><th>ID Klijenta</th><th>Iznos</th><th>Status</th></tr></thead><tbody>");

        String query = "SELECT * FROM fakture WHERE aktivan = 1";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            double total = 0;
            while (rs.next()) {
                total += rs.getDouble("ukupan_iznos");
                sb.append("<tr>")
                        .append("<td>").append(rs.getString("broj_fakture")).append("</td>")
                        .append("<td>").append(rs.getInt("klijent_id")).append("</td>")
                        .append("<td>").append(rs.getDouble("ukupan_iznos")).append(" KM</td>")
                        .append("<td>").append(rs.getString("status_placanja")).append("</td>")
                        .append("</tr>");
            }
            sb.append("</tbody></table>");

            String html = template
                    .replace("{{NASLOV}}", "FINANSIJSKI IZVJEŠTAJ")
                    .replace("{{PODNASLOV}}", "Pregled prihoda po firmama")
                    .replace("{{KORISNIK}}", "Finansijski administrator")
                    .replace("{{VRIJEME}}", now())
                    .replace("{{KPI_SEKCIJA}}", "<h2>Ukupan promet: " + String.format("%.2f", total) + " KM</h2>")
                    .replace("{{TABELA_SADRZAJ}}", sb.toString());

            pdfService.exportHtmlToPdf(html, outputPath);
        }
    }

    // 3. IZVJEŠTAJ O SERVISIMA
    public void generateServiceReport(String outputPath) throws Exception {
        String template = loadTemplate("report_template.html");
        StringBuilder sb = new StringBuilder("<table><thead><tr><th>Kamion</th><th>Opis</th><th>Datum</th><th>Trošak</th></tr></thead><tbody>");

        String query = "SELECT s.*, k.registracijske_oznake FROM servisni_dnevnik s JOIN kamioni k ON s.kamion_id = k.id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                sb.append("<tr><td>").append(rs.getString("registracijske_oznake")).append("</td>")
                        .append("<td>").append(rs.getString("opis")).append("</td>")
                        .append("<td>").append(rs.getDate("datum_servisa")).append("</td>")
                        .append("<td>").append(rs.getDouble("trosak")).append(" KM</td></tr>");
            }
            sb.append("</tbody></table>");

            String html = template
                    .replace("{{NASLOV}}", "SERVISNI IZVJEŠTAJ")
                    .replace("{{PODNASLOV}}", "Pregled održavanja voznog parka")
                    .replace("{{KORISNIK}}", "Administrator")
                    .replace("{{VRIJEME}}", now())
                    .replace("{{KPI_SEKCIJA}}", "")
                    .replace("{{TABELA_SADRZAJ}}", sb.toString());

            pdfService.exportHtmlToPdf(html, outputPath);
        }
    }

    private String loadTemplate(String name) throws Exception {
        String path = "/unze/ptf/view/templates/" + name;
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) throw new RuntimeException("Resurs nije nađen: " + path);
            return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
        }
    }

    private String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }
}