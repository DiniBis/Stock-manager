package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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

        // Définition des couleurs
        Color bgColor = new Color(30, 30, 30);
        Color btnColor = new Color(255, 140, 0);
        Color textColor = Color.WHITE;

        getContentPane().setBackground(bgColor);

        // Table des administrateurs
        tableModel = new DefaultTableModel(new String[]{"ID", "Email"}, 0);
        adminTable = new JTable(tableModel);
        adminTable.setBackground(bgColor);
        adminTable.setForeground(textColor);
        add(new JScrollPane(adminTable), BorderLayout.CENTER);
        loadAdminData();

        // Panel bas
        JPanel bottomPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        bottomPanel.setBackground(bgColor);

        emailField = new JTextField();
        addAdminButton = createStyledButton("Ajouter Admin", btnColor);
        removeAdminButton = createStyledButton("Supprimer Admin", btnColor);
        backButton = createStyledButton("Retour", btnColor);

        bottomPanel.add(new JLabel("Email: ")).setForeground(textColor);
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

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFont(new Font("Arial", Font.BOLD, 14));
        return button;
    }

    private void loadAdminData() {
        try (Connection conn = DriverManager.getConnection(config.link, config.login, config.password)) {
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

        try (Connection conn = DriverManager.getConnection(config.link, config.login, config.password)) {
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
        try (Connection conn = DriverManager.getConnection(config.link, config.login, config.password)) {
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
