package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
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

        // Définition des couleurs
        Color bgColor = new Color(30, 30, 30);
        Color btnColor = new Color(255, 140, 0);
        Color textColor = Color.WHITE;

        getContentPane().setBackground(bgColor);

        // Champs de saisie
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = createStyledButton("Se connecter", btnColor);
        registerButton = createStyledButton("Créer un compte", btnColor);

        JLabel emailLabel = new JLabel("    Mail:");
        emailLabel.setForeground(textColor);
        JLabel passwordLabel = new JLabel("    Mot de passe:");
        passwordLabel.setForeground(textColor);

        // Ajout des composants
        add(emailLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(registerButton);

        // Écouteurs d'événements
        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> register());

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

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DriverManager.getConnection(config.link, config.login, config.password)) {
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

    private void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(config.link, config.login, config.password)) {
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
