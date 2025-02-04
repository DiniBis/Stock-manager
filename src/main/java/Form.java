import javax.swing.*;

public class Form extends JFrame {
    private JTextField username;
    private JPasswordField password;
    private JButton new_account;
    private JButton login;
    private JLabel label;
    private JPanel start;
    private JTextArea label_password;
    private JTextArea label_username;

    public Form() {
        new JFrame();
        this.setTitle("iStore");
        this.setSize(500, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        // Set bounds (x, y, width, height)
        username.setBounds(150, 100, 200, 30);
        password.setBounds(150, 150, 200, 30);
        new_account.setBounds(150, 200, 200, 30);
        login.setBounds(150, 250, 200, 30);
        label_password.setBounds(150, 250, 200, 30);
        label_username.setBounds(150, 250, 200, 30);

        // Create a panel (optional, but not necessary if using null layout)
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0, 0, 500, 600);

        // Add components to the panel
        panel.add(username);
        panel.add(password);
        panel.add(new_account);
        panel.add(login);
        panel.add(label_password);
        panel.add(label_username);

        // Add panel to the frame
        this.add(panel);

        // Make frame visible
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new Form(); // Run the form
    }

}