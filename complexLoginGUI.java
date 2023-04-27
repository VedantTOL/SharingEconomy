import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginGUI {

    private JButton buyerButton;
    private JButton sellerButton;
    private JButton exitButton;
    private JFrame frame;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Welcome! Please choose if you're a buyer or seller!");
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        JButton buyerButton = new JButton("Buyer");
        JButton sellerButton = new JButton("Seller");
        JButton exitButton = new JButton("Exit");
        buttonPanel.add(buyerButton);
        buttonPanel.add(sellerButton);
        buttonPanel.add(exitButton);

        frame.getContentPane().add(buttonPanel, BorderLayout.CENTER);

        buyerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LoginFrame loginFrame = new LoginFrame("Buyer Login");
                loginFrame.addLoginForm();
                loginFrame.pack();
                loginFrame.setVisible(true);
            }
        });

        sellerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LoginFrame loginFrame = new LoginFrame("Seller Login");
                loginFrame.addLoginForm();
                loginFrame.pack();
                loginFrame.setVisible(true);
            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        frame.pack(); // resize the JFrame to fit its contents
        frame.setVisible(true);
    }

    private static class LoginFrame extends JFrame {
        private JTextField usernameField;
        private JPasswordField passwordField;
        private JButton submitButton;

        public LoginFrame(String title) {
            super(title);
            usernameField = new JTextField(20);
            passwordField = new JPasswordField(20);
            submitButton = new JButton("Submit");
            setLayout(new GridLayout(3, 2));
            add(new JLabel("Username:"));
            add(usernameField);
            add(new JLabel("Password:"));
            add(passwordField);
        }

        public void addLoginForm() {
            add(submitButton);
            submitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Perform login action here
                }
            });
        }
    }
}
