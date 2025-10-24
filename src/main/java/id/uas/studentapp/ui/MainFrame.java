package id.uas.studentapp.ui;

import id.uas.studentapp.dao.IStudentDAO;
import id.uas.studentapp.model.Student;
import id.uas.studentapp.util.ReportGenerator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

/**
 * Simple Swing UI implementing the required features.
 * Note: replace resources/logo.png and resources/background.jpg with real images.
 */
public class MainFrame extends JFrame {
    private final IStudentDAO dao;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField tfId, tfName, tfEmail, tfMajor;
    private final File captureDir = new File("captures");

    public MainFrame(IStudentDAO dao) {
        this.dao = dao;
        setTitle("Student Management App - UAS (SQLite)");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        if (!captureDir.exists()) captureDir.mkdirs();

        initUI();
        loadTable();
    }

    private void initUI() {
        setContentPane(new JPanel() {
            private BufferedImage bg;
            {
                try {
                    bg = ImageIO.read(getClass().getResource("/background.jpg"));
                } catch (Exception e) { bg = null; }
                setLayout(new BorderLayout());
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null) {
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                }
            }
        });

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);

        tfId = new JTextField(5); tfId.setEditable(false);
        tfName = new JTextField(15);
        tfEmail = new JTextField(15);
        tfMajor = new JTextField(10);

        c.gridx = 0; c.gridy = 0; form.add(new JLabel("ID"), c);
        c.gridx = 1; form.add(tfId, c);
        c.gridx = 2; form.add(new JLabel("Name"), c);
        c.gridx = 3; form.add(tfName, c);
        c.gridx = 4; form.add(new JLabel("Email"), c);
        c.gridx = 5; form.add(tfEmail, c);
        c.gridx = 6; form.add(new JLabel("Major"), c);
        c.gridx = 7; form.add(tfMajor, c);

        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnSwap = new JButton("Swap Emails");
        JButton btnReport = new JButton("Generate Report");

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(btnAdd); btnPanel.add(btnUpdate); btnPanel.add(btnDelete); btnPanel.add(btnSwap); btnPanel.add(btnReport);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(form, BorderLayout.CENTER);
        top.add(btnPanel, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"ID","Name","Email","Major"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Random rand = new Random();
            @Override
            public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int col) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    if (row % 2 == 0) comp.setBackground(new Color(245,245,255));
                    else comp.setBackground(new Color(255,250,240));
                } else comp.setBackground(new Color(200,220,255));
                return comp;
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        btnAdd.addActionListener(e -> {
            Student s = new Student();
            s.setName(tfName.getText().trim());
            s.setEmail(tfEmail.getText().trim());
            s.setMajor(tfMajor.getText().trim());
            if (s.getName().isEmpty() || s.getEmail().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and email required");
                return;
            }
            boolean ok = dao.addStudent(s);
            if (ok) {
                loadTable();
                capture("add_student");
                JOptionPane.showMessageDialog(this, "Student added successfully!");
                clearForm();
            } else JOptionPane.showMessageDialog(this, "Failed to add student");
        });

        btnUpdate.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected < 0) { JOptionPane.showMessageDialog(this, "Select a row"); return; }
            int id = (Integer)tableModel.getValueAt(selected, 0);
            Student s = new Student(id, tfName.getText().trim(), tfEmail.getText().trim(), tfMajor.getText().trim());
            boolean ok = dao.updateStudent(s);
            if (ok) {
                loadTable();
                capture("update_student");
                JOptionPane.showMessageDialog(this, "Updated!");
            } else JOptionPane.showMessageDialog(this, "Update failed");
        });

        btnDelete.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected < 0) { JOptionPane.showMessageDialog(this, "Select a row"); return; }
            int id = (Integer)tableModel.getValueAt(selected, 0);
            int conf = JOptionPane.showConfirmDialog(this, "Delete student id "+id+"?");
            if (conf == JOptionPane.YES_OPTION) {
                boolean ok = dao.deleteStudent(id);
                if (ok) {
                    loadTable();
                    capture("delete_student");
                    JOptionPane.showMessageDialog(this, "Deleted!");
                } else JOptionPane.showMessageDialog(this, "Delete failed");
            }
        });

        btnSwap.addActionListener(e -> {
            String a = JOptionPane.showInputDialog(this, "Enter Student ID A:");
            String b = JOptionPane.showInputDialog(this, "Enter Student ID B:");
            try {
                int idA = Integer.parseInt(a.trim());
                int idB = Integer.parseInt(b.trim());
                boolean ok = dao.swapStudentEmails(idA, idB);
                if (ok) {
                    loadTable();
                    capture("swap_emails");
                    JOptionPane.showMessageDialog(this, "Swap successful (transactional)");
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
                boolean ok = ReportGenerator.generateReport(fromId, toId, out);
                if (ok) {
                    capture("report_generated");
                    JOptionPane.showMessageDialog(this, "Report saved to " + out);
                } else JOptionPane.showMessageDialog(this, "Report generation failed");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input");
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel >= 0) {
                tfId.setText(tableModel.getValueAt(sel,0).toString());
                tfName.setText((String)tableModel.getValueAt(sel,1));
                tfEmail.setText((String)tableModel.getValueAt(sel,2));
                tfMajor.setText((String)tableModel.getValueAt(sel,3));
            }
        });
    }

    private void loadTable() {
        java.util.List<Student> list = dao.getAllStudents();
        tableModel.setRowCount(0);
        for (Student s : list) {
            tableModel.addRow(new Object[]{s.getStudentId(), s.getName(), s.getEmail(), s.getMajor()});
        }
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
            System.out.println("Captured: "+out.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
