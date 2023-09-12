import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportGenerator {
    public static void generateReport(Connection connection, String reportFileName) {
        String borrowedSql = "SELECT COUNT(*) AS borrowedCount FROM book WHERE status = 'borrowed'";
        String missingSql = "SELECT COUNT(*) AS missingCount FROM book WHERE status = 'missing'";
        String availableSql = "SELECT COUNT(*) AS availableCount FROM book WHERE status = 'available'";

        try (PreparedStatement borrowedStatement = connection.prepareStatement(borrowedSql);
             PreparedStatement missingStatement = connection.prepareStatement(missingSql);
             PreparedStatement availableStatement = connection.prepareStatement(availableSql)) {

            ResultSet borrowedResult = borrowedStatement.executeQuery();
            ResultSet missingResult = missingStatement.executeQuery();
            ResultSet availableResult = availableStatement.executeQuery();

            int borrowedCount = 0;
            int missingCount = 0;
            int availableCount = 0;

            if (borrowedResult.next()) {
                borrowedCount = borrowedResult.getInt("borrowedCount");
            }

            if (missingResult.next()) {
                missingCount = missingResult.getInt("missingCount");
            }

            if (availableResult.next()) {
                availableCount = availableResult.getInt("availableCount");
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFileName))) {
                writer.write("Library Book Report\n");
                writer.write("-------------------\n");
                writer.write("Borrowed Books: " + borrowedCount + "\n");
                writer.write("Missing Books: " + missingCount + "\n");
                writer.write("Available Books: " + availableCount + "\n");
            }

            System.out.println("Report generated successfully");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
