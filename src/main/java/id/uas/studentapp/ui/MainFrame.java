package id.uas.studentapp.ui;

import id.uas.studentapp.dao.IStudentDAO;
import id.uas.studentapp.model.Student;
import id.uas.studentapp.util.ReportGenerator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Refreshed Swing UI for Student App (SQLite)
 * Modernized layout and spacing
 */
public class MainFrame extends JFrame {
    private final IStudentDAO dao;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField tfId, tfName, tfEmail, tfMajor;
    private final File captureDir = new File("captures");

    public MainFrame(IStudentDAO dao) {
        this.dao = dao;
        setTitle("Student Data Management");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        if (!captureDir.exists()) captureDir.mkdirs();

        initUI();
        loadTable();
    }

    private void initUI() {
        // Background panel with image
        setContentPane(new JPanel(new BorderLayout()) {
            private BufferedImage bg;
            {
                try {
                    bg = ImageIO.read(getClass().getResource("/background.jpg"));
                } catch (Exception e) { bg = null; }
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null)
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                // overlay light transparent white
                g.setColor(new Color(255, 255, 255, 180));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        });

        // === LEFT FORM PANEL ===
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(Color.GRAY, 1, true),
                "Student Information",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        tfId = new JTextField(); tfId.setEditable(false);
        tfName = new JTextField();
        tfEmail = new JTextField();
        tfMajor = new JTextField();

        addField(formPanel, c, "ID:", tfId, 0);
        addField(formPanel, c, "Name:", tfName, 1);
        addField(formPanel, c, "Email:", tfEmail, 2);
        addField(formPanel, c, "Major:", tfMajor, 3);

        // === BUTTON PANEL ===
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnSwap = new JButton("Swap Emails");
        JButton btnReport = new JButton("Generate Report");
        JButton btnClear = new JButton("Clear Form");

        Font btnFont = new Font("Segoe UI", Font.PLAIN, 13);
        for (JButton b : new JButton[]{btnAdd, btnUpdate, btnDelete, btnSwap, btnReport, btnClear}) {
            b.setFont(btnFont);
            b.setFocusPainted(false);
            b.setBackground(new Color(230, 240, 250));
            b.setBorder(new LineBorder(new Color(180, 200, 220)));
        }

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnSwap);
        buttonPanel.add(btnReport);
        buttonPanel.add(btnClear);

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setOpaque(false);
        leftPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // === RIGHT TABLE PANEL ===
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Email", "Major"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionBackground(new Color(200, 220, 255));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int col) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    comp.setBackground(row % 2 == 0 ? new Color(245, 250, 255) : new Color(255, 255, 240));
                }
                return comp;
            }
        });

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(Color.GRAY, 1, true),
                "Student Records",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)
        ));

        add(leftPanel, BorderLayout.WEST);
        add(tableScroll, BorderLayout.CENTER);

        // === EVENT HANDLERS ===
        btnAdd.addActionListener(e -> {
            Student s = new Student();
            s.setName(tfName.getText().trim());
            s.setEmail(tfEmail.getText().trim());
            s.setMajor(tfMajor.getText().trim());
            if (s.getName().isEmpty() || s.getEmail().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Email required");
                return;
            }
            if (dao.addStudent(s)) {
                loadTable();
                capture("add_student");
                clearForm();
                JOptionPane.showMessageDialog(this, "Student added successfully!");
            } else JOptionPane.showMessageDialog(this, "Failed to add student");
        });

        btnUpdate.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected < 0) { JOptionPane.showMessageDialog(this, "Select a row first"); return; }
            int id = (Integer) tableModel.getValueAt(selected, 0);
            Student s = new Student(id, tfName.getText().trim(), tfEmail.getText().trim(), tfMajor.getText().trim());
            if (dao.updateStudent(s)) {
                loadTable();
                capture("update_student");
                JOptionPane.showMessageDialog(this, "Updated successfully!");
            } else JOptionPane.showMessageDialog(this, "Update failed");
        });

        btnDelete.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected < 0) { JOptionPane.showMessageDialog(this, "Select a row"); return; }
            int id = (Integer) tableModel.getValueAt(selected, 0);
            if (JOptionPane.showConfirmDialog(this, "Delete student ID " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION)
                    == JOptionPane.YES_OPTION) {
                if (dao.deleteStudent(id)) {
                    loadTable();
                    capture("delete_student");
                    JOptionPane.showMessageDialog(this, "Deleted successfully!");
                } else JOptionPane.showMessageDialog(this, "Delete failed");
            }
        });

        btnSwap.addActionListener(e -> {
            String a = JOptionPane.showInputDialog(this, "Enter Student ID A:");
            String b = JOptionPane.showInputDialog(this, "Enter Student ID B:");
            try {
                int idA = Integer.parseInt(a.trim());
                int idB = Integer.parseInt(b.trim());
                if (dao.swapStudentEmails(idA, idB)) {
                    loadTable();
                    capture("swap_emails");
                    JOptionPane.showMessageDialog(this, "Emails swapped successfully!");
                } else JOptionPane.showMessageDialog(this, "Swap failed");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid IDs");
            }
        });

        btnReport.addActionListener(e -> {
            String from = JOptionPane.showInputDialog(this, "From Student ID:");
            String to = JOptionPane.showInputDialog(this, "To Student ID:");
            try {
                int fromId = Integer.parseInt(from.trim());
                int toId = Integer.parseInt(to.trim());
                String out = "report/student_report.pdf";
                if (ReportGenerator.generateReport(fromId, toId, out)) {
                    capture("report_generated");
                    JOptionPane.showMessageDialog(this, "Report saved to " + out);
                } else JOptionPane.showMessageDialog(this, "Report generation failed");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input");
            }
        });

        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel >= 0) {
                tfId.setText(tableModel.getValueAt(sel, 0).toString());
                tfName.setText((String) tableModel.getValueAt(sel, 1));
                tfEmail.setText((String) tableModel.getValueAt(sel, 2));
                tfMajor.setText((String) tableModel.getValueAt(sel, 3));
            }
        });
    }

    private void addField(JPanel panel, GridBagConstraints c, String label, JTextField field, int row) {
        c.gridx = 0; c.gridy = row;
        panel.add(new JLabel(label), c);
        c.gridx = 1;
        panel.add(field, c);
    }

    private void loadTable() {
        java.util.List<Student> list = dao.getAllStudents();
        tableModel.setRowCount(0);
        for (Student s : list)
            tableModel.addRow(new Object[]{s.getStudentId(), s.getName(), s.getEmail(), s.getMajor()});
    }

    private void clearForm() {
        tfId.setText(""); tfName.setText(""); tfEmail.setText(""); tfMajor.setText("");
    }

    private void capture(String namePrefix) {
        try {
            Robot robot = new Robot();
            Rectangle rect = getBounds();
            Point loc = getLocationOnScreen();
            rect.setLocation(loc);
            BufferedImage img = robot.createScreenCapture(rect);
            DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String stamp = LocalDateTime.now().format(f);
            File out = new File(captureDir, namePrefix + "_" + stamp + ".png");
            ImageIO.write(img, "png", out);
            System.out.println("Captured: " + out.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
