package id.uas.studentapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Paths;

/**
 * SQLite connector: database file will be at data/studentdb.db
 * No external DB server needed.
 */
public class DatabaseConnector {
    private static final String DB_FILE = "data/studentdb.db";
    private static final String URL = "jdbc:sqlite:" + DB_FILE;

    static {
        // ensure data dir exists
        try {
            java.nio.file.Path p = Paths.get("data");
            if (!java.nio.file.Files.exists(p)) java.nio.file.Files.createDirectories(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
