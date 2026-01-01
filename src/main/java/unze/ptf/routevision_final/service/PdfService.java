package unze.ptf.routevision_final.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class PdfService {

    public void exportHtmlToPdf(String htmlContent, String outputPath) throws Exception {
        try (OutputStream os = new FileOutputStream(outputPath)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            // Ključno za prikaz naših slova i CSS-a
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(os);
            builder.run();
        } catch (Exception e) {
            System.err.println("Greška u PdfService: " + e.getMessage());
            throw e;
        }
    }
}