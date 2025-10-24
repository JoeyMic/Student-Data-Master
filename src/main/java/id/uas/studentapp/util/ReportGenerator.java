package id.uas.studentapp.util;

import net.sf.jasperreports.engine.*;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class ReportGenerator {

    public static boolean generateReport(int fromId, int toId, String outputPdfPath) {
        try (Connection conn = DatabaseConnector.getConnection()) {

            // Load file .jrxml dari src/main/resources/report/
            InputStream reportStream = ReportGenerator.class.getResourceAsStream("/report/student_report.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // Parameter untuk query dan logo
            Map<String, Object> params = new HashMap<>();
            params.put("FROM_ID", fromId);
            params.put("TO_ID", toId);

            InputStream logo = ReportGenerator.class.getResourceAsStream("/logo.png");
            params.put("LOGO", logo);

            // Generate isi laporan
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, conn);

            // Export ke PDF
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPdfPath);

            System.out.println("✅ Report berhasil dibuat: " + outputPdfPath);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Gagal membuat report:");
            e.printStackTrace();
            return false;
        }
    }
}
