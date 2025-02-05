package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class inventory_manager extends JFrame {
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JButton addItemButton, removeItemButton, updateQuantityButton, backButton;
    private JTextField itemNameField, itemPriceField, itemQuantityField;
    private JComboBox<String> storeComboBox;

    public inventory_manager() {
        setTitle("Gestion de l'inventaire");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Définition des couleurs
        Color bgColor = new Color(30, 30, 30);
        Color btnColor = new Color(255, 140, 0);
        Color textColor = Color.WHITE;

        getContentPane().setBackground(bgColor);

        // Table des articles
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Prix", "Quantité", "Magasin"}, 0);
        inventoryTable = new JTable(tableModel);
        inventoryTable.setBackground(bgColor);
        inventoryTable.setForeground(textColor);
        add(new JScrollPane(inventoryTable), BorderLayout.CENTER);
        loadInventoryData();

        // Panel bas
        JPanel bottomPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        bottomPanel.setBackground(bgColor);

        itemNameField = new JTextField();
        itemPriceField = new JTextField();
        itemQuantityField = new JTextField();
        storeComboBox = new JComboBox<>();
        loadStores();

        addItemButton = createStyledButton("Ajouter Article", btnColor);
        removeItemButton = createStyledButton("Supprimer Article", btnColor);
        updateQuantityButton = createStyledButton("Mettre à jour Quantité", btnColor);
        backButton = createStyledButton("Retour", btnColor);

        bottomPanel.add(new JLabel("Nom: ")).setForeground(textColor);
        bottomPanel.add(itemNameField);
        bottomPanel.add(new JLabel("Prix: ")).setForeground(textColor);
        bottomPanel.add(itemPriceField);
        bottomPanel.add(new JLabel("Quantité: ")).setForeground(textColor);
        bottomPanel.add(itemQuantityField);
        bottomPanel.add(new JLabel("Magasin: ")).setForeground(textColor);
        bottomPanel.add(storeComboBox);
        bottomPanel.add(addItemButton);
        bottomPanel.add(removeItemButton);
        bottomPanel.add(updateQuantityButton);

        add(bottomPanel, BorderLayout.SOUTH);
        add(backButton, BorderLayout.NORTH);

        // Actions des boutons
        addItemButton.addActionListener(e -> addItem());
        removeItemButton.addActionListener(e -> removeItem());
        updateQuantityButton.addActionListener(e -> updateQuantity());
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

    private void loadInventoryData() {
        try (Connection conn = DriverManager.getConnection(config.link, config.login, config.password)) {
            String sql = "SELECT i.id, i.name, i.price, i.quantity, s.name FROM items i " +
                    "JOIN inventories inv ON i.inventory_id = inv.id " +
                    "JOIN store s ON inv.store_id = s.id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getDouble("price"), rs.getInt("quantity"), rs.getString("name")});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données.");
        }
    }

    private void loadStores() {
        try (Connection conn = DriverManager.getConnection(config.link, config.login, config.password)) {
            String sql = "SELECT id, name FROM store";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                storeComboBox.addItem(rs.getInt("id") + " - " + rs.getString("name"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des magasins.");
        }
    }

    private void addItem() {
        String name = itemNameField.getText().trim();
        String priceText = itemPriceField.getText().trim();
        String quantityText = itemQuantityField.getText().trim();
        String storeSelection = (String) storeComboBox.getSelectedItem();

        if (name.isEmpty() || priceText.isEmpty() || quantityText.isEmpty() || storeSelection == null) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);
            int storeId = Integer.parseInt(storeSelection.split(" - ")[0]);

            try (Connection conn = DriverManager.getConnection(config.link, config.login, config.password)) {
                String sql = "INSERT INTO items (name, price, quantity, inventory_id) VALUES (?, ?, ?, (SELECT id FROM inventories WHERE store_id = ?))";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setDouble(2, price);
                stmt.setInt(3, quantity);
                stmt.setInt(4, storeId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Article ajouté avec succès.");
                loadInventoryData();
            }
        } catch (NumberFormatException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de l'article.");
        }
    }

    private void removeItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un article à supprimer.");
            return;
        }

        int itemId = (int) tableModel.getValueAt(selectedRow, 0);
        try (Connection conn = DriverManager.getConnection(config.link, config.login, config.password)) {
            String sql = "DELETE FROM items WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, itemId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Article supprimé avec succès.");
            loadInventoryData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de l'article.");
        }
    }

    private void updateQuantity() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un article.");
            return;
        }

        int itemId = (int) tableModel.getValueAt(selectedRow, 0);
        String quantityText = itemQuantityField.getText().trim();

        if (quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer une quantité.");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityText);
            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, "La quantité ne peut pas être négative.");
                return;
            }

            try (Connection conn = DriverManager.getConnection(config.link, config.login, config.password)) {
                String sql = "UPDATE items SET quantity = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, quantity);
                stmt.setInt(2, itemId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Quantité mise à jour avec succès.");
                loadInventoryData();
            }
        } catch (NumberFormatException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour de la quantité.");
        }
    }

    private void goBack() {
        new home("");
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(inventory_manager::new);
    }
}
