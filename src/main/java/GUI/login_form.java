package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class login_form extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    public login_form() {
        setTitle("Connexion");
        setSize(800, 160);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 30, 30));

        // Champs de saisie
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Se connecter");
        registerButton = new JButton("Créer un compte");

        // Ajout des composants
        add(new JLabel("    Mail:"));
        add(usernameField);
        add(new JLabel("    Mot de passe:"));
        add(passwordField);
        add(loginButton);
        add(registerButton);

        // Écouteur du bouton de connexion
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        // Écouteur du bouton d'inscription
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        setVisible(true);
    }

    // Méthode de connexion
    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/2java", "root", "")) {
            String sql = "SELECT password FROM users WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (BCrypt.checkpw(password, hashedPassword)) {
                    JOptionPane.showMessageDialog(this, "Connexion réussie !");
                    new home(username);
                    dispose();
                    
                } else {
                    JOptionPane.showMessageDialog(this, "Mot de passe incorrect.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Utilisateur introuvable.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données.");
        }
    }

    // Méthode d'inscription
    private void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydatabase", "root", "password")) {
            // Vérifier si l'email est whitelisted
            String checkWhitelist = "SELECT * FROM whitelist WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkWhitelist);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Votre email doit être whitelisted avant l'inscription.");
                return;
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            String sql = "INSERT INTO users (email, password, role) VALUES (?, ?, 'user')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Compte créé avec succès !");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'inscription.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(login_form::new);
    }
}
