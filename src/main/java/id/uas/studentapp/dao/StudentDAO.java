package id.uas.studentapp.dao;

import id.uas.studentapp.model.Student;
import id.uas.studentapp.util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO implements IStudentDAO {

    @Override
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT student_id, name, email, major FROM students ORDER BY student_id";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Student(
                    rs.getInt("student_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("major")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean addStudent(Student s) {
        String sql = "INSERT INTO students(name, email, major) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getMajor());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateStudent(Student s) {
        String sql = "UPDATE students SET name = ?, email = ?, major = ? WHERE student_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getMajor());
            ps.setInt(4, s.getStudentId());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean swapStudentEmails(int idA, int idB) {
        String getEmailSql = "SELECT email FROM students WHERE student_id = ?";
        String updateEmailSql = "UPDATE students SET email = ? WHERE student_id = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement getStmt = conn.prepareStatement(getEmailSql);
            PreparedStatement updateStmt = conn.prepareStatement(updateEmailSql);

            getStmt.setInt(1, idA);
            ResultSet rsA = getStmt.executeQuery();
            String emailA = rsA.next() ? rsA.getString("email") : null;

            getStmt.setInt(1, idB);
            ResultSet rsB = getStmt.executeQuery();
            String emailB = rsB.next() ? rsB.getString("email") : null;

            if (emailA == null || emailB == null) {
                throw new SQLException("One or both students not found.");
            }

            updateStmt.setString(1, emailB);
            updateStmt.setInt(2, idA);
            updateStmt.executeUpdate();

            updateStmt.setString(1, emailA);
            updateStmt.setInt(2, idB);
            updateStmt.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Transaction is being rolled back.");
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}
