package unze.ptf.routevision_final.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class PdfService {

    /**
     * Metoda koja generiše PDF na osnovu HTML stringa
     */
    public void generateReport(String fileName, String htmlContent) {
        try (OutputStream os = new FileOutputStream(fileName)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.useFont(new File("C:/Windows/Fonts/arial.ttf"), "Arial");
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(os);
            builder.run();
            System.out.println("PDF uspješno kreiran: " + fileName);
        } catch (Exception e) {
            System.err.println("Greška pri generisanju PDF-a: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Metoda koja učitava HTML šablon iz resources foldera
     */
    public String loadHtmlTemplate(String templateName) throws IOException {
        // Putanja do foldera: src/main/resources/unze/ptf/routevision_final/templates/
        String path = "/templates/" + templateName;
        InputStream is = getClass().getResourceAsStream(path);

        if (is == null) {
            throw new IOException("Template nije pronađen na putanji: " + path);
        }

        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
}