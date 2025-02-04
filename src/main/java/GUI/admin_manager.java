package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class admin_manager extends JFrame {
    private JTable adminTable;
    private DefaultTableModel tableModel;
    private JButton addAdminButton, removeAdminButton, backButton;
    private JTextField emailField;

    public admin_manager() {
        setTitle("Gestion des administrateurs");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Table des administrateurs
        tableModel = new DefaultTableModel(new String[]{"ID", "Email"}, 0);
        adminTable = new JTable(tableModel);
        loadAdminData();
        add(new JScrollPane(adminTable), BorderLayout.CENTER);

        // Panel bas
        JPanel bottomPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        emailField = new JTextField();
        addAdminButton = new JButton("Ajouter Admin");
        removeAdminButton = new JButton("Supprimer Admin");
        backButton = new JButton("Retour");

        bottomPanel.add(new JLabel("Email: "));
        bottomPanel.add(emailField);
        bottomPanel.add(addAdminButton);
        bottomPanel.add(removeAdminButton);

        add(bottomPanel, BorderLayout.SOUTH);
        add(backButton, BorderLayout.NORTH);

        // Actions des boutons
        addAdminButton.addActionListener(e -> addAdmin());
        removeAdminButton.addActionListener(e -> removeAdmin());
        backButton.addActionListener(e -> goBack());

        setVisible(true);
    }

    private void loadAdminData() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/2java", "root", "")) {
            String sql = "SELECT id, email FROM users WHERE role = 'admin'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt("id"), rs.getString("email")});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données.");
        }
    }

    private void addAdmin() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un email.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/2java", "root", "")) {
            String sql = "UPDATE users SET role = 'admin' WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Administrateur ajouté avec succès.");
                loadAdminData();
            } else {
                JOptionPane.showMessageDialog(this, "Email non trouvé dans la base de données.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de l'administrateur.");
        }
    }

    private void removeAdmin() {
        int selectedRow = adminTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un administrateur à supprimer.");
            return;
        }

        int adminId = (int) tableModel.getValueAt(selectedRow, 0);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/2java", "root", "")) {
            String sql = "UPDATE users SET role = 'employee' WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, adminId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Administrateur supprimé avec succès.");
            loadAdminData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de l'administrateur.");
        }
    }

    private void goBack() {
        new home("");
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(admin_manager::new);
    }
}
