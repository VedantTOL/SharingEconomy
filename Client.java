import javax.swing.*;
import java.io.IOException;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

public class Client extends JComponent implements Runnable {

    private static DataInputStream dis;
    private static DataOutputStream dos;
    private static Socket socket;

    private JButton customer;
    private JButton seller;
    private JButton exit;
    private JFrame frame;



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Client());
    }

    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == customer) {
                // creates new JFrame for a customer to log in
                LoginOptionCustomer loginOptionCustomer = new LoginOptionCustomer();
                loginOptionCustomer.pack();
                loginOptionCustomer.setVisible(true);
                frame.dispose();
            } else if (e.getSource() == exit) {
                frame.dispose();
            } else if (e.getSource() == seller) {
                // creates new JFrame for a seller to log in
                LoginOptionSeller loginOptionSeller = new LoginOptionSeller();
                loginOptionSeller.pack();
                loginOptionSeller.setVisible(true);
                frame.dispose();
            }
        }
    };

    @Override
    public void run() {

        try {
            socket = new Socket("localhost", 4242);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream((socket.getOutputStream()));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Make sure the server is running before trying to connect!", "ERROR! Run Server!", JOptionPane.ERROR_MESSAGE);
            frame.dispose();
        }

        frame = new JFrame("Welcome! Please click the button according to your information!");
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        JPanel topPanel = topPanel();
        content.add(topPanel, BorderLayout.NORTH);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private JPanel topPanel() {
        customer = newButton("Buyer");
        seller = newButton("Seller");
        exit = newButton("Exit");
        JPanel top = new JPanel();
        top.add(customer);
        top.add(seller);
        top.add(exit);
        return top;
    }

    private JButton newButton(String text) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        return button;
    }

    private static class LoginOptionCustomer extends JFrame {
        private JButton createAccountButton;
        private JButton loginButton;

        public LoginOptionCustomer() {
            super("Login or Create Account");
            createAccountButton = new JButton("Create Account");
            loginButton = new JButton("Login");

            JPanel panel = new JPanel();
            panel.add(createAccountButton);
            panel.add(loginButton);
            add(panel);

            createAccountButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    BuyerLoginCredentials loginCredentials = new BuyerLoginCredentials();
                    loginCredentials.setVisible(true);
                    dispose();

                }
            });
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    BuyerLoginCredentials loginCredentials = new BuyerLoginCredentials();
                    loginCredentials.setVisible(true);
                    dispose();

                }
            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);
        }
    }

    private static class BuyerGUI extends JFrame {
        private JButton marketPlaceButton;
        private JButton shopBySellerButton;
        private User user;

        public BuyerGUI(User user) {
            super("Would you like to view the whole marketplace or shop by seller?");
            marketPlaceButton = new JButton("View the whole marketplace");
            shopBySellerButton = new JButton("Shop by seller");
            this.user = user;

            JPanel panel = new JPanel();
            panel.add(marketPlaceButton);
            panel.add(shopBySellerButton);
            add(panel);

            marketPlaceButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // asking for balance before creating buyer object
                    String bal = JOptionPane.showInputDialog(null, "What is your budget?",
                            "Budget Information", JOptionPane.QUESTION_MESSAGE);
                    
                    double balance = Double.parseDouble(bal);
                    
                    // creating buyer object from user data that was generated in the login frame
                    Buyer buyer = new Buyer(user.getUniqueIdentifier(), user.getEmail(), user.getPassword(), user.getName(),
                            user.getAge(), balance);
                    
                    
                    // maybe send prompt to server for the database and then read the database in:
//                    dos.writeUTF();
//                    dis.readUTF();
                    
                    // generate new marketplace JFrame here to show what is in the marketplace

                }
            });

            shopBySellerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // asking for balance before creating buyer object
                    String bal = JOptionPane.showInputDialog(null, "What is your budget?",
                            "Budget Information", JOptionPane.QUESTION_MESSAGE);

                    double balance = Double.parseDouble(bal);

                    // creating buyer object from user data that was generated in the login frame
                    Buyer buyer = new Buyer(user.getUniqueIdentifier(), user.getEmail(), user.getPassword(), user.getName(),
                            user.getAge(), balance);

                    // maybe send prompt to server for the database and then read the database in:
//                    dos.writeUTF();
//                    dis.readUTF();
                    
                    // generate new shop by seller Jframe here to allow the buyer to search for a seller
                    
                }
            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);

        }
    }
    
    private static class marketPlace extends JFrame {
        private JButton viewAllProductsButton;
        private JButton searchForProductsButton;
        private Buyer buyer;
        
        public marketPlace(Buyer buyer) {
            super("View all products or search for a specific product?");
            viewAllProductsButton = new JButton("View all products");
            searchForProductsButton = new JButton("Search for a specific product");
            this.buyer = buyer;

            JPanel panel = new JPanel();
            panel.add(viewAllProductsButton);
            panel.add(searchForProductsButton);
            add(panel);

            viewAllProductsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    
                    

                }
            });
        }
    }

    private static class LoginOptionSeller extends JFrame {
        private JButton createAccountButton;
        private JButton loginButton;

        public LoginOptionSeller() {
            super("Login or Create Account");
            createAccountButton = new JButton("Create Account");
            loginButton = new JButton("Login");

            JPanel panel = new JPanel();
            panel.add(createAccountButton);
            panel.add(loginButton);
            add(panel);

            createAccountButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SellerLoginCredentials loginCredentials = new SellerLoginCredentials();
                    loginCredentials.setVisible(true);
                    dispose();

                }
            });
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SellerLoginCredentials loginCredentials = new SellerLoginCredentials();
                    loginCredentials.setVisible(true);
                    dispose();

                }
            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);
        }
    }

    private static class BuyerLoginCredentials extends JFrame {
        private JTextField emailField;
        private JPasswordField passwordField;
        private JTextField nameField;
        private JTextField ageField;
        private JButton loginButton;

        public BuyerLoginCredentials() {
            super("Enter New Login Credentials");
            emailField = new JTextField(20);
            passwordField = new JPasswordField(20);
            nameField = new JTextField(20);
            ageField = new JTextField(3);
            loginButton = new JButton("Enter");
            JPanel panel = new JPanel();
            panel.add(new JLabel("Email:"));
            panel.add(emailField);
            panel.add(new JLabel("Password:"));
            panel.add(passwordField);
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Age:"));
            panel.add(ageField);
            panel.add(loginButton);
            add(panel);

            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    User user = null;
                    try {
                        dos.writeUTF("sendLogin\n");
                        // write email here
                        dos.writeUTF(emailField.getText());

                        // User current = dis.read(); //read user
                        user = new User();
                        // maybe pass the User as an argument to the BuyerGUI class, so we can use it in marketplace

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    BuyerGUI buyerGUI = new BuyerGUI(user);
                    buyerGUI.setVisible(true);
                    dispose();


                }
            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);
        }
    }

    private static class SellerLoginCredentials extends JFrame {
        private JTextField emailField;
        private JPasswordField passwordField;
        private JTextField nameField;
        private JTextField ageField;
        private JButton loginButton;

        public SellerLoginCredentials() {
            super("Enter New Login Credentials");
            emailField = new JTextField(20);
            passwordField = new JPasswordField(20);
            nameField = new JTextField(20);
            ageField = new JTextField(3);
            loginButton = new JButton("Enter");
            JPanel panel = new JPanel();
            panel.add(new JLabel("Email:"));
            panel.add(emailField);
            panel.add(new JLabel("Password:"));
            panel.add(passwordField);
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Age:"));
            panel.add(ageField);
            panel.add(loginButton);
            add(panel);

            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        dos.writeUTF("sendLogin\n");
                        // write email here
                        dos.writeUTF(emailField.getText());

                        // User current = dis.read(); //read user

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    // make this for seller:

//                    BuyerGUI buyerGUI = new BuyerGUI();
//                    buyerGUI.setVisible(true);
//                    dispose();


                }
            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);
        }
    }

}


