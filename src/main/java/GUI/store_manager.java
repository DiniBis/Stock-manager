package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class store_manager extends JFrame {
    private JTable storeTable;
    private DefaultTableModel tableModel;
    private JButton addStoreButton, removeStoreButton, assignEmployeeButton, viewEmployeesButton, backButton;
    private JTextField storeNameField, employeeEmailField;

    public store_manager() {
        setTitle("Gestion des magasins");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Table des magasins
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom du magasin"}, 0);
        storeTable = new JTable(tableModel);
        loadStoreData();
        add(new JScrollPane(storeTable), BorderLayout.CENTER);

        // Panel bas
        JPanel bottomPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        storeNameField = new JTextField();
        employeeEmailField = new JTextField();
        addStoreButton = new JButton("Ajouter Magasin");
        removeStoreButton = new JButton("Supprimer Magasin");
        assignEmployeeButton = new JButton("Attribuer Employé");
        viewEmployeesButton = new JButton("Voir Employés");
        backButton = new JButton("Retour");

        bottomPanel.add(new JLabel("Nom du magasin: "));
        bottomPanel.add(storeNameField);
        bottomPanel.add(addStoreButton);
        bottomPanel.add(removeStoreButton);
        bottomPanel.add(new JLabel("Email Employé: "));
        bottomPanel.add(employeeEmailField);
        bottomPanel.add(assignEmployeeButton);
        bottomPanel.add(viewEmployeesButton);

        add(bottomPanel, BorderLayout.SOUTH);
        add(backButton, BorderLayout.NORTH);

        // Actions des boutons
        addStoreButton.addActionListener(e -> addStore());
        removeStoreButton.addActionListener(e -> removeStore());
        assignEmployeeButton.addActionListener(e -> assignEmployee());
        viewEmployeesButton.addActionListener(e -> viewEmployees());
        backButton.addActionListener(e -> goBack());

        setVisible(true);
    }

    private void loadStoreData() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/2java", "root", "")) {
            String sql = "SELECT id, name FROM stores";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt("id"), rs.getString("name")});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données.");
        }
    }

    private void addStore() {
        String storeName = storeNameField.getText().trim();
        if (storeName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un nom de magasin.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/2java", "root", "")) {
            String sql = "INSERT INTO stores (name) VALUES (?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, storeName);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Magasin ajouté avec succès.");
            loadStoreData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du magasin.");
        }
    }

    private void removeStore() {
        int selectedRow = storeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un magasin à supprimer.");
            return;
        }

        int storeId = (int) tableModel.getValueAt(selectedRow, 0);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/2java", "root", "")) {
            String sql = "DELETE FROM stores WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, storeId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Magasin supprimé avec succès.");
            loadStoreData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du magasin.");
        }
    }

    private void assignEmployee() {
        String email = employeeEmailField.getText().trim();
        int selectedRow = storeTable.getSelectedRow();
        if (email.isEmpty() || selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un email et sélectionner un magasin.");
            return;
        }

        int storeId = (int) tableModel.getValueAt(selectedRow, 0);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/2java", "root", "")) {
            String sql = "INSERT INTO store_employees (store_id, email) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, storeId);
            stmt.setString(2, email);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Employé attribué avec succès.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'attribution de l'employé.");
        }
    }

    private void viewEmployees() {
        JOptionPane.showMessageDialog(this, "Affichage des employés à implémenter.");
    }

    private void goBack() {
        new home("");
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(store_manager::new);
    }
}
