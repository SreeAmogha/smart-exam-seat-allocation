package org.example.auth;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class AdminLoginFrame extends JFrame {
    private final AdminDAO dao = new AdminDAO();
    private final Runnable onLoginSuccess;
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);

    public AdminLoginFrame(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
        setTitle("Smart Exam System - Admin Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(440, 280);
        setLocationRelativeTo(null);
        setResizable(false);

        JLabel heading = new JLabel("ADMIN LOGIN", SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(22, 10, 12, 10));

        JPanel fields = new JPanel(new GridLayout(2, 2, 10, 12));
        fields.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        fields.add(new JLabel("Username"));
        fields.add(usernameField);
        fields.add(new JLabel("Password"));
        fields.add(passwordField);

        JButton loginButton = new JButton("Login");
        JButton setupButton = new JButton("Create First Admin");
        JButton exitButton = new JButton("Exit");
        loginButton.addActionListener(e -> login());
        setupButton.addActionListener(e -> createFirstAdmin());
        exitButton.addActionListener(e -> dispose());

        JPanel buttons = new JPanel();
        buttons.add(loginButton);
        buttons.add(setupButton);
        buttons.add(exitButton);

        add(heading, BorderLayout.NORTH);
        add(fields, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(loginButton);
    }

    private void login() {
        String username = usernameField.getText().trim();
        char[] password = passwordField.getPassword();
        if (username.isEmpty() || password.length == 0) {
            showError("Enter both username and password.");
            Arrays.fill(password, '\0');
            return;
        }

        try {
            String salt = getStoredSalt(username);
            if (salt == null) {
                showError("Invalid username or password.");
                return;
            }
            String hash = PasswordUtil.hash(password, salt);
            if (dao.verifyAdmin(username, salt, hash)) {
                dispose();
                onLoginSuccess.run();
            } else {
                showError("Invalid username or password.");
            }
        } catch (Exception ex) {
            showError("Login failed: " + ex.getMessage());
        } finally {
            Arrays.fill(password, '\0');
            passwordField.setText("");
        }
    }

    private String getStoredSalt(String username) throws Exception {
        // Fetch the salt through a dedicated DAO lookup.
        return dao.getSalt(username);
    }

    private void createFirstAdmin() {
        try {
            if (dao.hasAnyAdmin()) {
                showError("An admin account already exists. Ask the existing administrator to manage access.");
                return;
            }
            String username = usernameField.getText().trim();
            char[] password = passwordField.getPassword();
            if (username.length() < 4 || password.length < 10) {
                showError("Choose a username of at least 4 characters and a password of at least 10 characters.");
                Arrays.fill(password, '\0');
                return;
            }
            String confirm = JOptionPane.showInputDialog(this,
                    "Re-enter the password to confirm (input is masked by the password field only if entered on login).",
                    "Confirm Password", JOptionPane.QUESTION_MESSAGE);
            if (confirm == null || !new String(password).equals(confirm)) {
                showError("Passwords did not match.");
                Arrays.fill(password, '\0');
                return;
            }
            String salt = PasswordUtil.newSaltHex();
            String hash = PasswordUtil.hash(password, salt);
            Arrays.fill(password, '\0');
            if (dao.createAdmin(username, salt, hash)) {
                JOptionPane.showMessageDialog(this, "First admin account created. You can now log in.");
            } else {
                showError("Could not create admin account. The username may already exist.");
            }
        } catch (Exception ex) {
            showError("Admin setup failed: " + ex.getMessage());
        } finally {
            passwordField.setText("");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Admin Authentication",
                JOptionPane.ERROR_MESSAGE);
    }
}
