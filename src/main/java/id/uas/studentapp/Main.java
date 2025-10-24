package id.uas.studentapp;

import javax.swing.SwingUtilities;
import id.uas.studentapp.dao.IStudentDAO;
import id.uas.studentapp.dao.StudentDAO;
import id.uas.studentapp.ui.MainFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            IStudentDAO dao = new StudentDAO();
            MainFrame frame = new MainFrame(dao);
            frame.setVisible(true);
        });
    }
}
